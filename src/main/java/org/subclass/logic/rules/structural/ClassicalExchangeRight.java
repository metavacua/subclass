package org.subclass.logic.rules.structural;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Classical (arbitrary-position) exchange on the succedent.
 * Strictly stronger than {@link AdjacentExchangeRight}.
 */
@RuleSpec(
    name = "ClassicalExchangeRight",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Arbitrary-swap exchange on the succedent (fully commutative)"
)
public record ClassicalExchangeRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        int i,
        int j,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public ClassicalExchangeRight {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
        if (i < 0 || j < 0) {
            throw new IllegalArgumentException("Positions must be non-negative: i=" + i + " j=" + j);
        }
    }

    @Override
    public String ruleName() {
        return "ClassicalExchangeRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
