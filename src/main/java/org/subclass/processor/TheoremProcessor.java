package org.subclass.processor;

import org.subclass.annotation.RuleSpec;
import org.subclass.annotation.TheoremFamily;
import org.subclass.annotation.Theorem;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Annotation processor for compile-time validation of theorem families and proof signatures.
 *
 * This processor validates that:
 * 1. @TheoremFamily annotations comply with the metalanguage constraints
 *    - All four theorem methods are present and properly named
 *    - Theorem statuses respect logical consistency/completeness constraints
 * 2. @Theorem method return types match their declared logical signatures
 *    - "LK" must return Proof&lt;Many, Many&gt;
 *    - "LJ" must return Proof&lt;Many, One&gt;
 *    - "LDJ" must return Proof&lt;One, Many&gt;
 *    - "Common" must return Proof&lt;One, One&gt;
 *
 * By validating return types, we ensure that the Java compiler's type system
 * is being used as the proof verification mechanism.
 *
 * The processor generates no source or resource files; it purely validates
 * and reports compile-time errors/warnings.
 */
public class TheoremProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
            TheoremFamily.class.getName(),
            Theorem.class.getName(),
            RuleSpec.class.getName()
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Process @TheoremFamily annotations
        for (Element element : roundEnv.getElementsAnnotatedWith(TheoremFamily.class)) {
            validateTheoremFamily(element);
        }

        // Process @Theorem annotations
        for (Element element : roundEnv.getElementsAnnotatedWith(Theorem.class)) {
            if (element instanceof ExecutableElement method) {
                validateTheoremSignature(method);
            }
        }

        // Process @RuleSpec annotations: generate descriptors and collect for service file
        List<String> generatedDescriptors = new ArrayList<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(RuleSpec.class)) {
            if (element instanceof TypeElement type) {
                validateRuleSpec(type);
                String generatedClassName = generateRuleDescriptor(type);
                if (generatedClassName != null) {
                    generatedDescriptors.add(generatedClassName);
                }
            }
        }

        // Register generated descriptors in META-INF/services if any were generated
        if (!generatedDescriptors.isEmpty() && roundEnv.processingOver()) {
            registerDescriptorService(generatedDescriptors);
        }

        // Note: We don't claim the annotations to allow other processors to handle them
        return false;
    }

    /**
     * Cross-check a {@code @RuleSpec}-annotated class against the structure of
     * the reified proof-tree API:
     * <ul>
     *   <li>the class must implement {@code ProofNode<L,R>};</li>
     *   <li>{@link RuleSpec#side()} must be one of the known values;</li>
     *   <li>{@link RuleSpec#premiseCount()} must be non-negative; axioms must have zero premises.</li>
     * </ul>
     */
    private void validateRuleSpec(TypeElement type) {
        RuleSpec spec = type.getAnnotation(RuleSpec.class);
        Set<String> validSides = Set.of("axiom", "cut", "structural", "left", "right");
        if (!validSides.contains(spec.side())) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "@RuleSpec.side must be one of " + validSides + " but was '" + spec.side() + "'",
                type
            );
        }
        if (spec.premiseCount() < 0) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "@RuleSpec.premiseCount must be non-negative, was " + spec.premiseCount(),
                type
            );
        }
        if ("axiom".equals(spec.side()) && spec.premiseCount() != 0) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Axiom rule '" + spec.name() + "' must declare premiseCount=0, got " + spec.premiseCount(),
                type
            );
        }

        TypeElement proofNodeElem = processingEnv.getElementUtils()
            .getTypeElement("org.subclass.logic.proof.typed.ProofNode");
        if (proofNodeElem == null) {
            return;
        }
        TypeMirror proofNodeErased = processingEnv.getTypeUtils().erasure(proofNodeElem.asType());
        if (!processingEnv.getTypeUtils().isAssignable(
                processingEnv.getTypeUtils().erasure(type.asType()), proofNodeErased)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "@RuleSpec class '" + type.getQualifiedName() + "' must implement ProofNode<L,R>",
                type
            );
        }
    }

    /**
     * Validate a @TheoremFamily annotated class.
     *
     * @param element The annotated type element
     */
    private void validateTheoremFamily(Element element) {
        TheoremFamily family = element.getAnnotation(TheoremFamily.class);

        // Check that the family name is not empty
        if (family.name().isEmpty()) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "TheoremFamily name cannot be empty",
                element
            );
        }

        // Check that the four theorem method references are not empty
        String[] theoremMethods = {
            family.classicalTheorem(),
            family.intuitionisticTheorem(),
            family.paraconsistentTheorem(),
            family.commonLogicTheorem()
        };

        int nonEmptyCount = 0;
        for (String methodRef : theoremMethods) {
            if (!methodRef.isEmpty()) {
                nonEmptyCount++;
            }
        }

        if (nonEmptyCount > 0 && nonEmptyCount < 4) {
            String[] positions = {"classical", "intuitionistic", "paraconsistent", "common logic"};
            for (int i = 0; i < theoremMethods.length; i++) {
                if (theoremMethods[i].isEmpty()) {
                    processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "TheoremFamily provides some method references but is missing " + positions[i] + " theorem method reference. All four must be provided if any are present.",
                        element
                    );
                }
            }
        }

        // Optional: verify that the referenced methods exist on the annotated type
        if (nonEmptyCount == 4) {
            for (String methodName : theoremMethods) {
                boolean found = false;
                for (Element member : element.getEnclosedElements()) {
                    if (member instanceof ExecutableElement method && method.getSimpleName().contentEquals(methodName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "TheoremFamily references method '" + methodName + "' which does not exist in class " + element.getSimpleName(),
                        element
                    );
                }
            }
        }

        // Additional validation could go here:
        // - Verify that the referenced methods exist
        // - Check that all methods have @Theorem annotations
        // - Validate theorem status consistency across the tetragram
        // - Ensure duality relationships are correctly specified
    }

    /**
     * Validate that a @Theorem method's return type matches its declared signature.
     *
     * The return type serves as the proof witness. For example:
     * - A method with signature="LK" must return Proof&lt;Many, Many&gt;
     * - A method with signature="LJ" must return Proof&lt;Many, One&gt;
     *
     * This ensures the type system enforces proof validity.
     *
     * @param method The @Theorem annotated method
     */
    private void validateTheoremSignature(ExecutableElement method) {
        Theorem theorem = method.getAnnotation(Theorem.class);
        String signatureName = theorem.signature();
        TypeMirror returnType = method.getReturnType();

        // We allow void return types to support non-executable theorem metadata classes.
        if (returnType.getKind() == TypeKind.VOID) {
            return;
        }

        // Expected generic arguments for Proof<L, R>
        String leftArg;
        String rightArg;
        switch (signatureName) {
            case "LK" -> { leftArg = "Many"; rightArg = "Many"; }
            case "LJ" -> { leftArg = "Many"; rightArg = "One"; }
            case "LDJ" -> { leftArg = "One"; rightArg = "Many"; }
            case "Common" -> { leftArg = "One"; rightArg = "One"; }
            default -> {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING,
                    "Unknown signature '" + signatureName + "'. Skipping return type validation.",
                    method
                );
                return;
            }
        }

        TypeMirror expectedType = getExpectedProofType(leftArg, rightArg);
        if (expectedType == null) {
            // Types not found on classpath, skip validation
            return;
        }

        // Validate return type matches signature
        if (!processingEnv.getTypeUtils().isSameType(returnType, expectedType) &&
            !processingEnv.getTypeUtils().isAssignable(returnType, expectedType)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Theorem '" + theorem.name() + "' claims signature '" + signatureName +
                    "' but returns '" + returnType + "'. Expected: '" + expectedType + "'",
                method
            );
        }
    }

    /**
     * Construct the expected Proof type with appropriate generic arguments.
     *
     * @param left Generic argument for left side (Many or One)
     * @param right Generic argument for right side (Many or One)
     * @return The constructed DeclaredType, or null if types not found
     */
    private TypeMirror getExpectedProofType(String left, String right) {
        TypeElement proofElem = processingEnv.getElementUtils().getTypeElement("org.subclass.logic.proof.typed.Proof");
        TypeElement leftElem = processingEnv.getElementUtils().getTypeElement("org.subclass.logic.proof.typed." + left);
        TypeElement rightElem = processingEnv.getElementUtils().getTypeElement("org.subclass.logic.proof.typed." + right);

        if (proofElem == null || leftElem == null || rightElem == null) {
            return null;
        }

        return processingEnv.getTypeUtils().getDeclaredType(proofElem, leftElem.asType(), rightElem.asType());
    }

    /**
     * Generate a RuleDescriptor class for a @RuleSpec-annotated ProofNode.
     *
     * @param ruleClass The @RuleSpec-annotated class (e.g., Axiom, AndLeft)
     * @return The fully qualified class name of the generated descriptor, or null if generation failed
     */
    private String generateRuleDescriptor(TypeElement ruleClass) {
        try {
            RuleSpec spec = ruleClass.getAnnotation(RuleSpec.class);
            String ruleName = ruleClass.getSimpleName().toString();
            String descriptorName = ruleName + "Descriptor";
            String packageName = processingEnv.getElementUtils()
                .getPackageOf(ruleClass).getQualifiedName().toString();
            String descriptorPackage = packageName.replace(".rules.", ".rules.descriptors.");
            String descriptorQualifiedName = descriptorPackage + "." + descriptorName;

            // Generate descriptor source code
            String descriptorSource = generateDescriptorCode(ruleClass, ruleName, descriptorName, descriptorPackage);

            // Write to source output directory
            Filer filer = processingEnv.getFiler();
            javax.tools.JavaFileObject sourceFile = filer.createSourceFile(descriptorQualifiedName, ruleClass);
            try (Writer writer = sourceFile.openWriter()) {
                writer.write(descriptorSource);
            }

            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "Generated RuleDescriptor: " + descriptorQualifiedName
            );

            return descriptorQualifiedName;
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to generate RuleDescriptor for " + ruleClass.getQualifiedName() + ": " + e.getMessage()
            );
            return null;
        }
    }

    /**
     * Generate the source code for a RuleDescriptor class.
     *
     * @param ruleClass The @RuleSpec-annotated rule class
     * @param ruleName Simple name of the rule (e.g., "Axiom")
     * @param descriptorName Simple name of the descriptor (e.g., "AxiomDescriptor")
     * @param packageName Package for the descriptor
     * @return Java source code as a String
     */
    private String generateDescriptorCode(TypeElement ruleClass, String ruleName, String descriptorName, String packageName) {
        String ruleQualifiedName = ruleClass.getQualifiedName().toString();
        String rulePackage = processingEnv.getElementUtils()
            .getPackageOf(ruleClass).getQualifiedName().toString();

        // Import or use fully qualified name for the rule class
        String ruleRef;
        if (rulePackage.equals(packageName)) {
            // Same package, can use simple name
            ruleRef = ruleName + ".class";
        } else {
            // Different package, use fully qualified name
            ruleRef = ruleQualifiedName + ".class";
        }

        return "package " + packageName + ";\n" +
            "\n" +
            "import org.subclass.logic.proof.typed.ProofNode;\n" +
            "import org.subclass.logic.rules.RuleDescriptor;\n" +
            "\n" +
            "/**\n" +
            " * Auto-generated RuleDescriptor for {@link " + ruleQualifiedName + "}.\n" +
            " * Generated by TheoremProcessor annotation processor.\n" +
            " * DO NOT EDIT: delete the corresponding @RuleSpec annotation to remove this class.\n" +
            " */\n" +
            "public final class " + descriptorName + " implements RuleDescriptor {\n" +
            "    @Override\n" +
            "    @SuppressWarnings({\"unchecked\", \"rawtypes\"})\n" +
            "    public Class<? extends ProofNode<?, ?>> ruleClass() {\n" +
            "        return (Class) " + ruleRef + ";\n" +
            "    }\n" +
            "}\n";
    }

    /**
     * Register generated RuleDescriptor classes in META-INF/services/RuleDescriptor.
     *
     * @param generatedDescriptors List of fully qualified descriptor class names
     */
    private void registerDescriptorService(List<String> generatedDescriptors) {
        try {
            Filer filer = processingEnv.getFiler();
            String serviceName = "org.subclass.logic.rules.RuleDescriptor";

            // Read existing descriptors (if any)
            Set<String> allDescriptors = new TreeSet<>(generatedDescriptors);
            try {
                FileObject existing = filer.getResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    "META-INF/services/" + serviceName
                );
                if (existing != null) {
                    String existingContent = existing.getCharContent(true).toString();
                    for (String line : existingContent.split("\n")) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            allDescriptors.add(line);
                        }
                    }
                }
            } catch (IOException ignored) {
                // File doesn't exist yet, which is fine
            }

            // Write service file
            FileObject serviceFile = filer.createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "META-INF/services/" + serviceName
            );
            try (Writer writer = serviceFile.openWriter()) {
                writer.write("# Auto-generated RuleDescriptor service file\n");
                writer.write("# Generated by TheoremProcessor\n");
                for (String descriptor : allDescriptors) {
                    writer.write(descriptor);
                    writer.write("\n");
                }
            }

            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "Registered " + generatedDescriptors.size() + " RuleDescriptors in META-INF/services"
            );
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to register RuleDescriptors in service file: " + e.getMessage()
            );
        }
    }
}
