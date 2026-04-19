package org.subclass.processor;

import org.subclass.annotation.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Annotation processor that detects @LogicGraph and @GeneratedLogic annotations,
 * validates graph specifications, and generates logic implementations.
 *
 * <p>This processor runs at compile-time and generates:
 * <ul>
 *   <li>{LogicName}Signature.java - Logic definitions with connectives/rules</li>
 *   <li>{LogicName}Proof.java - Sealed proof interfaces</li>
 *   <li>{LogicName}Rules.java - Inference rule registries</li>
 * </ul>
 */
@SupportedAnnotationTypes({
    "org.subclass.annotation.LogicGraph",
    "org.subclass.annotation.GeneratedLogic"
})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class LogicGraphProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnv;
    private Messager messager;
    private Map<String, GraphSpecification> graphSpecs; // name -> spec

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.graphSpecs = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            // Process @LogicGraph annotations first
            for (Element element : roundEnv.getElementsAnnotatedWith(LogicGraph.class)) {
                processLogicGraph((TypeElement) element);
            }

            // Process @GeneratedLogic annotations
            for (Element element : roundEnv.getElementsAnnotatedWith(GeneratedLogic.class)) {
                processGeneratedLogic((TypeElement) element);
            }

            return true;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Error in LogicGraphProcessor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Process a @LogicGraph annotation and build specification model.
     */
    private void processLogicGraph(TypeElement element) {
        LogicGraph annotation = element.getAnnotation(LogicGraph.class);

        GraphSpecification.GraphType graphType = switch (annotation.graphType().toString()) {
            case "DIAMOND" -> GraphSpecification.GraphType.DIAMOND;
            case "K4" -> GraphSpecification.GraphType.K4;
            default -> throw new IllegalArgumentException("Unknown graph type");
        };

        GraphSpecification spec = new GraphSpecification(
            annotation.name(),
            graphType,
            annotation.baseSignature(),
            annotation.generatedPackage(),
            annotation.displayName(),
            annotation.reference()
        );

        // Add nodes
        for (GraphNode nodeAnn : annotation.nodes()) {
            spec.addNode(new GraphSpecification.NodeSpec(
                nodeAnn.id(),
                nodeAnn.name(),
                nodeAnn.displayName(),
                nodeAnn.consistent(),
                nodeAnn.complete(),
                nodeAnn.connectives(),
                nodeAnn.structuralRules(),
                nodeAnn.properties()
            ));
        }

        // Add edges
        for (GraphEdge edgeAnn : annotation.edges()) {
            spec.addEdge(new GraphSpecification.EdgeSpec(
                edgeAnn.from(),
                edgeAnn.to(),
                edgeAnn.morphismType(),
                edgeAnn.description()
            ));
        }

        // Validate
        try {
            spec.validate();
            graphSpecs.put(annotation.name(), spec);
            messager.printMessage(
                Diagnostic.Kind.NOTE,
                "Registered graph specification: " + annotation.name() + " (type: " + graphType + ")"
            );
        } catch (GraphSpecification.GraphValidationException e) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Invalid graph specification '" + annotation.name() + "': " + e.getMessage(),
                element
            );
        }
    }

    /**
     * Process a @GeneratedLogic annotation and generate logic implementation.
     */
    private void processGeneratedLogic(TypeElement element) {
        GeneratedLogic annotation = element.getAnnotation(GeneratedLogic.class);

        // Resolve referenced graph
        GraphSpecification spec = graphSpecs.get(annotation.graphReference());
        if (spec == null) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Graph reference not found: " + annotation.graphReference(),
                element
            );
            return;
        }

        // Resolve referenced node
        GraphSpecification.NodeSpec node = spec.node(annotation.nodeReference());
        if (node == null) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Node reference not found in graph: " + annotation.nodeReference(),
                element
            );
            return;
        }

        // Generate logic implementations
        try {
            generateLogicSignature(spec, node, annotation);
            // Note: Proof interfaces are not generated separately because Proof<L,R> is sealed
            // and only permits specific implementations (CommonProof, IntuitionisticProof, etc).
            // Generated logics use the standard Proof types with appropriate cardinality.
            // generateProofInterface(spec, node, annotation);
            generateRulesRegistry(spec, node, annotation);

            messager.printMessage(
                Diagnostic.Kind.NOTE,
                "Generated logic implementation for " + node.name() + " from graph " + spec.name()
            );
        } catch (Exception e) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to generate logic implementation: " + e.getMessage(),
                element
            );
            e.printStackTrace();
        }
    }

    /**
     * Generate {LogicName}Signature.java
     */
    private void generateLogicSignature(GraphSpecification spec,
                                       GraphSpecification.NodeSpec node,
                                       GeneratedLogic annotation) throws Exception {
        String packageName = spec.generatedPackage();
        String className = "Generated" + node.name() + "Signature";

        CodeGenerator gen = new CodeGenerator(packageName, className, "class");

        gen.addImport("org.subclass.logic.signature.LogicalSignature");
        gen.addImport("org.subclass.logic.signature.StructuralRule");
        gen.addImport("java.util.Set");

        gen.addJavadocLines(
            "Signature for the " + node.displayName() + " logic.",
            "Generated from graph specification: " + spec.name(),
            "Properties: consistent=" + node.consistent() + ", complete=" + node.complete()
        );

        gen.addBody("    /**\n");
        gen.addBody("     * Get the LogicalSignature for " + node.name() + ".\n");
        gen.addBody("     */\n");
        gen.addBody("    public static LogicalSignature getInstance() {\n");
        gen.addBody("        return new LogicalSignature.Builder(\"" + node.name() + "\", \"" + node.displayName() + "\")\n");

        // Add structural rules
        String[] structRules = annotation.structuralRules().isEmpty()
            ? node.structuralRules()
            : annotation.structuralRules().split(",");

        if (structRules.length > 0 && !structRules[0].isEmpty()) {
            gen.addBody("            .addStructuralRules(Set.of(");
            for (int i = 0; i < structRules.length; i++) {
                if (i > 0) gen.addBody(", ");
                String rule = structRules[i].trim().toUpperCase();
                gen.addBody("StructuralRule." + rule);
            }
            gen.addBody("))\n");
        }

        gen.addBody("            .functionallyComplete(" + annotation.functionallyComplete() + ")\n");
        gen.addBody("            .description(\"Generated from graph: " + spec.name() + "\")\n");
        gen.addBody("            .build();\n");
        gen.addBody("    }\n");

        // Write file
        writeSourceFile(packageName, className, gen.generate());
    }

    /**
     * Generate {LogicName}Proof.java - sealed proof interface
     */
    private void generateProofInterface(GraphSpecification spec,
                                       GraphSpecification.NodeSpec node,
                                       GeneratedLogic annotation) throws Exception {
        String packageName = spec.generatedPackage();
        String className = "Generated" + node.name() + "Proof";

        // Determine proof type from node properties
        TemplateResolver.ProofTemplate template =
            TemplateResolver.resolveProofTemplate(node.consistent(), node.complete());
        String[] cardinalities = TemplateResolver.resolveCardinalities(template);

        CodeGenerator gen = new CodeGenerator(packageName, className, "interface");

        gen.addImport("org.subclass.logic.proof.typed.Proof");
        gen.addImport("org.subclass.logic.proof.typed." + cardinalities[0]);
        gen.addImport("org.subclass.logic.proof.typed." + cardinalities[1]);

        gen.addJavadocLines(
            "Sealed proof interface for " + node.displayName() + " logic.",
            "Type: Proof<" + cardinalities[0] + ", " + cardinalities[1] + ">",
            "Properties: consistent=" + node.consistent() + ", complete=" + node.complete()
        );

        // Generate interface that extends Proof<L,R>
        gen.extendsClass("Proof<" + cardinalities[0] + ", " + cardinalities[1] + ">");

        writeSourceFile(packageName, className, gen.generate());
    }

    /**
     * Generate {LogicName}Rules.java - inference rules registry
     */
    private void generateRulesRegistry(GraphSpecification spec,
                                      GraphSpecification.NodeSpec node,
                                      GeneratedLogic annotation) throws Exception {
        String packageName = spec.generatedPackage();
        String className = "Generated" + node.name() + "Rules";

        CodeGenerator gen = new CodeGenerator(packageName, className, "class");

        gen.addImport("org.subclass.logic.rules.InferenceRules");
        gen.addImport("java.util.Set");

        gen.addJavadocLines(
            "Inference rule registry for " + node.displayName() + " logic.",
            "Lists all valid introduction and structural rules for this logic."
        );

        gen.addBody("    public static Set<String> getInferenceRules() {\n");
        gen.addBody("        return Set.of(\n");

        // Add basic rules
        String[] structRules = annotation.structuralRules().isEmpty()
            ? node.structuralRules()
            : annotation.structuralRules().split(",");

        for (int i = 0; i < structRules.length; i++) {
            String rule = structRules[i].trim();
            gen.addBody("            \"" + rule + "\"");
            if (i < structRules.length - 1) gen.addBody(",");
            gen.addBody("\n");
        }

        gen.addBody("        );\n");
        gen.addBody("    }\n");

        writeSourceFile(packageName, className, gen.generate());
    }

    /**
     * Write generated source file to output.
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
            // File may already exist; log warning
            messager.printMessage(Diagnostic.Kind.NOTE, "Could not write file, may already exist: " + className);
        }
    }
}
