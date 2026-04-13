package org.subclass.logic.proof.typed;

/**
 * Zero formulas (used for initial/absurd sequents, or specialized contexts).
 * Represents an empty side of a sequent.
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
