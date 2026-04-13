package org.subclass.logic.proof.typed;

/**
 * Phantom type representing the cardinality (number of formulas allowed)
 * on a side of a sequent in a logical system.
 *
 * The cardinality constraint is enforced at **compile-time** via the type system:
 * - A Sequent<One, One> physically cannot hold multiple formulas
 * - Factory methods return types that match their cardinality guarantees
 * - Attempting to construct violations causes compile-time errors
 *
 * At runtime, formulas are still stored in lists, but the type parameter
 * serves as a proof that the list respects the cardinality invariant.
 *
 * Examples:
 * - Common Logic: Sequent<One, One> (additive: at most one formula per side)
 * - Intuitionistic Logic: Sequent<Many, One> (unrestricted left, single right)
 * - Classical Logic: Sequent<Many, Many> (unrestricted both sides)
 */
public sealed interface Cardinality permits Zero, One, Many {
    /**
     * Get a human-readable representation of this cardinality.
     */
    String description();
}

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

/**
 * Exactly one formula (additive junction constraint).
 * This cardinality is the defining restriction of intuitionistic logic (on right)
 * and common logic (on both sides).
 *
 * Semantically, One represents the additive junctions {&, ⊕} in linear logic terms:
 * - & (with): choose one of two premises
 * - ⊕ (plus): exactly one conclusion
 *
 * The constraint is enforced by factory methods that return Sequent<..., One>
 * with a type signature that guarantees the succedent has exactly one formula.
 */
public final class One implements Cardinality {
    public static final One INSTANCE = new One();

    private One() {
        // Sealed singleton
    }

    @Override
    public String description() {
        return "One (single formula)";
    }

    @Override
    public String toString() {
        return "One";
    }
}

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
