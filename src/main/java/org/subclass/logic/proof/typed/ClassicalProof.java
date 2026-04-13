package org.subclass.logic.proof.typed;

import org.subclass.logic.proof.typed.capabilities.ContractionLeft;
import org.subclass.logic.proof.typed.capabilities.ContractionRight;
import org.subclass.logic.proof.typed.capabilities.ExchangeLeft;
import org.subclass.logic.proof.typed.capabilities.ExchangeRight;
import org.subclass.logic.proof.typed.capabilities.WeakeningLeft;
import org.subclass.logic.proof.typed.capabilities.WeakeningRight;

import java.util.Objects;

/**
 * Proof in classical logic (LK): unrestricted both sides.
 *
 * Classical logic has no restrictions on the number of formulas on either side:
 * - The left side (antecedent) is unrestricted
 * - The right side (succedent) is unrestricted
 * - All structural rules are allowed on both sides
 *
 * This makes classical logic fully commutative and associative for both conjunction
 * (left side) and disjunction (right side).
 *
 * ClassicalProof implements all six capability interfaces (WeakeningLeft/Right,
 * ContractionLeft/Right, ExchangeLeft/Right), making all structural operations available.
 */
public record ClassicalProof(Sequent<Many, Many> conclusion)
    implements Proof<Many, Many>,
               WeakeningLeft<Many, Many>,
               WeakeningRight<Many, Many>,
               ContractionLeft<Many, Many>,
               ContractionRight<Many, Many>,
               ExchangeLeft<Many, Many>,
               ExchangeRight<Many, Many> {

    /**
     * Construct a classical proof with the given sequent.
     *
     * @param conclusion the proven sequent with unrestricted both sides
     * @throws IllegalArgumentException if sequent is null
     */
    public ClassicalProof {
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");

        // Classical logic has no cardinality constraints to assert beyond non-null check
    }
}
