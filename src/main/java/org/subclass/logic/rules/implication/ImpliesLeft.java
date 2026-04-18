package org.subclass.logic.rules.implication;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * Left implication introduction (→L):
 * <pre>
 *   Γ ⊢ Δ, A        Γ, B ⊢ Δ
 *   -------------------------- (→L)
 *       Γ, A → B ⊢ Δ
 * </pre>
 *
 * To use an implication A → B, prove its antecedent A on one premise and
 * discharge its consequent B on the other.
 */
@RuleSpec(
    name = "ImpliesLeft",
    connective = "→",
    side = "left",
    premiseCount = 2,
    description = "Left implication introduction"
)
public record ImpliesLeft<L extends Cardinality, R extends Cardinality>(
        Proof<L, R> antecedentPremise,
        Proof<L, R> consequentPremise,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public ImpliesLeft {
        Objects.requireNonNull(antecedentPremise, "Antecedent premise cannot be null");
        Objects.requireNonNull(consequentPremise, "Consequent premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "ImpliesLeft";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(antecedentPremise, consequentPremise);
    }
}
