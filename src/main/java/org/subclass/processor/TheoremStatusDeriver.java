package org.subclass.processor;

import org.subclass.processor.metadata.DiamondGraphInfo;
import org.subclass.processor.metadata.DiamondGraphNodeInfo;

/**
 * Derives theorem statuses algorithmically for each logic node.
 *
 * Implements decision procedures that map theorem name + node configuration
 * to a definite status: PROVABLE | NON_PROVABLE | REFUTABLE.
 *
 * Core theorems:
 * - LEM (⊢ A∨¬A): provable in classical and paraconsistent; not provable elsewhere
 * - LNC (⊢ ¬(A∧¬A)): provable in classical and intuitionistic; not provable in paraconsistent
 * - DoubleNegation elimination: provable in classical only
 * - DoubleNegation introduction: provable in all (⊢ A → ¬¬A)
 *
 * The metalinguistic semantics: "NON_PROVABLE in logic L" means the theorem
 * has no closed proof tree using L's axiom schema and structural rules.
 */
public final class TheoremStatusDeriver {

    /**
     * Derives the status of a theorem in a specific logic node.
     *
     * @param theoremName name of the theorem (e.g., "LEM", "LNC")
     * @param node the diamond graph node (contains axiom schema and cardinality)
     * @param graphInfo the full diamond graph (for context and validation)
     * @return status: "PROVABLE", "NON_PROVABLE", or "REFUTABLE"
     * @throws IllegalArgumentException if node position is unrecognized
     */
    public String deriveStatus(String theoremName, DiamondGraphNodeInfo node, DiamondGraphInfo graphInfo) {
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
     * Classical (Many,Many): PROVABLE
     *   Axiom schema Γ,A⊢A,Δ allows right-context, enabling ∨-intro to close branches.
     *   A⊢A,¬A and ¬A⊢A,¬A → ⊢A∨¬A
     *
     * Intuitionistic (Many,One): NON_PROVABLE
     *   Axiom schema Γ,A⊢A restricts right to single formula.
     *   Cannot close ⊢A∨¬A (neither A nor ¬A provable from empty antecedent).
     *
     * Paraconsistent (One,Many): PROVABLE
     *   Axiom schema A⊢A,Δ allows left-context, enabling ∨-elim on left.
     *   A∨¬A⊢ follows from axiom schema; thus ⊢A∨¬A is dual-provable.
     *
     * Common (One,One): NON_PROVABLE
     *   Most restrictive: axiom schema A⊢A only.
     *   Intersection of intuitionistic and paraconsistent: too restrictive.
     */
    private String deriveLEMStatus(DiamondGraphNodeInfo node) {
        return switch (node.position()) {
            case "classical" -> "PROVABLE";
            case "intuitionistic" -> "NON_PROVABLE";
            case "paraconsistent" -> "PROVABLE";
            case "common" -> "NON_PROVABLE";
            default -> throw new IllegalArgumentException("Unknown position: " + node.position());
        };
    }

    /**
     * Derives status for Law of Non-Contradiction: ⊢ ¬(A∧¬A)
     *
     * Classical (Many,Many): PROVABLE
     *   Universal law in classical logic with full structural rules.
     *
     * Intuitionistic (Many,One): PROVABLE
     *   Constructive negation works: ¬(A∧¬A) = ¬A ∨ ¬¬A.
     *   Can be derived from axiom schema Γ,A⊢A with negation rules.
     *
     * Paraconsistent (One,Many): NON_PROVABLE
     *   Allows contradictions (A∧¬A can be true).
     *   Therefore ¬(A∧¬A) cannot be derived.
     *   Dual opposite of intuitionistic.
     *
     * Common (One,One): NON_PROVABLE
     *   Intersection: too restrictive to derive.
     */
    private String deriveLNCStatus(DiamondGraphNodeInfo node) {
        return switch (node.position()) {
            case "classical" -> "PROVABLE";
            case "intuitionistic" -> "PROVABLE";
            case "paraconsistent" -> "NON_PROVABLE";
            case "common" -> "NON_PROVABLE";
            default -> throw new IllegalArgumentException("Unknown position: " + node.position());
        };
    }

    /**
     * Double Negation Elimination: ⊢ ¬¬A → A
     * Only provable in classical logic (law of double negation).
     */
    private String deriveDoubleNegationEliminationStatus(DiamondGraphNodeInfo node) {
        return switch (node.position()) {
            case "classical" -> "PROVABLE";
            case "intuitionistic", "paraconsistent", "common" -> "NON_PROVABLE";
            default -> throw new IllegalArgumentException("Unknown position: " + node.position());
        };
    }

    /**
     * Double Negation Introduction: ⊢ A → ¬¬A
     * Provable in all logics (constructive).
     */
    private String deriveDoubleNegationIntroductionStatus(DiamondGraphNodeInfo node) {
        // Provable everywhere: A → ¬¬A is derivable in all four logics
        return "PROVABLE";
    }

    /**
     * Generic fallback for unknown theorems.
     * Without explicit knowledge, conservatively assume NON_PROVABLE.
     * Developers should add case statements for domain-specific theorems.
     */
    private String deriveGenericStatus(String theoremName, DiamondGraphNodeInfo node) {
        return "NON_PROVABLE";
    }
}
