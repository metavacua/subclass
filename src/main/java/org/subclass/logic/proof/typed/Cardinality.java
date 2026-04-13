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
