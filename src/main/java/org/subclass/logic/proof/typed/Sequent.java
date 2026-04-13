package org.subclass.logic.proof.typed;

import java.util.List;
import java.util.Objects;

/**
 * Represents a sequent in sequent calculus: Γ ⊢ Δ
 *
 * A sequent is parameterized by two cardinality types L and R:
 * - L (left/antecedent cardinality): how many formulas are allowed on the left
 * - R (right/succedent cardinality): how many formulas are allowed on the right
 *
 * The cardinality constraint is enforced at compile-time by the type parameter,
 * while actual formulas are stored at runtime.
 *
 * Examples:
 * - Sequent&lt;One, One&gt;: exactly one formula on each side (common logic)
 * - Sequent&lt;Many, One&gt;: unrestricted left, single right (intuitionistic)
 * - Sequent&lt;Many, Many&gt;: unrestricted both sides (classical)
 * - Sequent&lt;One, Many&gt;: single left, unrestricted right (dual/paraconsistent)
 *
 * @param &lt;L&gt; the cardinality of the antecedent (left side)
 * @param &lt;R&gt; the cardinality of the succedent (right side)
 */
public record Sequent<L extends Cardinality, R extends Cardinality>(
    List<Formula> antecedent,
    List<Formula> succedent
) {
    /**
     * Construct a sequent, enforcing that formulas are immutable.
     *
     * @param antecedent list of formulas on the left of the turnstile
     * @param succedent list of formulas on the right of the turnstile
     */
    public Sequent(List<Formula> antecedent, List<Formula> succedent) {
        this.antecedent = Objects.requireNonNull(antecedent, "Antecedent cannot be null");
        this.succedent = Objects.requireNonNull(succedent, "Succedent cannot be null");
        // Make lists immutable to preserve cardinality invariant at runtime
        this.antecedent = List.copyOf(this.antecedent);
        this.succedent = List.copyOf(this.succedent);
    }

    /**
     * Factory for common logic sequent: exactly one formula on each side.
     * Type: Sequent&lt;One, One&gt;
     *
     * @param left the single formula on the left (antecedent)
     * @param right the single formula on the right (succedent)
     * @return a common logic sequent
     * @throws IllegalArgumentException if either formula is null
     */
    public static Sequent<One, One> common(Formula left, Formula right) {
        Objects.requireNonNull(left, "Left formula cannot be null");
        Objects.requireNonNull(right, "Right formula cannot be null");

        Sequent<One, One> sequent = new Sequent<>(List.of(left), List.of(right));

        // Runtime assertion: verify cardinality
        assert sequent.antecedent.size() == 1 : "Common logic sequent must have exactly one antecedent formula";
        assert sequent.succedent.size() == 1 : "Common logic sequent must have exactly one succedent formula";

        return sequent;
    }

    /**
     * Factory for intuitionistic logic sequent: unrestricted left, single right.
     * Type: Sequent&lt;Many, One&gt;
     *
     * @param left list of formulas on the left (can be empty or multiple)
     * @param right the single formula on the right (succedent)
     * @return an intuitionistic logic sequent
     * @throws IllegalArgumentException if either parameter is null
     */
    public static Sequent<Many, One> intuitionistic(List<Formula> left, Formula right) {
        Objects.requireNonNull(left, "Left formulas cannot be null");
        Objects.requireNonNull(right, "Right formula cannot be null");

        Sequent<Many, One> sequent = new Sequent<>(left, List.of(right));

        // Runtime assertion: verify cardinality
        assert sequent.succedent.size() == 1 : "Intuitionistic sequent must have exactly one succedent formula";

        return sequent;
    }

    /**
     * Factory for classical logic sequent: unrestricted both sides.
     * Type: Sequent&lt;Many, Many&gt;
     *
     * @param left list of formulas on the left (can be empty or multiple)
     * @param right list of formulas on the right (can be empty or multiple)
     * @return a classical logic sequent
     * @throws IllegalArgumentException if either parameter is null
     */
    public static Sequent<Many, Many> classical(List<Formula> left, List<Formula> right) {
        Objects.requireNonNull(left, "Left formulas cannot be null");
        Objects.requireNonNull(right, "Right formulas cannot be null");

        return new Sequent<>(left, right);
    }

    /**
     * Factory for paraconsistent dual logic sequent: single left, unrestricted right.
     * Type: Sequent&lt;One, Many&gt;
     *
     * @param left the single formula on the left (antecedent)
     * @param right list of formulas on the right (can be empty or multiple)
     * @return a paraconsistent dual logic sequent
     * @throws IllegalArgumentException if either parameter is null
     */
    public static Sequent<One, Many> dual(Formula left, List<Formula> right) {
        Objects.requireNonNull(left, "Left formula cannot be null");
        Objects.requireNonNull(right, "Right formulas cannot be null");

        Sequent<One, Many> sequent = new Sequent<>(List.of(left), right);

        // Runtime assertion: verify cardinality
        assert sequent.antecedent.size() == 1 : "Dual sequent must have exactly one antecedent formula";

        return sequent;
    }

    /**
     * Get a human-readable representation of this sequent.
     *
     * @return string in the form "A, B ⊢ C, D"
     */
    @Override
    public String toString() {
        String leftStr = String.join(", ", antecedent.stream()
            .map(f -> f instanceof Formula.Atom a ? a.name() : f.representation())
            .toList());
        String rightStr = String.join(", ", succedent.stream()
            .map(f -> f instanceof Formula.Atom a ? a.name() : f.representation())
            .toList());

        if (leftStr.isEmpty() && rightStr.isEmpty()) {
            return "⊢";
        } else if (leftStr.isEmpty()) {
            return "⊢ " + rightStr;
        } else if (rightStr.isEmpty()) {
            return leftStr + " ⊢";
        } else {
            return leftStr + " ⊢ " + rightStr;
        }
    }
}
