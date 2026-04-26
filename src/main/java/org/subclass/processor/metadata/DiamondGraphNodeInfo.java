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
    private static final Set<String> VALID_POSITIONS = Set.of("classical", "intuitionistic", "paraconsistent", "common");
    private static final Set<String> VALID_AXIOM_SCHEMAS = Set.of("A⊢A", "Γ,A⊢A", "A⊢A,Δ", "Γ,A⊢A,Δ");
    private static final Set<String> VALID_STRUCTURAL_RULES = Set.of("W", "C", "E", "Cut");

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
     * Returns the set of rules that should be available based on the axiom schema.
     *
     * @return set of rule names (short form: W, C, E, Cut) that are valid for this node
     */
    public Set<String> derivedStructuralRules() {
        Set<String> derived = new HashSet<>();

        // Rules depend on axiom schema
        if (axiomSchema.startsWith("Γ")) {
            // Intuitionistic or classical: left-side rules allowed
            derived.add("W");  // Weakening left
            derived.add("C");  // Contraction left
            derived.add("E");  // Exchange left
        }

        if (axiomSchema.endsWith("Δ")) {
            // Paraconsistent or classical: right-side rules allowed
            derived.add("W");  // Weakening right
            derived.add("C");  // Contraction right
            derived.add("E");  // Exchange right
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
        if (!VALID_POSITIONS.contains(position)) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }

        // Validate axiom schema
        if (!VALID_AXIOM_SCHEMAS.contains(axiomSchema)) {
            throw new IllegalArgumentException("Invalid axiomSchema: " + axiomSchema);
        }

        // Validate structural rules and ensure they match axiom schema requirements
        validateStructuralRulesForAxiomSchema();
    }

    /**
     * Validates that configured structural rules are valid and consistent with the axiom schema.
     * Rules must be a subset of rules allowed by the axiom schema.
     */
    private void validateStructuralRulesForAxiomSchema() {
        // Validate that each rule is valid
        for (String rule : structuralRules) {
            if (!VALID_STRUCTURAL_RULES.contains(rule)) {
                throw new IllegalArgumentException("Invalid structural rule: " + rule);
            }
        }

        // Validate that configured rules are consistent with axiom schema
        Set<String> allowedRules = derivedStructuralRules();
        for (String rule : structuralRules) {
            if (!allowedRules.contains(rule)) {
                throw new IllegalArgumentException(
                    "Structural rule '" + rule + "' not allowed by axiom schema '" + axiomSchema + "'; " +
                    "allowed rules for this schema: " + allowedRules
                );
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
