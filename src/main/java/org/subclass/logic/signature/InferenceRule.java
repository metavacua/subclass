package org.subclass.logic.signature;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single inference rule in sequent calculus form.
 * An inference rule has zero or more premises (sequents) and exactly one conclusion (sequent).
 *
 * Example: For conjunction introduction on the right:
 * - Premises: [Γ ⊢ Δ, A] and [Γ ⊢ Δ, B]
 * - Conclusion: Γ ⊢ Δ, A ∧ B
 */
public class InferenceRule {
    private final String name;
    private final String connective;
    private final String side; // "left" or "right" for introduction rules
    private final List<String> premises;
    private final String conclusion;
    private final String description;

    /**
     * Create an inference rule.
     *
     * @param name Unique name for this rule (e.g., "AND_right_intro")
     * @param connective The connective this rule introduces (e.g., "∧")
     * @param side "left" or "right" indicating the side of ⊢ where connective appears
     * @param premises List of sequent forms as strings (e.g., ["Γ ⊢ Δ, A", "Γ ⊢ Δ, B"])
     * @param conclusion The conclusion sequent form (e.g., "Γ ⊢ Δ, A ∧ B")
     * @param description Human-readable description of the rule
     */
    public InferenceRule(String name, String connective, String side,
                        List<String> premises, String conclusion, String description) {
        this.name = Objects.requireNonNull(name);
        this.connective = Objects.requireNonNull(connective);
        this.side = Objects.requireNonNull(side);
        this.premises = Collections.unmodifiableList(Objects.requireNonNull(premises));
        this.conclusion = Objects.requireNonNull(conclusion);
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getConnective() {
        return connective;
    }

    public String getSide() {
        return side;
    }

    public List<String> getPremises() {
        return premises;
    }

    public String getConclusion() {
        return conclusion;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        if (premises.isEmpty()) {
            return name + ": " + conclusion;
        }
        return name + ": " + String.join(" | ", premises) + " / " + conclusion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InferenceRule that = (InferenceRule) o;
        return name.equals(that.name) && connective.equals(that.connective)
            && side.equals(that.side);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, connective, side);
    }
}
