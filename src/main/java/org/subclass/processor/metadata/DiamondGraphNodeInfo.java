package org.subclass.processor.metadata;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Parsed configuration of a single node in the diamond graph.
 * Encodes position, cardinality constraints, axiom schema, and structural rules.
 */
public final class DiamondGraphNodeInfo {
    private final String position;
    private final String antecedentCardinality;
    private final String succedentCardinality;
    private final String axiomSchema;
    private final Set<String> structuralRules;
    private final Set<String> connectives;
    private final boolean functionallyComplete;

    public DiamondGraphNodeInfo(
        String position,
        String antecedentCardinality,
        String succedentCardinality,
        String axiomSchema,
        Set<String> structuralRules,
        Set<String> connectives,
        boolean functionallyComplete
    ) {
        this.position = Objects.requireNonNull(position, "position");
        this.antecedentCardinality = Objects.requireNonNull(antecedentCardinality, "antecedentCardinality");
        this.succedentCardinality = Objects.requireNonNull(succedentCardinality, "succedentCardinality");
        this.axiomSchema = Objects.requireNonNull(axiomSchema, "axiomSchema");
        this.structuralRules = Collections.unmodifiableSet(new HashSet<>(structuralRules));
        this.connectives = Collections.unmodifiableSet(new HashSet<>(connectives));
        this.functionallyComplete = functionallyComplete;

        validate();
    }

    public String position() {
        return position;
    }

    public String antecedentCardinality() {
        return antecedentCardinality;
    }

    public String succedentCardinality() {
        return succedentCardinality;
    }

    public String axiomSchema() {
        return axiomSchema;
    }

    public Set<String> structuralRules() {
        return structuralRules;
    }

    public Set<String> connectives() {
        return connectives;
    }

    public boolean isFunctionallyComplete() {
        return functionallyComplete;
    }

    /**
     * Derives which structural rules are valid for this node's axiom schema.
     * Validates that configured rules match the axiom schema requirements.
     */
    public Set<String> derivedStructuralRules() {
        Set<String> derived = new HashSet<>();

        // All logics allow cut and axiom
        derived.add("Axiom");
        derived.add("Cut");

        // Rules depend on axiom schema
        if (axiomSchema.startsWith("Γ")) {
            // Intuitionistic or classical: left-side rules allowed
            derived.add("WeakeningLeft");
            derived.add("ContractionLeft");
            derived.add("ExchangeLeft");
        }

        if (axiomSchema.endsWith("Δ")) {
            // Paraconsistent or classical: right-side rules allowed
            derived.add("WeakeningRight");
            derived.add("ContractionRight");
            derived.add("ExchangeRight");
        }

        // Validate against configured rules (configuration should match derived)
        for (String rule : structuralRules) {
            if (!derived.contains(rule) && !derived.contains(rule + "Left") && !derived.contains(rule + "Right")) {
                // Allow short names (W, C, E) and map them
                if (!(rule.equals("W") || rule.equals("C") || rule.equals("E"))) {
                    throw new IllegalArgumentException(
                        "Structural rule " + rule + " not valid for axiom schema " + axiomSchema
                    );
                }
            }
        }

        return derived;
    }

    private void validate() {
        // Validate cardinality values
        if (!antecedentCardinality.equals("One") && !antecedentCardinality.equals("Many")) {
            throw new IllegalArgumentException("Invalid antecedentCardinality: " + antecedentCardinality);
        }
        if (!succedentCardinality.equals("One") && !succedentCardinality.equals("Many")) {
            throw new IllegalArgumentException("Invalid succedentCardinality: " + succedentCardinality);
        }

        // Validate position
        Set<String> validPositions = Set.of("classical", "intuitionistic", "paraconsistent", "common");
        if (!validPositions.contains(position)) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }

        // Validate axiom schema
        Set<String> validSchemas = Set.of("A⊢A", "Γ,A⊢A", "A⊢A,Δ", "Γ,A⊢A,Δ");
        if (!validSchemas.contains(axiomSchema)) {
            throw new IllegalArgumentException("Invalid axiomSchema: " + axiomSchema);
        }

        // Validate structural rules
        for (String rule : structuralRules) {
            Set<String> validRules = Set.of("W", "C", "E", "Cut");
            if (!validRules.contains(rule)) {
                throw new IllegalArgumentException("Invalid structural rule: " + rule);
            }
        }
    }

    @Override
    public String toString() {
        return "DiamondGraphNodeInfo{" +
               "position='" + position + '\'' +
               ", cardinality=(" + antecedentCardinality + ", " + succedentCardinality + ')' +
               ", axiomSchema='" + axiomSchema + '\'' +
               '}';
    }
}
