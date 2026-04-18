package org.subclass.logic.rules.cut;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * The cut rule:
 * <pre>
 *   Γ ⊢ Δ, A        A, Γ' ⊢ Δ'
 *   ---------------------------- (Cut)
 *          Γ, Γ' ⊢ Δ, Δ'
 * </pre>
 *
 * Cut is the compositional backbone of sequent calculus. Its elimination
 * (Gentzen's Hauptsatz) establishes cut-freeness of the proof system; the
 * {@link #cutFormula()} component is the formula being "cut out."
 *
 * @param <L> antecedent cardinality (shared by both premises and conclusion)
 * @param <R> succedent cardinality (shared by both premises and conclusion)
 */
@RuleSpec(
    name = "Cut",
    connective = "",
    side = "cut",
    premiseCount = 2,
    description = "Cut rule: composition of two proofs on a shared formula"
)
public record Cut<L extends Cardinality, R extends Cardinality>(
        Formula cutFormula,
        Proof<L, R> left,
        Proof<L, R> right,
        Sequent<L, R> conclusion
) implements ProofNode<L, R> {

    public Cut {
        Objects.requireNonNull(cutFormula, "Cut formula cannot be null");
        Objects.requireNonNull(left, "Left premise cannot be null");
        Objects.requireNonNull(right, "Right premise cannot be null");
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");
    }

    @Override
    public String ruleName() {
        return "Cut";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of(left, right);
    }
}
