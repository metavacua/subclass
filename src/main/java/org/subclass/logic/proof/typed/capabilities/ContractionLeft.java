package org.subclass.logic.proof.typed.capabilities;

import org.subclass.logic.proof.typed.Cardinality;

/**
 * Capability interface indicating a proof supports left contraction.
 *
 * Contraction (C) is a structural rule that allows removing duplicate hypotheses:
 *   Γ, A, A ⊢ Δ
 *   ---------- (C-left)
 *   Γ, A ⊢ Δ
 *
 * A Proof class that implements this interface can apply left contraction.
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public interface ContractionLeft<L extends Cardinality, R extends Cardinality> {
    // Marker interface
}
