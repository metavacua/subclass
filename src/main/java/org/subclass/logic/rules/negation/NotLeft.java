package org.subclass.logic.rules.negation;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Left negation introduction (¬L):
 * <pre>
 *   Γ ⊢ Δ, A
 *   ------------ (¬L)
 *   Γ, ¬A ⊢ Δ
 * </pre>
 *
 * Moves A from the succedent to the antecedent as ¬A.
 */
@RuleSpec(
    name = "NotLeft",
    connective = "¬",
    side = "left",
    premiseCount = 1,
    description = "Left negation introduction"
)
public record NotLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public NotLeft {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "NotLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
