package org.subclass.logic.proof.typed;

import java.util.List;

/**
 * A reified inference-rule application: a proof-tree node carrying its rule,
 * its premises, and its conclusion.
 *
 * Where {@link CommonProof}, {@link IntuitionisticProof}, {@link ClassicalProof},
 * {@link DualProof} are *certificate* wrappers (they record only the sequent
 * that is derivable), a {@code ProofNode} additionally remembers **how** the
 * sequent was derived — which rule and from which premises.
 *
 * Because {@code ProofNode} extends {@code Proof<L,R>}, every reified rule
 * application *is itself* a proof: factory methods in
 * {@link org.subclass.logic.rules.InferenceRules} return {@code ProofNode}
 * instances typed as {@code Proof<L,R>}, so proof trees can be composed by
 * the same factories that used to return only certificates.
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public non-sealed interface ProofNode<L extends Cardinality, R extends Cardinality>
    extends Proof<L, R> {

    /**
     * The name of the rule applied at this node (matches {@link org.subclass.annotation.RuleSpec#name()}).
     */
    String ruleName();

    /**
     * The list of proof premises consumed by this rule. Zero for axioms,
     * one or two for the propositional rules of this first pass.
     */
    List<Proof<?, ?>> premises();
}
