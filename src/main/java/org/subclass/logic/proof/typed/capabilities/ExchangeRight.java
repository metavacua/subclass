package org.subclass.logic.proof.typed.capabilities;

import org.subclass.logic.proof.typed.Cardinality;

/**
 * Capability interface indicating a proof supports right exchange.
 *
 * Exchange (E) is a structural rule that allows reordering conclusions:
 *   Γ ⊢ Δ, A, B, Δ'
 *   ---------- (E-right)
 *   Γ ⊢ Δ, B, A, Δ'
 *
 * A Proof class that implements this interface can apply right exchange.
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public interface ExchangeRight<L extends Cardinality, R extends Cardinality> {
    // Marker interface
}
