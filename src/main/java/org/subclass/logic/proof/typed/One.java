package org.subclass.logic.proof.typed;

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
