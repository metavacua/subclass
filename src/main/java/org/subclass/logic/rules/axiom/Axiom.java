package org.subclass.logic.rules.axiom;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Objects;

/**
 * The identity axiom: A ⊢ A.
 *
 * Axioms are the leaves of every proof tree: zero premises, one active
 * formula that appears on both sides of the conclusion sequent. Parameterized
 * on {@code <L, R>} so that the same reified axiom fits a common, classical,
 * intuitionistic, or dual context — the identity sequent {@code [A] ⊢ [A]}
 * satisfies every cardinality constraint.
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
@RuleSpec(
    name = "Axiom",
    connective = "",
    side = "axiom",
    premiseCount = 0,
    description = "Identity axiom A ⊢ A"
)
public record Axiom<L extends Cardinality, R extends Cardinality>(Formula formula)
    implements ProofNode<L, R> {

    public Axiom {
        Objects.requireNonNull(formula, "Axiom formula cannot be null");
    }

    @Override
    public Sequent<L, R> conclusion() {
        return new Sequent<>(List.of(formula), List.of(formula));
    }

    @Override
    public String ruleName() {
        return "Axiom";
    }

    @Override
    public List<Proof<?, ?>> premises() {
        return List.of();
    }
}
