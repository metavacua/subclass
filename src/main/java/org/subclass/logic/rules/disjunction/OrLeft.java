package org.subclass.logic.rules.disjunction;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Left disjunction introduction (∨L):
 * <pre>
 *   Γ, A ⊢ Δ        Γ, B ⊢ Δ
 *   -------------------------- (∨L)
 *        Γ, A ∨ B ⊢ Δ
 * </pre>
 *
 * Case analysis: to use an assumption A ∨ B, discharge both possibilities.
 */
@RuleSpec(
    name = "OrLeft",
    connective = "∨",
    side = "left",
    premiseCount = 2,
    description = "Left disjunction introduction (case analysis)"
)
public record OrLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> left,
        Proof<L, R> right,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public OrLeft {
        Objects.requireNonNull(left, "Left premise cannot be null");
        Objects.requireNonNull(right, "Right premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "OrLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(left, right);
    }
}
