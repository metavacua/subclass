package org.subclass.logic.signature;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a logical signature in sequent calculus form.
 * A logical signature defines the vocabulary (connectives) and structural rules
 * of a logical system. The signature determines whether the logic is functionally
 * complete or incomplete.
 *
 * A signature is in minimal canonical form when it contains only the necessary
 * connectives and structural rules without redundant or unreachable symbols.
 *
 * Example:
 * - LK (Classical): Has {∧, ∨, ¬, →} with full {Weakening, Contraction, Exchange}
 * - LJ (Intuitionistic): Has {∧, ∨, ¬, →} with {Weakening, Contraction, Exchange}
 *   but with restricted right-hand side of sequents (at most 1 formula)
 * - Linear Logic: Has {⊗, ⊕, !, ?} with {Exchange} only (no Weakening/Contraction)
 */
public record LogicalSignature(
    String name,
    String displayName,
    Set<Connective> connectives,
    Set<StructuralRule> structuralRules,
    boolean functionallyComplete,
    String description
) {
    /**
     * Canonical constructor for validation and immutability.
     * Uses Set.copyOf() (Java 10+) to guarantee immutability.
     */
    public LogicalSignature {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(displayName, "displayName cannot be null");
        Objects.requireNonNull(connectives, "connectives cannot be null");
        Objects.requireNonNull(structuralRules, "structuralRules cannot be null");
        connectives = Set.copyOf(connectives);
        structuralRules = Set.copyOf(structuralRules);
    }

    /**
     * Check if a specific connective is present in this signature.
     */
    public boolean hasConnective(String symbol) {
        return connectives.stream().anyMatch(c -> c.getSymbol().equals(symbol));
    }

    /**
     * Check if a specific structural rule is allowed.
     */
    public boolean hasStructuralRule(StructuralRule rule) {
        return structuralRules.contains(rule);
    }

    /**
     * Count the number of connectives in this signature.
     */
    public int getConnectiveCount() {
        return connectives.size();
    }

    /**
     * Check if this signature has all three structural rules.
     */
    public boolean hasAllStructuralRules() {
        return structuralRules.size() == 3; // W, C, E
    }

    /**
     * Check if this signature is substructural (missing at least one structural rule).
     */
    public boolean isSubstructural() {
        return structuralRules.size() < 3;
    }

    /**
     * Get a human-readable string representation of structural rules.
     */
    public String getStructuralRulesSummary() {
        return StructuralRule.formatRules(structuralRules);
    }

    @Override
    public String toString() {
        return name + " (" + displayName + ")" +
               " - Connectives: " + connectives.size() +
               ", Structural rules: " + getStructuralRulesSummary() +
               ", Functionally " + (functionallyComplete ? "complete" : "incomplete");
    }

    /**
     * Builder for creating LogicalSignature instances.
     */
    public static class Builder {
        private final String name;
        private final String displayName;
        private Set<Connective> connectives = Set.of();
        private Set<StructuralRule> structuralRules = Set.of();
        private boolean functionallyComplete = false;
        private String description = "";

        public Builder(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }

        public Builder addConnectives(Set<Connective> connectives) {
            this.connectives = connectives;
            return this;
        }

        public Builder addStructuralRules(Set<StructuralRule> structuralRules) {
            this.structuralRules = structuralRules;
            return this;
        }

        public Builder functionallyComplete(boolean functionallyComplete) {
            this.functionallyComplete = functionallyComplete;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public LogicalSignature build() {
            return new LogicalSignature(
                name, displayName, connectives, structuralRules, functionallyComplete, description
            );
        }
    }
}
