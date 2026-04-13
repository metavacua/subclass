package org.subclass.logic.proof.typed;

import java.util.Objects;

/**
 * Proof in common logic: the most restrictive logic with exactly one formula per side.
 *
 * Common logic is the intersection of intuitionistic logic (LJ) and its dual (LDJ).
 * It has no structural rules enabled:
 * - No weakening: cannot add unused hypotheses or conclusions
 * - No contraction: cannot remove duplicates
 * - No exchange: cannot reorder formulas
 *
 * This restriction forces every formula to be "relevant" - used in the proof.
 *
 * CommonProof intentionally does NOT implement any capability interfaces,
 * making it a compile-time error to call structural rule methods on it.
 */
public record CommonProof(Sequent<One, One> conclusion)
    implements Proof<One, One> {

    /**
     * Construct a common logic proof with the given sequent.
     *
     * @param conclusion the proven sequent with exactly one formula on each side
     * @throws IllegalArgumentException if sequent is null
     * @throws AssertionError if sequent violates cardinality (if assertions enabled)
     */
    public CommonProof {
        Objects.requireNonNull(conclusion, "Conclusion cannot be null");

        // Runtime assertion to catch any violations at construction time
        assert conclusion.antecedent().size() == 1
            : "CommonProof conclusion must have exactly one antecedent formula, got " + conclusion.antecedent().size();
        assert conclusion.succedent().size() == 1
            : "CommonProof conclusion must have exactly one succedent formula, got " + conclusion.succedent().size();
    }
}
