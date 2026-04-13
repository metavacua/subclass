package org.subclass.logic.proof.typed.capabilities;

import org.subclass.logic.proof.typed.Cardinality;

/**
 * Capability interface indicating a proof supports left weakening.
 *
 * Weakening (W) is a structural rule that allows adding unused hypotheses:
 *   Γ ⊢ Δ
 *   ---------- (W-left)
 *   Γ, A ⊢ Δ
 *
 * A Proof class that implements this interface can apply left weakening.
 * By default, only the left side can be weakened (hence "WeakeningLeft").
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public interface WeakeningLeft<L extends Cardinality, R extends Cardinality> {
    // Marker interface: presence of this interface on a Proof class indicates
    // that left weakening is available. Specific methods can be added later.
}
