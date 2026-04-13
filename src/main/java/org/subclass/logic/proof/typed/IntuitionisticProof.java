package org.subclass.logic.proof.typed;

import org.subclass.logic.proof.typed.capabilities.ContractionLeft;
import org.subclass.logic.proof.typed.capabilities.ExchangeLeft;
import org.subclass.logic.proof.typed.capabilities.WeakeningLeft;

import java.util.Objects;

/**
 * Proof in intuitionistic logic (LJ): unrestricted left side, single conclusion.
 *
 * Intuitionistic logic has a single-conclusion restriction:
 * - The right side (succedent) must contain exactly one formula
 * - The left side (antecedent) is unrestricted
 * - All structural rules are allowed on the left (weakening, contraction, exchange)
 * - No structural rules on the right
 *
 * This restriction is fundamental to intuitionistic logic:
 * - Only one proof goal can be active at a time
 * - Hypotheses can be freely manipulated
 *
 * IntuitionisticProof implements WeakeningLeft, ContractionLeft, ExchangeLeft,
 * making these operations available. It does NOT implement right-side capabilities,
 * making them compile-time errors to call.
 */
public record IntuitionisticProof(Sequent<Many, One> conclusion)
    implements Proof<Many, One>,
               WeakeningLeft<Many, One>,
               ContractionLeft<Many, One>,
               ExchangeLeft<Many, One> {

    /**
     * Construct an intuitionistic proof with the given sequent.
     *
     * @param conclusion the proven sequent with unrestricted left, single right
     * @throws IllegalArgumentException if sequent is null
     * @throws AssertionError if sequent violates cardinality (if assertions enabled)
     */
    public IntuitionisticProof {
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");

        // Runtime assertion to catch violations at construction time
        assert conclusion.succedent().size() == 1
            : "IntuitionisticProof conclusion must have exactly one succedent formula, got " + conclusion.succedent().size();
    }
}
