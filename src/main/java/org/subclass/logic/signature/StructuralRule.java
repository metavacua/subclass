package org.subclass.logic.signature;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum representing structural rules in sequent calculus.
 * These rules apply to all formulas uniformly and define
 * the meta-logical properties of a sequent system.
 */
public enum StructuralRule {
    /**
     * Weakening (W): Γ ⊢ Δ / Γ, A ⊢ Δ (left) or Γ ⊢ Δ / Γ ⊢ Δ, A (right)
     * Allows addition of unused formulas.
     */
    WEAKENING("W", "Allows addition of unused formulas to sequent context"),

    /**
     * Contraction (C): Γ, A, A ⊢ Δ / Γ, A ⊢ Δ (left) or Γ ⊢ Δ, A, A / Γ ⊢ Δ, A (right)
     * Allows removal of duplicate formulas.
     */
    CONTRACTION("C", "Allows removal of duplicate formulas from sequent context"),

    /**
     * Exchange (E): Γ, A, B, Γ' ⊢ Δ / Γ, B, A, Γ' ⊢ Δ (left) or similar (right)
     * Allows reordering of formulas.
     */
    EXCHANGE("E", "Allows reordering of formulas in sequent context");

    private final String symbol;
    private final String description;

    StructuralRule(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Format a set of structural rules as a human-readable string.
     * Handles special cases (full set, empty set) and provides consistent formatting.
     *
     * @param rules Set of structural rules to format
     * @return Human-readable string representation of the rules
     */
    public static String formatRules(Set<StructuralRule> rules) {
        if (rules == null || rules.isEmpty()) {
            return "No structural rules";
        }
        if (rules.size() == 3) {
            return "Full structural rules (W, C, E)";
        }
        return rules.stream()
            .map(StructuralRule::getSymbol)
            .sorted()
            .collect(Collectors.joining(", "));
    }
}
