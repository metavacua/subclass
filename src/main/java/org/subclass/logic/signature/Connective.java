package org.subclass.logic.signature;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a logical connective with its introduction rules in sequent calculus.
 * In sequent calculus, each connective is defined by its left-introduction and
 * right-introduction rules, which characterize its meaning.
 *
 * Example: Conjunction (∧) has:
 * - Left: (Γ, A ⊢ Δ) | (Γ, B ⊢ Δ) / (Γ, A ∧ B ⊢ Δ)
 * - Right: (Γ ⊢ Δ, A) | (Γ ⊢ Δ, B) / (Γ ⊢ Δ, A ∧ B)
 *   Actually right introduction for conjunction is: (Γ ⊢ Δ, A) (Γ ⊢ Δ, B) / (Γ ⊢ Δ, A ∧ B)
 */
public class Connective {
    private final String symbol;
    private final int arity;
    private final String category; // "propositional", "quantifier", etc.
    private final List<InferenceRule> leftRules;
    private final List<InferenceRule> rightRules;
    private final String description;

    /**
     * Create a connective with its introduction rules.
     *
     * @param symbol The symbol representing this connective (e.g., "∧", "∨", "¬", "∀")
     * @param arity The arity of the connective (1 for negation, 2 for binary operators, etc.)
     * @param category The category (e.g., "propositional", "quantifier")
     * @param leftRules List of left-introduction rules
     * @param rightRules List of right-introduction rules
     * @param description Human-readable description
     */
    public Connective(String symbol, int arity, String category,
                     List<InferenceRule> leftRules, List<InferenceRule> rightRules,
                     String description) {
        this.symbol = Objects.requireNonNull(symbol);
        this.arity = arity;
        this.category = Objects.requireNonNull(category);
        this.leftRules = Collections.unmodifiableList(Objects.requireNonNull(leftRules));
        this.rightRules = Collections.unmodifiableList(Objects.requireNonNull(rightRules));
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getArity() {
        return arity;
    }

    public String getCategory() {
        return category;
    }

    public List<InferenceRule> getLeftRules() {
        return leftRules;
    }

    public List<InferenceRule> getRightRules() {
        return rightRules;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this connective has at least one left-introduction rule.
     */
    public boolean hasLeftIntroduction() {
        return !leftRules.isEmpty();
    }

    /**
     * Check if this connective has at least one right-introduction rule.
     */
    public boolean hasRightIntroduction() {
        return !rightRules.isEmpty();
    }

    /**
     * Check if this connective is fully defined (has both left and right rules).
     * Some logics may have incomplete connectives.
     */
    public boolean isFullyDefined() {
        return hasLeftIntroduction() && hasRightIntroduction();
    }

    @Override
    public String toString() {
        return symbol + " (arity: " + arity + ", category: " + category + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connective that = (Connective) o;
        return symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
