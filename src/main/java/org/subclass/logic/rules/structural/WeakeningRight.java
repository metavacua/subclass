package org.subclass.logic.rules.structural;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Right weakening:
 * <pre>
 *      Γ ⊢ Δ
 *   --------------- (WR)
 *     Γ ⊢ Δ, A
 * </pre>
 *
 * Absent in intuitionistic LJ (which restricts the succedent to a single
 * formula and so cannot weaken on the right); present in LK and LDJ.
 */
@RuleSpec(
    name = "WeakeningRight",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Right weakening: add an unused formula to the succedent"
)
public record WeakeningRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        Formula added,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public WeakeningRight {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(added, "Added formula cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "WeakeningRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
