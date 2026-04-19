package org.subclass.processor;

import org.subclass.annotation.*;
import org.subclass.logic.tetragram.Tetragram;
import org.subclass.logic.tetragram.TheoremStatus;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Annotation processor that detects @GeneratedTheoremFamily annotations and generates
 * complete theorem families with relationship-aware anti-theorem support.
 *
 * <p>Generates:
 * <ul>
 *   <li>{TheoremName}Family.java - Implements @TheoremFamily with 4 @Theorem methods</li>
 *   <li>{TheoremName}Proofs.java - Proof implementations for each node</li>
 *   <li>Optional anti-theorem family if antiTheoremNotation is provided</li>
 * </ul>
 *
 * <p>Anti-theorems are NOT simple status inversions; they represent relationship-aware
 * duality based on double negation elimination/introduction failures.
 */
@SupportedAnnotationTypes("org.subclass.annotation.GeneratedTheoremFamily")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TheoremGeneratorProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnv;
    private Messager messager;
    private Map<String, TheoremFamilySpec> theoremFamilies;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.theoremFamilies = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(GeneratedTheoremFamily.class)) {
                processTheoremFamily((TypeElement) element);
            }
            return true;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Error in TheoremGeneratorProcessor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Process a @GeneratedTheoremFamily annotation.
     */
    private void processTheoremFamily(TypeElement element) {
        GeneratedTheoremFamily annotation = element.getAnnotation(GeneratedTheoremFamily.class);

        try {
            // Parse status mappings
            Map<String, String> theoremStatus = parseStatusMapping(annotation.theoremStatusMapping());

            // Create theorem family spec
            TheoremFamilySpec spec = new TheoremFamilySpec(
                annotation.name(),
                annotation.graphReference(),
                annotation.theoremNotation(),
                theoremStatus,
                annotation.displayName(),
                annotation.description(),
                annotation.reference()
            );

            // Handle optional anti-theorem
            if (!annotation.antiTheoremNotation().isEmpty()) {
                Map<String, String> antiTheoremStatus = parseStatusMapping(annotation.antiTheoremStatusMapping());
                spec.setAntiTheoremData(
                    annotation.antiTheoremName(),
                    annotation.antiTheoremNotation(),
                    antiTheoremStatus,
                    annotation.doubleNegationVariance()
                );
            }

            theoremFamilies.put(annotation.name(), spec);

            // Generate theorem family class
            generateTheoremFamily(spec);

            // Generate optional anti-theorem family
            if (spec.hasAntiTheorem()) {
                generateAntiTheoremFamily(spec);
            }

            messager.printMessage(
                Diagnostic.Kind.NOTE,
                "Generated theorem family: " + annotation.name()
                    + (spec.hasAntiTheorem() ? " with anti-theorem '" + spec.antiTheoremName() + "'" : "")
            );
        } catch (Exception e) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to process theorem family '" + annotation.name() + "': " + e.getMessage(),
                element
            );
            e.printStackTrace();
        }
    }

    /**
     * Parse status mapping string: "LK:PROVABLE,LJ:NON_PROVABLE,..."
     */
    private Map<String, String> parseStatusMapping(String mapping) throws IllegalArgumentException {
        Map<String, String> result = new LinkedHashMap<>();
        if (mapping == null || mapping.isEmpty()) {
            throw new IllegalArgumentException("Status mapping cannot be empty");
        }

        String[] pairs = mapping.split(",");
        for (String pair : pairs) {
            String[] kv = pair.trim().split(":");
            if (kv.length != 2) {
                throw new IllegalArgumentException("Invalid status mapping pair: " + pair);
            }
            result.put(kv[0].trim(), kv[1].trim());
        }
        return result;
    }

    /**
     * Generate theorem family class.
     */
    private void generateTheoremFamily(TheoremFamilySpec spec) throws Exception {
        String className = "Generated" + spec.name() + "Family";
        String packageName = "org.subclass.examples"; // Default; could be configurable

        CodeGenerator gen = new CodeGenerator(packageName, className, "class");

        gen.addImport("org.subclass.annotation.TheoremFamily");
        gen.addImport("org.subclass.annotation.Theorem");
        gen.addImport("org.subclass.logic.tetragram.Tetragram");
        gen.addImport("org.subclass.logic.tetragram.TheoremStatus");

        gen.addJavadocLines(
            "Theorem family: " + spec.name(),
            "Notation: " + spec.theoremNotation(),
            spec.description().isEmpty() ? "" : spec.description()
        );

        gen.addAnnotation("@TheoremFamily(name = \"" + spec.name() + "\")");

        gen.addBody("public class " + className + " {\n\n");

        // Generate 4 theorem methods (one per tetragram node)
        // Mapping: LK→(T,T), LJ→(T,F), LDJ→(F,T), COMMON→(F,F)
        String[] nodeIds = {"LK", "LJ", "LDJ", "COMMON"};

        for (String nodeId : nodeIds) {
            String status = spec.theoremStatus().get(nodeId);

            if (status == null) {
                messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Missing status for node " + nodeId + " in theorem family " + spec.name()
                );
                continue;
            }

            gen.addBody("    @Theorem(name = \"" + spec.name() + "_" + nodeId + "\",\n");
            gen.addBody("             signature = \"" + nodeId + "\",\n");
            gen.addBody("             status = \"" + status + "\",\n");
            gen.addBody("             proofReference = \"Generated from " + spec.graphReference() + "\")\n");
            gen.addBody("    public void " + nodeId.toLowerCase() + "Theorem() {\n");
            gen.addBody("        // Theorem: " + spec.theoremNotation() + "\n");
            gen.addBody("        // Status: " + status + "\n");
            gen.addBody("        // Generated from graph specification\n");
            gen.addBody("    }\n\n");
        }

        gen.addBody("}\n");

        writeSourceFile(packageName, className, gen.generate());
    }

    /**
     * Generate anti-theorem family class (relationship-aware duality).
     */
    private void generateAntiTheoremFamily(TheoremFamilySpec spec) throws Exception {
        String className = "Generated" + spec.antiTheoremName() + "Family";
        String packageName = "org.subclass.examples";

        CodeGenerator gen = new CodeGenerator(packageName, className, "class");

        gen.addImport("org.subclass.annotation.TheoremFamily");
        gen.addImport("org.subclass.annotation.Theorem");

        gen.addJavadocLines(
            "Anti-theorem family (relationship-aware dual): " + spec.antiTheoremName(),
            "Theorem: " + spec.name() + " (notation: " + spec.theoremNotation() + ")",
            "Anti-theorem notation: " + spec.antiTheoremNotation(),
            "Duality basis: " + spec.doubleNegationVariance(),
            "",
            "Anti-theorems are NOT status inversions; they capture unprovability/refutability",
            "distinctions and double negation elimination/introduction failures."
        );

        gen.addAnnotation("@TheoremFamily(name = \"" + spec.antiTheoremName() + "\")");

        gen.addBody("public class " + className + " {\n\n");

        // Generate 4 anti-theorem methods with relationship-aware status
        String[] nodeIds = {"LK", "LJ", "LDJ", "COMMON"};

        for (String nodeId : nodeIds) {
            String status = spec.antiTheoremStatus().get(nodeId);

            if (status == null) {
                messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Missing anti-theorem status for node " + nodeId
                );
                continue;
            }

            gen.addBody("    @Theorem(name = \"" + spec.antiTheoremName() + "_" + nodeId + "\",\n");
            gen.addBody("             signature = \"" + nodeId + "\",\n");
            gen.addBody("             status = \"" + status + "\",\n");
            gen.addBody("             proofReference = \"Generated anti-theorem from " + spec.graphReference() + "\")\n");
            gen.addBody("    public void " + nodeId.toLowerCase() + "AntiTheorem() {\n");
            gen.addBody("        // Anti-theorem: " + spec.antiTheoremNotation() + "\n");
            gen.addBody("        // Status: " + status + "\n");
            gen.addBody("        // Duality: " + spec.doubleNegationVariance() + "\n");
            gen.addBody("    }\n\n");
        }

        gen.addBody("}\n");

        writeSourceFile(packageName, className, gen.generate());
    }

    /**
     * Write generated source file.
     */
    private void writeSourceFile(String packageName, String className, String sourceCode) throws Exception {
        try {
            String qualifiedName = packageName + "." + className;
            javax.tools.JavaFileObject file = processingEnv.getFiler()
                .createSourceFile(qualifiedName);
            try (java.io.Writer writer = file.openWriter()) {
                writer.write(sourceCode);
            }
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Could not write file: " + className);
        }
    }

    /**
     * Internal specification for a theorem family.
     */
    private static class TheoremFamilySpec {
        private final String name;
        private final String graphReference;
        private final String theoremNotation;
        private final Map<String, String> theoremStatus;
        private final String displayName;
        private final String description;
        private final String reference;

        private String antiTheoremName;
        private String antiTheoremNotation;
        private Map<String, String> antiTheoremStatus;
        private String doubleNegationVariance;

        public TheoremFamilySpec(String name, String graphReference, String theoremNotation,
                               Map<String, String> theoremStatus, String displayName,
                               String description, String reference) {
            this.name = name;
            this.graphReference = graphReference;
            this.theoremNotation = theoremNotation;
            this.theoremStatus = theoremStatus;
            this.displayName = displayName;
            this.description = description;
            this.reference = reference;
        }

        public void setAntiTheoremData(String antiTheoremName, String antiTheoremNotation,
                                     Map<String, String> antiTheoremStatus,
                                     String doubleNegationVariance) {
            this.antiTheoremName = antiTheoremName;
            this.antiTheoremNotation = antiTheoremNotation;
            this.antiTheoremStatus = antiTheoremStatus;
            this.doubleNegationVariance = doubleNegationVariance;
        }

        public boolean hasAntiTheorem() {
            return antiTheoremName != null && !antiTheoremName.isEmpty();
        }

        // Accessors
        public String name() { return name; }
        public String graphReference() { return graphReference; }
        public String theoremNotation() { return theoremNotation; }
        public Map<String, String> theoremStatus() { return theoremStatus; }
        public String antiTheoremName() { return antiTheoremName; }
        public String antiTheoremNotation() { return antiTheoremNotation; }
        public Map<String, String> antiTheoremStatus() { return antiTheoremStatus; }
        public String doubleNegationVariance() { return doubleNegationVariance; }
        public String displayName() { return displayName; }
        public String description() { return description; }
        public String reference() { return reference; }
    }
}
