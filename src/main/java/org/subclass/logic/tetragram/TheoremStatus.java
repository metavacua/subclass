package org.subclass.logic.tetragram;

/**
 * Enum representing the status of a theorem in a logical system.
 *
 * In classical logic, a statement is either provable or its negation is provable.
 * However, in non-classical logics (paraconsistent, paracomplete), we have four
 * possible states:
 *
 * - PROVABLE: The theorem can be derived from the axioms and rules of the logic
 * - NON_PROVABLE: The theorem cannot be derived, but its negation may or may not be derivable
 * - REFUTABLE: The negation of the theorem can be derived
 * - UNPROVABLE_AND_REFUTABLE: Neither the theorem nor its negation can be derived
 *   (This occurs in paracomplete logics where neither A nor ¬A is derivable)
 *
 * Note: In complete logics (classical, some paraconsistent), UNPROVABLE_AND_REFUTABLE
 * should not occur - if not provable, then refutable or vice versa.
 * In paraconsistent logics, both PROVABLE and REFUTABLE can occur simultaneously.
 * In paracomplete logics, neither PROVABLE nor REFUTABLE can occur.
 */
public enum TheoremStatus {
    /**
     * The theorem is provable in this logic.
     * There exists a proof tree from axioms to the theorem.
     */
    PROVABLE("Provable"),

    /**
     * The theorem is not provable in this logic.
     * No proof exists (but the negation may or may not be provable).
     */
    NON_PROVABLE("Non-provable"),

    /**
     * The theorem is refutable in this logic.
     * The negation of the theorem can be derived.
     * In paraconsistent logics, a theorem can be both PROVABLE and REFUTABLE.
     */
    REFUTABLE("Refutable"),

    /**
     * The theorem is neither provable nor refutable in this logic.
     * This occurs in paracomplete logics where the logic lacks the law of excluded middle
     * or similar completeness properties.
     */
    UNPROVABLE_AND_REFUTABLE("Unprovable and refutable");

    private final String displayName;

    TheoremStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
