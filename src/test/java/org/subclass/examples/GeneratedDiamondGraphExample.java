package org.subclass.examples;

import org.subclass.annotation.*;

/**
 * Example: Annotation-driven generation of a diamond graph logic structure.
 *
 * <p>This class demonstrates the new @LogicGraph annotation that completely specifies
 * a 4-node diamond graph structure. The LogicGraphProcessor will automatically generate:
 * <ul>
 *   <li>GeneratedLKSignature.java - Classical logic signature</li>
 *   <li>GeneratedLJSignature.java - Intuitionistic logic signature</li>
 *   <li>GeneratedLDJSignature.java - Paraconsistent dual logic signature</li>
 *   <li>GeneratedCommonSignature.java - Common logic intersection signature</li>
 *   <li>Corresponding proof interfaces and rule registries for each</li>
 * </ul>
 *
 * <p><b>Graph Structure (Diamond):</b>
 * <pre>
 *          LK (T,T) - Classical: Consistent & Complete
 *         /  \
 *       /      \
 *     LJ        LDJ - Paraconsistent: Inconsistent & Complete
 *   (T,F)      (F,T)
 *     \        /
 *       \    /
 *       Common (F,F) - Both restricted
 * </pre>
 *
 * <p>The diamond has 5 extension morphisms (logical extensions) forming a lattice structure.
 * This is the foundational tetragram used for LEM and other theorems.
 */
@LogicGraph(
    name = "DiamondGraphSpec",
    graphType = LogicGraph.GraphType.DIAMOND,
    baseSignature = "LK",
    generatedPackage = "org.subclass.logic.generated",
    displayName = "Diamond Graph of Logics",
    reference = "Diamond lattice of mutually-definable logical systems",
    nodes = {
        @GraphNode(
            id = "LK",
            name = "LK",
            displayName = "Classical Sequent Calculus",
            consistent = true,
            complete = true,
            connectives = {"AND", "OR", "NOT", "IMPLIES"},
            structuralRules = {"WEAKENING", "CONTRACTION", "EXCHANGE"}
        ),
        @GraphNode(
            id = "LJ",
            name = "LJ",
            displayName = "Intuitionistic Logic",
            consistent = true,
            complete = false,
            connectives = {"AND", "OR", "NOT", "IMPLIES"},
            structuralRules = {"WEAKENING", "CONTRACTION", "EXCHANGE"}
        ),
        @GraphNode(
            id = "LDJ",
            name = "LDJ",
            displayName = "Paraconsistent Dual",
            consistent = false,
            complete = true,
            connectives = {"AND", "OR", "NOT", "IMPLIES"},
            structuralRules = {"WEAKENING", "CONTRACTION", "EXCHANGE"}
        ),
        @GraphNode(
            id = "COMMON",
            name = "COMMON",
            displayName = "Common Logic Intersection",
            consistent = false,
            complete = false,
            connectives = {"NOT"},
            structuralRules = {}
        )
    },
    edges = {
        // Extension morphisms forming the diamond lattice
        @GraphEdge(from = "LK", to = "LJ", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Restrict completeness: drop right-side multiple formula rule"),
        @GraphEdge(from = "LK", to = "LDJ", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Restrict consistency: allow left-side contradictions"),
        @GraphEdge(from = "LJ", to = "COMMON", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Restrict consistency further: contract connectives"),
        @GraphEdge(from = "LDJ", to = "COMMON", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Restrict completeness further: restrict connectives"),
        @GraphEdge(from = "LK", to = "COMMON", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Direct extension to intersection logic")
    }
)
@GeneratedLogic(
    graphReference = "DiamondGraphSpec",
    nodeReference = "LK",
    functionallyComplete = true,
    description = "Generated classical logic from diamond graph"
)
public class GeneratedDiamondGraphExample {

    /**
     * Demonstrates that the annotation is purely declarative.
     * Code generation happens automatically during compilation.
     */
    public static void main(String[] args) {
        System.out.println("Diamond Graph Specification:");
        System.out.println("- Graph type: DIAMOND (4 nodes, 5 extension morphisms)");
        System.out.println("- Base signature: LK (Classical Sequent Calculus)");
        System.out.println("- Generated package: org.subclass.logic.generated");
        System.out.println();
        System.out.println("Generated artifacts:");
        System.out.println("  - GeneratedLKSignature.java");
        System.out.println("  - GeneratedLJSignature.java");
        System.out.println("  - GeneratedLDJSignature.java");
        System.out.println("  - GeneratedCommonSignature.java");
        System.out.println("  - Corresponding Proof interfaces and Rules registries");
    }
}
