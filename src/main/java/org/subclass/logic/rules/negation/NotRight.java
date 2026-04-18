package org.subclass.logic.rules.negation;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Right negation introduction (¬R):
 * <pre>
 *   Γ, A ⊢ Δ
 *   ------------ (¬R)
 *   Γ ⊢ Δ, ¬A
 * </pre>
 *
 * Moves A from the antecedent to the succedent as ¬A.
 */
@RuleSpec(
    name = "NotRight",
    connective = "¬",
    side = "right",
    premiseCount = 1,
    description = "Right negation introduction"
)
public record NotRight<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public NotRight {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "NotRight";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
