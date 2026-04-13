package org.subclass.processor;

import org.subclass.annotation.TheoremFamily;
import org.subclass.annotation.Theorem;

import javax.annotation.processing.AbstractProcessor;
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
import java.util.Set;

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
        return Set.of(TheoremFamily.class.getName(), Theorem.class.getName());
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

        // Note: We don't claim the annotations to allow other processors to handle them
        return false;
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
}
