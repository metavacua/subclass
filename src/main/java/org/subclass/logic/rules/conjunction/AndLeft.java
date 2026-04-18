package org.subclass.logic.rules.conjunction;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Left conjunction introduction (∧L):
 * <pre>
 *     Γ, A, B ⊢ Δ
 *   --------------- (∧L)
 *   Γ, A ∧ B ⊢ Δ
 * </pre>
 *
 * A single premise: replace a pair of adjacent antecedent formulas A, B with
 * their conjunction A ∧ B.
 */
@RuleSpec(
    name = "AndLeft",
    connective = "∧",
    side = "left",
    premiseCount = 1,
    description = "Left conjunction introduction"
)
public record AndLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public AndLeft {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "AndLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
