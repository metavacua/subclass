package org.subclass.processor.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Parsed representation of a @DiamondGraph annotation.
 * Provides structured access to all four node configurations and validates graph constraints.
 */
public final class DiamondGraphInfo {
    private final String name;
    private final String displayName;
    private final String description;
    private final DiamondGraphNodeInfo classicalNode;
    private final DiamondGraphNodeInfo intuitionisticNode;
    private final DiamondGraphNodeInfo paraconsistentNode;
    private final DiamondGraphNodeInfo commonLogicNode;
    private final Map<String, TheoremStatusExpectation> theoremExpectations;

    public DiamondGraphInfo(
        String name,
        String displayName,
        String description,
        DiamondGraphNodeInfo classicalNode,
        DiamondGraphNodeInfo intuitionisticNode,
        DiamondGraphNodeInfo paraconsistentNode,
        DiamondGraphNodeInfo commonLogicNode,
        Map<String, TheoremStatusExpectation> theoremExpectations
    ) {
        this.name = Objects.requireNonNull(name, "name");
        this.displayName = displayName;
        this.description = description;
        this.classicalNode = Objects.requireNonNull(classicalNode, "classicalNode");
        this.intuitionisticNode = Objects.requireNonNull(intuitionisticNode, "intuitionisticNode");
        this.paraconsistentNode = Objects.requireNonNull(paraconsistentNode, "paraconsistentNode");
        this.commonLogicNode = Objects.requireNonNull(commonLogicNode, "commonLogicNode");
        this.theoremExpectations = new HashMap<>(theoremExpectations);

        validate();
    }

    public String name() {
        return name;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }

    public DiamondGraphNodeInfo classicalNode() {
        return classicalNode;
    }

    public DiamondGraphNodeInfo intuitionisticNode() {
        return intuitionisticNode;
    }

    public DiamondGraphNodeInfo paraconsistentNode() {
        return paraconsistentNode;
    }

    public DiamondGraphNodeInfo commonLogicNode() {
        return commonLogicNode;
    }

    public Map<String, TheoremStatusExpectation> theoremExpectations() {
        return new HashMap<>(theoremExpectations);
    }

    public DiamondGraphNodeInfo getNodeByPosition(String position) {
        return switch (position) {
            case "classical" -> classicalNode;
            case "intuitionistic" -> intuitionisticNode;
            case "paraconsistent" -> paraconsistentNode;
            case "common" -> commonLogicNode;
            default -> throw new IllegalArgumentException("Unknown position: " + position);
        };
    }

    /**
     * Validates the cardinality hierarchy and position consistency.
     * Common (One,One) ⊂ {Intuitionistic(Many,One), Paraconsistent(One,Many)} ⊂ Classical(Many,Many)
     */
    private void validate() {
        // Validate that position fields match their slots
        validateNodePositionConsistency();

        // Validate cardinality constraints and positions
        validateNodeConfiguration(classicalNode, "classical", "Many", "Many");
        validateNodeConfiguration(intuitionisticNode, "intuitionistic", "Many", "One");
        validateNodeConfiguration(paraconsistentNode, "paraconsistent", "One", "Many");
        validateNodeConfiguration(commonLogicNode, "common", "One", "One");

        // Validate axiom schemas
        validateAxiomSchemaConsistency();
    }

    /**
     * Ensures each node's position() string matches its slot in the graph.
     * This prevents silent bugs where a node is placed in the wrong slot.
     */
    private void validateNodePositionConsistency() {
        if (!classicalNode.position().equals("classical")) {
            throw new IllegalArgumentException(
                "Classical node slot contains node with position '" + classicalNode.position() +
                "'; node position must match its slot"
            );
        }
        if (!intuitionisticNode.position().equals("intuitionistic")) {
            throw new IllegalArgumentException(
                "Intuitionistic node slot contains node with position '" + intuitionisticNode.position() +
                "'; node position must match its slot"
            );
        }
        if (!paraconsistentNode.position().equals("paraconsistent")) {
            throw new IllegalArgumentException(
                "Paraconsistent node slot contains node with position '" + paraconsistentNode.position() +
                "'; node position must match its slot"
            );
        }
        if (!commonLogicNode.position().equals("common")) {
            throw new IllegalArgumentException(
                "Common node slot contains node with position '" + commonLogicNode.position() +
                "'; node position must match its slot"
            );
        }
    }

    private void validateNodeConfiguration(
        DiamondGraphNodeInfo node,
        String expectedPosition,
        String expectedAntecedent,
        String expectedSuccedent
    ) {
        if (!node.antecedentCardinality().equals(expectedAntecedent) ||
            !node.succedentCardinality().equals(expectedSuccedent)) {
            throw new IllegalArgumentException(
                node.position() + " node must have cardinality (" + expectedAntecedent + ", " + expectedSuccedent +
                "), got (" + node.antecedentCardinality() + ", " + node.succedentCardinality() + ")"
            );
        }
    }

    private void validateAxiomSchemaConsistency() {
        String classicalAxiom = classicalNode.axiomSchema();
        String intuitionisticAxiom = intuitionisticNode.axiomSchema();
        String paraconsistentAxiom = paraconsistentNode.axiomSchema();
        String commonAxiom = commonLogicNode.axiomSchema();

        // Classical should be the most permissive
        if (!classicalAxiom.equals("Γ,A⊢A,Δ")) {
            throw new IllegalArgumentException(
                "Classical node must have axiom schema Γ,A⊢A,Δ, got " + classicalAxiom
            );
        }

        // Intuitionistic and paraconsistent should be duals
        if (!intuitionisticAxiom.equals("Γ,A⊢A")) {
            throw new IllegalArgumentException(
                "Intuitionistic node must have axiom schema Γ,A⊢A, got " + intuitionisticAxiom
            );
        }

        if (!paraconsistentAxiom.equals("A⊢A,Δ")) {
            throw new IllegalArgumentException(
                "Paraconsistent node must have axiom schema A⊢A,Δ, got " + paraconsistentAxiom
            );
        }

        // Common should be the most restrictive
        if (!commonAxiom.equals("A⊢A")) {
            throw new IllegalArgumentException(
                "Common node must have axiom schema A⊢A, got " + commonAxiom
            );
        }
    }

    @Override
    public String toString() {
        return "DiamondGraphInfo{name='" + name + "', displayName='" + displayName + "'}";
    }
}
