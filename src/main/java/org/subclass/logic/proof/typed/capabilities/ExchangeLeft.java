package org.subclass.logic.proof.typed.capabilities;

import org.subclass.logic.proof.typed.Cardinality;

/**
 * Capability interface indicating a proof supports left exchange.
 *
 * Exchange (E) is a structural rule that allows reordering hypotheses:
 *   Γ, A, B, Γ' ⊢ Δ
 *   ---------- (E-left)
 *   Γ, B, A, Γ' ⊢ Δ
 *
 * A Proof class that implements this interface can apply left exchange.
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public interface ExchangeLeft<L extends Cardinality, R extends Cardinality> {
    // Marker interface
}
