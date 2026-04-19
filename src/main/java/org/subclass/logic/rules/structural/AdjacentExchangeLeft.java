package org.subclass.logic.rules.structural;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Adjacent exchange on the antecedent:
 * <pre>
 *   Γ, A, B, Γ' ⊢ Δ
 *   -------------------- (ExL-adj, swap at position {@link #position()})
 *   Γ, B, A, Γ' ⊢ Δ
 * </pre>
 *
 * The non-commutative primitive: only adjacent positions may be swapped.
 * Logics that admit this rule but not {@link ClassicalExchangeLeft} are
 * weakly commutative (adjacent-only); logics that admit both are fully
 * commutative.
 */
@RuleSpec(
    name = "AdjacentExchangeLeft",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Adjacent-swap exchange on the antecedent (non-commutative primitive)"
)
public record AdjacentExchangeLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        int position,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public AdjacentExchangeLeft {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
        if (position < 0) {
            throw new IllegalArgumentException("Position must be non-negative: " + position);
        }
    }

    @Override
    public String ruleName() {
        return "AdjacentExchangeLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
