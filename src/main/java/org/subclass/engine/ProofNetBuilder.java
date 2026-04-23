package org.subclass.engine;

import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Sequent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builds a Ranked Graph from a Sequent by decomposing formulas.
 * Follows Gentzen-style bilateral rules for polarity assignment.
 */
public class ProofNetBuilder {
    private final RankedGraph graph = new RankedGraph();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * Decomposes a sequent into a ranked graph.
     * @param sequent The sequent to decompose.
     * @return A RankedGraph representing the decomposition.
     */
    public RankedGraph build(Sequent<?, ?> sequent) {
        // Antecedents are initially negative
        for (Formula f : sequent.antecedent()) {
            decompose(f, Polarity.NEGATIVE, 0);
        }
        // Succedents are initially positive
        for (Formula f : sequent.succedent()) {
            decompose(f, Polarity.POSITIVE, 0);
        }
        return graph;
    }

    /**
     * Recursively decomposes a formula and adds nodes/edges to the graph.
     * 
     * @param formula The formula to decompose.
     * @param polarity Current logical polarity.
     * @param rank Current nesting depth.
     * @return The ID of the created node.
     */
    private String decompose(Formula formula, Polarity polarity, int rank) {
        String id = "node_" + idGenerator.getAndIncrement();
        Node node = new Node(id, rank, polarity, formula.hashCode());
        graph.addNode(node);

        if (formula instanceof Formula.Atom) {
            // Atoms are leaves in the decomposition tree.
            return id;
        } else if (formula instanceof Formula.Not not) {
            // Negation flips polarity.
            String childId = decompose(not.formula(), polarity.opposite(), rank + 1);
            graph.addEdge(new Edge(id, childId, EdgeType.EXTENSIVE));
        } else if (formula instanceof Formula.And and) {
            // Conjunction preserves polarity.
            String leftId = decompose(and.left(), polarity, rank + 1);
            String rightId = decompose(and.right(), polarity, rank + 1);
            graph.addEdge(new Edge(id, leftId, EdgeType.EXTENSIVE));
            graph.addEdge(new Edge(id, rightId, EdgeType.EXTENSIVE));
        } else if (formula instanceof Formula.Or or) {
            // Disjunction preserves polarity.
            String leftId = decompose(or.left(), polarity, rank + 1);
            String rightId = decompose(or.right(), polarity, rank + 1);
            graph.addEdge(new Edge(id, leftId, EdgeType.EXTENSIVE));
            graph.addEdge(new Edge(id, rightId, EdgeType.EXTENSIVE));
        } else if (formula instanceof Formula.Implies implies) {
            // Implication (A -> B) is equivalent to (not A or B).
            // Antecedent (A) gets flipped polarity, Consequent (B) preserves it.
            String antId = decompose(implies.antecedent(), polarity.opposite(), rank + 1);
            String consId = decompose(implies.consequent(), polarity, rank + 1);
            graph.addEdge(new Edge(id, antId, EdgeType.EXTENSIVE));
            graph.addEdge(new Edge(id, consId, EdgeType.EXTENSIVE));
        }

        return id;
    }
}
