package org.subclass.logic.rules.structural;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Right contraction:
 * <pre>
 *   Γ ⊢ Δ, A, A
 *   --------------- (CR)
 *    Γ ⊢ Δ, A
 * </pre>
 *
 * Absent in intuitionistic LJ (single-formula succedent leaves no room for
 * duplication); present in LK and LDJ.
 */
@RuleSpec(
    name = "ContractionRight",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Right contraction: merge two adjacent duplicate succedent formulas"
)
public record ContractionRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        int position,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public ContractionRight {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
        if (position < 0) {
            throw new IllegalArgumentException("Position must be non-negative: " + position);
        }
    }

    @Override
    public String ruleName() {
        return "ContractionRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
