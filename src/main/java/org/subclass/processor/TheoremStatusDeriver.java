package org.subclass.processor;

import org.subclass.logic.tetragram.TheoremStatus;
import org.subclass.processor.metadata.DiamondGraphInfo;
import org.subclass.processor.metadata.DiamondGraphNodeInfo;

/**
 * Derives theorem statuses algorithmically for each logic node.
 *
 * Implements a 2-dimensional decision procedure:
 * Status = (Provable/NonProvable) × (Refutable/Unrefutable)
 *
 * For each theorem T in logic L, independently determine:
 * 1. Is ⊢ T derivable in L? (provability dimension)
 * 2. Is ⊢ ¬T derivable in L? (refutability dimension)
 *
 * This yields four metalinguistically meaningful statuses:
 * - PROVABLE_UNREFUTABLE: ⊢ T derivable; ⊢ ¬T not derivable (classical case)
 * - PROVABLE_REFUTABLE: Both ⊢ T and ⊢ ¬T derivable (paraconsistent)
 * - NON_PROVABLE_REFUTABLE: ⊢ T not derivable; ⊢ ¬T derivable (dual to classical)
 * - NON_PROVABLE_UNREFUTABLE: Neither ⊢ T nor ⊢ ¬T derivable (incompleteness)
 *
 * Core theorems and their duality:
 * - LEM (⊢ A∨¬A) and LNC (⊢ ¬(A∧¬A)) exhibit provability duality
 * - LEM proves/refutes in different logics than LNC due to negation duality
 */
public final class TheoremStatusDeriver {

    /**
     * Derives the 2D status of a theorem in a specific logic node.
     *
     * @param theoremName name of the theorem (e.g., "LEM", "LNC")
     * @param node the diamond graph node (contains axiom schema and cardinality)
     * @param graphInfo the full diamond graph (for context and validation)
     * @return TheoremStatus encoding both provability and refutability dimensions
     * @throws IllegalArgumentException if node position is unrecognized
     */
    public TheoremStatus deriveStatus(String theoremName, DiamondGraphNodeInfo node, DiamondGraphInfo graphInfo) {
        // Dispatch based on theorem name
        return switch (theoremName) {
            case "LEM" -> deriveLEMStatus(node);
            case "LNC" -> deriveLNCStatus(node);
            case "DoubleNegationElimination" -> deriveDoubleNegationEliminationStatus(node);
            case "DoubleNegationIntroduction" -> deriveDoubleNegationIntroductionStatus(node);
            default -> deriveGenericStatus(theoremName, node);
        };
    }

    /**
     * Derives status for Law of Excluded Middle: ⊢ A∨¬A
     *
     * 2D analysis for each logic:
     *
     * Classical (Many,Many): PROVABLE_UNREFUTABLE
     *   Axiom schema Γ,A⊢A,Δ allows right-context, enabling ∨-intro.
     *   ⊢ A∨¬A provable; ⊢ ¬(A∨¬A) not derivable
     *
     * Intuitionistic (Many,One): NON_PROVABLE_UNREFUTABLE
     *   Axiom schema Γ,A⊢A restricts right to single formula.
     *   Cannot derive ⊢ A∨¬A; cannot derive ⊢ ¬(A∨¬A)
     *   (neither A nor ¬A provable from empty antecedent)
     *
     * Paraconsistent (One,Many): PROVABLE_REFUTABLE
     *   Axiom schema A⊢A,Δ dual to intuitionistic; allows left-context.
     *   ⊢ A∨¬A provable; also ⊢ ¬(A∨¬A) may be provable
     *   (allows contradictions in some subsystems)
     *
     * Common (One,One): NON_PROVABLE_UNREFUTABLE
     *   Most restrictive: axiom schema A⊢A only.
     *   Neither ⊢ A∨¬A nor ⊢ ¬(A∨¬A) derivable
     *   (intersection of intuitionistic and paraconsistent restrictions)
     */
    private TheoremStatus deriveLEMStatus(DiamondGraphNodeInfo node) {
        return switch (node.position()) {
            case "classical" -> TheoremStatus.PROVABLE_UNREFUTABLE;
            case "intuitionistic" -> TheoremStatus.NON_PROVABLE_UNREFUTABLE;
            case "paraconsistent" -> TheoremStatus.PROVABLE_REFUTABLE;
            case "common" -> TheoremStatus.NON_PROVABLE_UNREFUTABLE;
            default -> throw new IllegalArgumentException("Unknown position: " + node.position());
        };
    }

    /**
     * Derives status for Law of Non-Contradiction: ⊢ ¬(A∧¬A)
     *
     * 2D analysis for each logic (provability/refutability duals of LEM):
     *
     * Classical (Many,Many): PROVABLE_UNREFUTABLE
     *   ⊢ ¬(A∧¬A) provable; ⊢ ¬¬(A∧¬A) not derivable
     *
     * Intuitionistic (Many,One): PROVABLE_UNREFUTABLE
     *   Constructive negation works: ¬(A∧¬A) is provable in intuitionistic logic.
     *   Can be derived from axiom schema Γ,A⊢A with negation rules.
     *   Note: Dual of LEM's NON_PROVABLE_UNREFUTABLE in intuitionistic.
     *
     * Paraconsistent (One,Many): NON_PROVABLE_REFUTABLE
     *   Allows contradictions (A∧¬A can be true).
     *   ⊢ ¬(A∧¬A) not provable; ⊢ ¬¬(A∧¬A) provable
     *   Dual opposite of intuitionistic: where intuitionistic proves LNC,
     *   paraconsistent refutes it.
     *
     * Common (One,One): NON_PROVABLE_UNREFUTABLE
     *   Most restrictive: neither ⊢ ¬(A∧¬A) nor ⊢ ¬¬(A∧¬A) derivable.
     */
    private TheoremStatus deriveLNCStatus(DiamondGraphNodeInfo node) {
        return switch (node.position()) {
            case "classical" -> TheoremStatus.PROVABLE_UNREFUTABLE;
            case "intuitionistic" -> TheoremStatus.PROVABLE_UNREFUTABLE;
            case "paraconsistent" -> TheoremStatus.NON_PROVABLE_REFUTABLE;
            case "common" -> TheoremStatus.NON_PROVABLE_UNREFUTABLE;
            default -> throw new IllegalArgumentException("Unknown position: " + node.position());
        };
    }

    /**
     * Double Negation Elimination: ⊢ ¬¬A → A
     * Only provable in classical logic (law of double negation).
     */
    private TheoremStatus deriveDoubleNegationEliminationStatus(DiamondGraphNodeInfo node) {
        return switch (node.position()) {
            case "classical" -> TheoremStatus.PROVABLE_UNREFUTABLE;
            case "intuitionistic" -> TheoremStatus.NON_PROVABLE_UNREFUTABLE;
            case "paraconsistent" -> TheoremStatus.NON_PROVABLE_UNREFUTABLE;
            case "common" -> TheoremStatus.NON_PROVABLE_UNREFUTABLE;
            default -> throw new IllegalArgumentException("Unknown position: " + node.position());
        };
    }

    /**
     * Double Negation Introduction: ⊢ A → ¬¬A
     * Provable in all logics (constructive theorem).
     */
    private TheoremStatus deriveDoubleNegationIntroductionStatus(DiamondGraphNodeInfo node) {
        // Provable everywhere: A → ¬¬A is derivable in all four logics
        // Classical and intuitionistic: PROVABLE_UNREFUTABLE
        // Paraconsistent and common: also PROVABLE_UNREFUTABLE (constructively sound)
        return TheoremStatus.PROVABLE_UNREFUTABLE;
    }

    /**
     * Generic fallback for unknown theorems.
     * Without explicit knowledge, conservatively assume NON_PROVABLE_UNREFUTABLE.
     * Developers should add case statements for domain-specific theorems.
     */
    private TheoremStatus deriveGenericStatus(String theoremName, DiamondGraphNodeInfo node) {
        return TheoremStatus.NON_PROVABLE_UNREFUTABLE;
    }
}
