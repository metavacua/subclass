package org.subclass.examples;

import org.subclass.annotation.*;

/**
 * Example: Annotation-driven generation of a K4 complete graph structure.
 *
 * <p>K4 is the complete graph on 4 nodes with a distinguished duality morphism.
 * It has:
 * <ul>
 *   <li>5 EXTENSION morphisms (standard diamond lattice)</li>
 *   <li>1 DUALITY morphism (external negation relation between calculi)</li>
 * </ul>
 *
 * <p>This structure represents mutually-definable calculi where one additional edge
 * (the duality morphism) indicates a relationship based on external negation.
 *
 * <p><b>Graph Structure (K4):</b>
 * <pre>
 *          LK (T,T)
 *         /  \ ╱ \
 *       /    ╱ D  \     D = Duality morphism
 *     LJ    ╱      LDJ
 *   (T,F) ╱        (F,T)
 *     \  ╱        /
 *       ╲      /
 *         \   /
 *       Common (F,F)
 * </pre>
 *
 * <p>The duality morphism (shown with ╱) connects LK and LDJ, representing the
 * external negation relation between classical and paraconsistent logic.
 *
 * <p><b>K4 Semantics:</b>
 * In K4, theorems and anti-theorems map to status values that respect both
 * the lattice structure (extension morphisms) and the duality relationship.
 * The duality morphism indicates that certain proof-theoretic properties
 * of LK correspond to negation-inverted properties of LDJ.
 */
@LogicGraph(
    name = "K4CompleteGraphSpec",
    graphType = LogicGraph.GraphType.K4,
    baseSignature = "LK",
    generatedPackage = "org.subclass.logic.generated",
    displayName = "K4 Complete Graph (4 Logics with Duality)",
    reference = "Complete graph on 4 vertices with duality morphism for mutual definability",
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
        // Five EXTENSION morphisms (diamond lattice)
        @GraphEdge(from = "LK", to = "LJ", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Restrict completeness"),
        @GraphEdge(from = "LK", to = "LDJ", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Restrict consistency"),
        @GraphEdge(from = "LJ", to = "COMMON", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Further restrict consistency"),
        @GraphEdge(from = "LDJ", to = "COMMON", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Further restrict completeness"),
        @GraphEdge(from = "LK", to = "COMMON", morphismType = GraphEdge.MorphismType.EXTENSION,
                   description = "Direct path to intersection"),

        // One DUALITY morphism (K4 specific)
        @GraphEdge(from = "LK", to = "LDJ", morphismType = GraphEdge.MorphismType.DUALITY,
                   description = "External negation relation (complement of classical in paraconsistent)")
    }
)
public class GeneratedK4GraphExample {

    /**
     * The duality morphism in K4 encodes the relationship between classical (LK) and
     * paraconsistent (LDJ) logic via external negation. This represents how certain
     * provability properties in one calculus correspond to refutability or unprovability
     * properties in the other.
     */
    public static void main(String[] args) {
        System.out.println("K4 Complete Graph Specification");
        System.out.println("================================");
        System.out.println();

        System.out.println("Graph Structure:");
        System.out.println("  Type: K4 (4 nodes, 6 edges total)");
        System.out.println("  Extension morphisms: 5");
        System.out.println("  Duality morphism: 1 (LK ↔ LDJ)");
        System.out.println();

        System.out.println("Duality Morphism Semantics:");
        System.out.println("  The edge (LK ↔ LDJ) represents an external negation relation:");
        System.out.println("  - What is provable in LK may be refutable or unprovable in LDJ");
        System.out.println("  - What is unprovable in LK may be provable or refutable in LDJ");
        System.out.println("  - This captures the duality of classical and paraconsistent logic");
        System.out.println();

        System.out.println("Theorem/Anti-Theorem Implications:");
        System.out.println("  In K4, the duality morphism affects anti-theorem status mapping:");
        System.out.println("  - LEM in LK maps to LEM' status in LDJ via the duality relationship");
        System.out.println("  - Anti-theorems can express negation variants that respect duality");
        System.out.println();

        System.out.println("Generated artifacts:");
        System.out.println("  - GeneratedLKSignature.java (via LK node)");
        System.out.println("  - GeneratedLJSignature.java (via LJ node)");
        System.out.println("  - GeneratedLDJSignature.java (via LDJ node)");
        System.out.println("  - GeneratedCommonSignature.java (via COMMON node)");
        System.out.println("  - Proof interfaces and Rules for each");
        System.out.println("  - All aware of duality relationship in K4");
    }
}
