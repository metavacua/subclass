package org.subclass.logic.rules.structural;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Classical (arbitrary-position) exchange on the antecedent:
 * <pre>
 *   Γ, …, A, …, B, … ⊢ Δ
 *   ------------------------ (ExL, swap positions i, j)
 *   Γ, …, B, …, A, … ⊢ Δ
 * </pre>
 *
 * The fully-commutative primitive: any two antecedent positions may be
 * swapped directly. Strictly stronger than {@link AdjacentExchangeLeft}:
 * a logic that admits this rule admits all permutations of the antecedent;
 * a logic that admits only adjacent exchange admits the same permutations
 * but derives each via sequential applications.
 */
@RuleSpec(
    name = "ClassicalExchangeLeft",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Arbitrary-swap exchange on the antecedent (fully commutative)"
)
public record ClassicalExchangeLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        int i,
        int j,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public ClassicalExchangeLeft {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
        if (i < 0 || j < 0) {
            throw new IllegalArgumentException("Positions must be non-negative: i=" + i + " j=" + j);
        }
    }

    @Override
    public String ruleName() {
        return "ClassicalExchangeLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
