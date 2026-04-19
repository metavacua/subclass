package org.subclass.logic.signature;

import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.One;

import java.util.Set;

/**
 * A logic, characterized structurally by its reflexive axiom schema, its
 * admitted inference rules, and its antecedent / succedent cardinalities.
 *
 * <p>This is the object the diamond graph ranges over: the four primary
 * nodes of the propositional diamond are the four sealed implementations
 * {@link CommonLogic}, {@link IntuitionisticLJ}, {@link DualLJ}, and
 * {@link ClassicalLK}. A logic is identified by its signature (the four
 * characterizing pieces together), not by its arbitrary name.
 *
 * <p>Phantom-typed {@code Proof<L, R>} values remain the within-logic
 * shape check; {@code Logic} is the cross-node identity under which
 * morphisms between nodes of the diamond operate.
 */
public sealed interface Logic
    permits CommonLogic, IntuitionisticLJ, DualLJ, ClassicalLK {

    /** The reflexive axiom schema admitted by this logic. */
    ReflexiveAxiomSchema axiomSchema();

    /** The admitted structural + connective inference rules, by rule name. */
    Set<String> admittedRules();

    /** Antecedent cardinality: {@link One} or {@link Many}. */
    Class<? extends Cardinality> antecedentCardinality();

    /** Succedent cardinality: {@link One} or {@link Many}. */
    Class<? extends Cardinality> succedentCardinality();

    /**
     * A short canonical label ("Common", "LJ", "LDJ", "LK"). Not the logic's
     * identity — signature is — but a convenience for diagnostics.
     */
    String label();
}
