package org.subclass.logic.rules.disjunction;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Right disjunction introduction (∨R), multi-succedent form:
 * <pre>
 *   Γ ⊢ Δ, A, B
 *   --------------- (∨R)
 *   Γ ⊢ Δ, A ∨ B
 * </pre>
 *
 * A single-premise rule that combines the last two succedent formulas into a
 * disjunction. This is the natural form in classical sequent calculus with
 * multi-conclusion succedents (as used in the LEM derivation).
 */
@RuleSpec(
    name = "OrRight",
    connective = "∨",
    side = "right",
    premiseCount = 1,
    description = "Right disjunction introduction (multi-succedent combining form)"
)
public record OrRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public OrRight {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "OrRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
