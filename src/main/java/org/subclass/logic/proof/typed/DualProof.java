package org.subclass.logic.proof.typed;

import org.subclass.logic.proof.typed.capabilities.ContractionRight;
import org.subclass.logic.proof.typed.capabilities.ExchangeRight;
import org.subclass.logic.proof.typed.capabilities.WeakeningRight;

import java.util.Objects;

/**
 * Proof in paraconsistent dual logic (LDJ): single hypothesis, unrestricted conclusions.
 *
 * Paraconsistent dual logic is the structural dual of intuitionistic logic:
 * - The left side (antecedent) must contain exactly one formula
 * - The right side (succedent) is unrestricted
 * - All structural rules are allowed on the right (weakening, contraction, exchange)
 * - No structural rules on the left
 *
 * This restriction is dual to intuitionistic logic:
 * - Only one hypothesis (proof premise) is available at a time
 * - Conclusions can be freely manipulated
 *
 * Paraconsistent logic uses this to avoid Explosion (ex falso quodlibet).
 * With only one premise, certain contradictions cannot be exploited.
 *
 * DualProof implements WeakeningRight, ContractionRight, ExchangeRight,
 * making right-side operations available. It does NOT implement left-side capabilities,
 * making them compile-time errors to call.
 */
public record DualProof(Sequent<One, Many> conclusion)
    implements Proof<One, Many>,
               WeakeningRight<One, Many>,
               ContractionRight<One, Many>,
               ExchangeRight<One, Many> {

    /**
     * Construct a paraconsistent dual proof with the given sequent.
     *
     * @param conclusion the proven sequent with single left, unrestricted right
     * @throws IllegalArgumentException if sequent is null
     * @throws AssertionError if sequent violates cardinality (if assertions enabled)
     */
    public DualProof {
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");

        // Runtime assertion to catch violations at construction time
        assert conclusion.antecedent().size() == 1
            : "DualProof conclusion must have exactly one antecedent formula, got " + conclusion.antecedent().size();
    }
}
