package org.subclass.logic.rules.conjunction;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Right conjunction introduction (∧R):
 * <pre>
 *   Γ ⊢ Δ, A        Γ ⊢ Δ, B
 *   -------------------------- (∧R)
 *        Γ ⊢ Δ, A ∧ B
 * </pre>
 *
 * Premises share the antecedent Γ and the surrounding succedent Δ; the
 * conclusion introduces A ∧ B on the right.
 */
@RuleSpec(
    name = "AndRight",
    connective = "∧",
    side = "right",
    premiseCount = 2,
    description = "Right conjunction introduction"
)
public record AndRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> left,
        Proof<L, R> right,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public AndRight {
        Objects.requireNonNull(left, "Left premise cannot be null");
        Objects.requireNonNull(right, "Right premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "AndRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(left, right);
    }
}
