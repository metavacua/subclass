package org.subclass.logic.signature;

import java.util.Collections;
import java.util.HashSet;
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
public class LogicalSignature {
    private final String name;
    private final String displayName;
    private final Set<Connective> connectives;
    private final Set<StructuralRule> structuralRules;
    private final boolean functionallyComplete;
    private final String description;

    /**
     * Create a logical signature.
     *
     * @param name Internal identifier (e.g., "LK", "LJ")
     * @param displayName Human-readable name (e.g., "Classical Sequent Calculus")
     * @param connectives Set of connectives in this signature
     * @param structuralRules Set of structural rules allowed
     * @param functionallyComplete Whether this signature is functionally complete
     * @param description Detailed description
     */
    public LogicalSignature(String name, String displayName,
                           Set<Connective> connectives,
                           Set<StructuralRule> structuralRules,
                           boolean functionallyComplete,
                           String description) {
        this.name = Objects.requireNonNull(name);
        this.displayName = Objects.requireNonNull(displayName);
        this.connectives = Collections.unmodifiableSet(Objects.requireNonNull(connectives));
        this.structuralRules = Collections.unmodifiableSet(Objects.requireNonNull(structuralRules));
        this.functionallyComplete = functionallyComplete;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<Connective> getConnectives() {
        return connectives;
    }

    public Set<StructuralRule> getStructuralRules() {
        return structuralRules;
    }

    public boolean isFunctionallyComplete() {
        return functionallyComplete;
    }

    public String getDescription() {
        return description;
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
        if (hasAllStructuralRules()) {
            return "Full structural rules (W, C, E)";
        }
        StringBuilder sb = new StringBuilder();
        if (hasStructuralRule(StructuralRule.WEAKENING)) {
            sb.append("W");
        }
        if (hasStructuralRule(StructuralRule.CONTRACTION)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("C");
        }
        if (hasStructuralRule(StructuralRule.EXCHANGE)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("E");
        }
        return sb.toString().isEmpty() ? "No structural rules" : sb.toString();
    }

    @Override
    public String toString() {
        return name + " (" + displayName + ")" +
               " - Connectives: " + connectives.size() +
               ", Structural rules: " + getStructuralRulesSummary() +
               ", Functionally " + (functionallyComplete ? "complete" : "incomplete");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicalSignature that = (LogicalSignature) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Builder for constructing LogicalSignature instances.
     */
    public static class Builder {
        private final String name;
        private final String displayName;
        private final Set<Connective> connectives = new HashSet<>();
        private final Set<StructuralRule> structuralRules = new HashSet<>();
        private boolean functionallyComplete = false;
        private String description = "";

        public Builder(String name, String displayName) {
            this.name = Objects.requireNonNull(name);
            this.displayName = Objects.requireNonNull(displayName);
        }

        public Builder addConnective(Connective connective) {
            this.connectives.add(Objects.requireNonNull(connective));
            return this;
        }

        public Builder addConnectives(Set<Connective> connectives) {
            this.connectives.addAll(Objects.requireNonNull(connectives));
            return this;
        }

        public Builder addStructuralRule(StructuralRule rule) {
            this.structuralRules.add(Objects.requireNonNull(rule));
            return this;
        }

        public Builder addStructuralRules(Set<StructuralRule> rules) {
            this.structuralRules.addAll(Objects.requireNonNull(rules));
            return this;
        }

        public Builder functionallyComplete(boolean complete) {
            this.functionallyComplete = complete;
            return this;
        }

        public Builder description(String desc) {
            this.description = desc;
            return this;
        }

        public LogicalSignature build() {
            return new LogicalSignature(name, displayName, connectives, structuralRules,
                    functionallyComplete, description);
        }
    }
}
