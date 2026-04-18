package org.subclass.logic.rules.implication;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Right implication introduction (→R):
 * <pre>
 *   Γ, A ⊢ Δ, B
 *   --------------- (→R)
 *   Γ ⊢ Δ, A → B
 * </pre>
 *
 * Discharges the hypothesis A to build A → B on the right.
 */
@RuleSpec(
    name = "ImpliesRight",
    connective = "→",
    side = "right",
    premiseCount = 1,
    description = "Right implication introduction"
)
public record ImpliesRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public ImpliesRight {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "ImpliesRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
