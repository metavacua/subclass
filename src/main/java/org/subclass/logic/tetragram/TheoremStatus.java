package org.subclass.logic.tetragram;

/**
 * Two-dimensional theorem status model for proof and refutation calculi.
 *
 * Status is the Cartesian product of two independent dimensions:
 * - Provability: whether ⊢ T is derivable (PROVABLE vs NON_PROVABLE)
 * - Refutability: whether ⊢ ¬T is derivable (REFUTABLE vs UNREFUTABLE)
 *
 * This gives four metalinguistically meaningful states:
 *
 * 1. PROVABLE_UNREFUTABLE: ⊢ T derivable; ⊢ ¬T not derivable
 *    - Classical logic, intuitionistic logic (most theorems)
 *    - Pure proof calculi
 *
 * 2. PROVABLE_REFUTABLE: Both ⊢ T and ⊢ ¬T derivable
 *    - Paraconsistent logics (allow contradictions)
 *    - Inconsistent but coherent subsystems
 *
 * 3. NON_PROVABLE_REFUTABLE: ⊢ T not derivable; ⊢ ¬T derivable
 *    - Dual to state 1; appears in refutation-primary calculi
 *    - Pure refutation calculi
 *
 * 4. NON_PROVABLE_UNREFUTABLE: Neither ⊢ T nor ⊢ ¬T derivable
 *    - Paracomplete logics (reject both T and ¬T)
 *    - Metalinguistic signature of incompleteness/undecidability
 *    - Formal condition for Gödel incompleteness, consistency dilemmas
 *
 * In the object language of a specific logic L:
 * - States 1 & 3 represent definite closure (classical completeness)
 * - State 2 represents paraconsistent collapse (both provable)
 * - State 4 represents genuine undecidability (neither provable nor refutable)
 */
public enum TheoremStatus {
    /**
     * Provable and unrefutable: ⊢ T derivable; ⊢ ¬T not derivable.
     * Valid in classical and intuitionistic proof calculi.
     */
    PROVABLE_UNREFUTABLE("Provable and unrefutable"),

    /**
     * Provable and refutable: Both ⊢ T and ⊢ ¬T derivable.
     * Valid in paraconsistent logics that allow contradictions.
     * Signature of logical explosion containment.
     */
    PROVABLE_REFUTABLE("Provable and refutable"),

    /**
     * Non-provable and refutable: ⊢ T not derivable; ⊢ ¬T derivable.
     * Dual to PROVABLE_UNREFUTABLE.
     * Valid in refutation-primary calculi and some paracomplete systems.
     */
    NON_PROVABLE_REFUTABLE("Non-provable and refutable"),

    /**
     * Non-provable and unrefutable: Neither ⊢ T nor ⊢ ¬T derivable.
     * Metalinguistic signature of genuine incompleteness/undecidability.
     * Formal condition for Gödel incompleteness, consistency dilemmas.
     * Valid in paracomplete logics where both A and ¬A may be unprovable.
     */
    NON_PROVABLE_UNREFUTABLE("Non-provable and unrefutable");

    private final String displayName;

    TheoremStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this status represents a provable state (dimension 1).
     */
    public boolean isProvable() {
        return this == PROVABLE_UNREFUTABLE || this == PROVABLE_REFUTABLE;
    }

    /**
     * Check if this status represents a non-provable state (dimension 1).
     */
    public boolean isNonProvable() {
        return this == NON_PROVABLE_REFUTABLE || this == NON_PROVABLE_UNREFUTABLE;
    }

    /**
     * Check if this status represents a refutable state (dimension 2).
     */
    public boolean isRefutable() {
        return this == PROVABLE_REFUTABLE || this == NON_PROVABLE_REFUTABLE;
    }

    /**
     * Check if this status represents an unrefutable state (dimension 2).
     */
    public boolean isUnrefutable() {
        return this == PROVABLE_UNREFUTABLE || this == NON_PROVABLE_UNREFUTABLE;
    }

    /**
     * Check if this status represents determined closure (classical sense):
     * either provable unrefutable OR non-provable refutable (not both, not neither).
     */
    public boolean isDetermined() {
        return (this == PROVABLE_UNREFUTABLE) || (this == NON_PROVABLE_REFUTABLE);
    }

    /**
     * Check if this status represents undecidability/incompleteness:
     * neither provable nor refutable in the metalanguage.
     */
    public boolean isUndecidable() {
        return this == NON_PROVABLE_UNREFUTABLE;
    }

    /**
     * Check if this status represents contradiction in the object language:
     * both provable and refutable (paraconsistent collapse).
     */
    public boolean isContradictory() {
        return this == PROVABLE_REFUTABLE;
    }

    /**
     * Get the refutational dual of this status.
     * Swaps the refutability dimension.
     *
     * PROVABLE_UNREFUTABLE ↔ PROVABLE_REFUTABLE
     * NON_PROVABLE_REFUTABLE ↔ NON_PROVABLE_UNREFUTABLE
     */
    public TheoremStatus refutationalDual() {
        return switch (this) {
            case PROVABLE_UNREFUTABLE -> PROVABLE_REFUTABLE;
            case PROVABLE_REFUTABLE -> PROVABLE_UNREFUTABLE;
            case NON_PROVABLE_REFUTABLE -> NON_PROVABLE_UNREFUTABLE;
            case NON_PROVABLE_UNREFUTABLE -> NON_PROVABLE_REFUTABLE;
        };
    }

    /**
     * Get the provability dual of this status.
     * Swaps the provability dimension (used for negation duality).
     *
     * PROVABLE_UNREFUTABLE ↔ NON_PROVABLE_REFUTABLE
     * PROVABLE_REFUTABLE ↔ NON_PROVABLE_UNREFUTABLE
     */
    public TheoremStatus provabilityDual() {
        return switch (this) {
            case PROVABLE_UNREFUTABLE -> NON_PROVABLE_REFUTABLE;
            case PROVABLE_REFUTABLE -> NON_PROVABLE_UNREFUTABLE;
            case NON_PROVABLE_REFUTABLE -> PROVABLE_UNREFUTABLE;
            case NON_PROVABLE_UNREFUTABLE -> PROVABLE_REFUTABLE;
        };
    }
}
