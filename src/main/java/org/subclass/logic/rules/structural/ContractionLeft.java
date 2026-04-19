package org.subclass.logic.rules.structural;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Left contraction:
 * <pre>
 *   Γ, A, A ⊢ Δ
 *   --------------- (CL)
 *    Γ, A ⊢ Δ
 * </pre>
 *
 * Collapses two adjacent copies of the same antecedent formula into one.
 * Absent in affine and linear logics; present in LK / LJ / LDJ / Common.
 */
@RuleSpec(
    name = "ContractionLeft",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Left contraction: merge two adjacent duplicate antecedent formulas"
)
public record ContractionLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        int position,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public ContractionLeft {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
        if (position < 0) {
            throw new IllegalArgumentException("Position must be non-negative: " + position);
        }
    }

    @Override
    public String ruleName() {
        return "ContractionLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
