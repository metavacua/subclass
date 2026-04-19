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
 * Left weakening:
 * <pre>
 *        Γ ⊢ Δ
 *   ---------------- (WL)
 *     Γ, A ⊢ Δ
 * </pre>
 *
 * Adds a formula to the antecedent without using it. Absent in relevance
 * logics; present in LK / LJ / LDJ / Common.
 */
@RuleSpec(
    name = "WeakeningLeft",
    connective = "",
    side = "structural",
    premiseCount = 1,
    description = "Left weakening: add an unused formula to the antecedent"
)
public record WeakeningLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> premise,
        Formula added,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public WeakeningLeft {
        Objects.requireNonNull(premise, "Premise cannot be null");
        Objects.requireNonNull(added, "Added formula cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "WeakeningLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(premise);
    }
}
