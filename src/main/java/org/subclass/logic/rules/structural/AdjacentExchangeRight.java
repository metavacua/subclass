package org.subclass.logic.rules.structural;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Adjacent exchange on the succedent:
 * <pre>
 *   Γ ⊢ Δ, A, B, Δ'
 *   -------------------- (ExR-adj, swap at position {@link #position()})
 *   Γ ⊢ Δ, B, A, Δ'
 * </pre>
 */
@RuleSpec(
    name = "AdjacentExchangeRight",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Adjacent-swap exchange on the succedent (non-commutative primitive)"
)
public record AdjacentExchangeRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        int position,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public AdjacentExchangeRight {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
        if (position < 0) {
            throw new IllegalArgumentException("Position must be non-negative: " + position);
        }
    }

    @Override
    public String ruleName() {
        return "AdjacentExchangeRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
