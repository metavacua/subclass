package org.subclass.logic.proof.typed.capabilities;

import org.subclass.logic.proof.typed.Cardinality;

/**
 * Capability interface indicating a proof supports right contraction.
 *
 * Contraction (C) is a structural rule that allows removing duplicate conclusions:
 *   Γ ⊢ Δ, A, A
 *   ---------- (C-right)
 *   Γ ⊢ Δ, A
 *
 * A Proof class that implements this interface can apply right contraction.
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public interface ContractionRight<L extends Cardinality, R extends Cardinality> {
    // Marker interface
}
