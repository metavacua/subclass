package org.subclass.logic.proof.typed;

/**
 * Multiple formulas (unrestricted, structural junctions).
 * Represents the unrestricted sides in classical logic and the left side
 * of intuitionistic/common logic.
 *
 * Semantically, Many enables all three structural rules:
 * - Weakening (W): add unused formulas
 * - Contraction (C): remove duplicates
 * - Exchange (E): reorder formulas
 */
public final class Many implements Cardinality {
    public static final Many INSTANCE = new Many();

    private Many() {
        // Sealed singleton
    }

    @Override
    public String description() {
        return "Many (multiple formulas, full structural rules)";
    }

    @Override
    public String toString() {
        return "Many";
    }
}
