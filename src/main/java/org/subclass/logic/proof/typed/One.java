package org.subclass.logic.proof.typed;

/**
 * At most one formula on this side of the sequent: cardinality in {0, 1}.
 * This is the defining restriction of intuitionistic logic (on the right)
 * and the initial-object ("common") logic (on both sides).
 *
 * <p>The overall sequent must still be non-empty: the empty sequent
 * {@code ⊢} is excluded by the compact constructor of {@link Sequent}. Thus
 * the initial-object sequents inhabiting {@code Sequent<One,One>} are
 * exactly the three shapes {@code A ⊢ B}, {@code A ⊢}, and {@code ⊢ B}.
 *
 * <p>Semantically, One represents the additive junctions {&, ⊕} in linear
 * logic terms:
 * <ul>
 *   <li>{@code &} (with): choose at most one of two premises</li>
 *   <li>{@code ⊕} (plus): at most one conclusion</li>
 * </ul>
 *
 * The constraint is enforced by factory methods that return
 * {@code Sequent<..., One>} with a type signature that guarantees the
 * corresponding side has cardinality at most one.
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
