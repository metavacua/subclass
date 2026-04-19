package org.subclass.logic.proof.typed;

/**
 * Zero cardinality: represents absence of formulas on a sequent side.
 * Used in sequent constraints where the initial node (intuitionistic ∩ dual-intuitionistic)
 * requires empty consequents for unprovability derivations (Γ ⊢ {}),
 * and in diamond graph validation to enforce cardinality constraints across logical matrices.
 * Sealed singleton member of {@link Cardinality}.
 */
public final class Zero implements Cardinality {
    public static final Zero INSTANCE = new Zero();

    private Zero() {
        // Sealed singleton
    }

    @Override
    public String description() {
        return "Zero (empty)";
    }

    @Override
    public String toString() {
        return "Zero";
    }
}
