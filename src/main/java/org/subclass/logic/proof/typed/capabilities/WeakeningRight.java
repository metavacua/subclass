package org.subclass.logic.proof.typed.capabilities;

import org.subclass.logic.proof.typed.Cardinality;

/**
 * Capability interface indicating a proof supports right weakening.
 *
 * Weakening (W) is a structural rule that allows adding unused conclusions:
 *   Γ ⊢ Δ
 *   ---------- (W-right)
 *   Γ ⊢ Δ, A
 *
 * A Proof class that implements this interface can apply right weakening.
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public interface WeakeningRight<L extends Cardinality, R extends Cardinality> {
    // Marker interface
}
