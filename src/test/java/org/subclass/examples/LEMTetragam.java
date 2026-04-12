package org.subclass.examples;

import org.subclass.annotation.Theorem;
import org.subclass.annotation.TheoremFamily;
import org.subclass.logic.signature.LogicalSignature;
import org.subclass.logic.tetragram.Tetragram;
import org.subclass.logic.tetragram.TetragamNode;
import org.subclass.logic.tetragram.TheoremStatus;

/**
 * Case study 1: Law of Excluded Middle (LEM) formalized across a tetragram.
 *
 * LEM is the statement: A ∨ ¬A (for any proposition A)
 *
 * This class demonstrates how LEM has different provability statuses
 * across four logically related systems forming a diamond/tetragram:
 *
 *        LK: LEM✓              ← Classical logic, <Consistent, Complete>
 *         ↗              ↖
 *   LJ: LEM✗  ↔  LDJ: LEM✓     ← Intuitionistic vs Paraconsistent
 *         ↖              ↗      ← <Consistent, Paracomplete> × <Paraconsistent, Complete>
 *     Common: LEM?              ← Intersection, <Paraconsistent, Paracomplete>
 *
 * **LEM TETRAGRAM** (Separate from LNC tetragram):
 * - LEM is PROVABLE in: {LK, LDJ}
 * - LEM is NON_PROVABLE in: {LJ}
 * - LEM is UNPROVABLE_AND_REFUTABLE in: {Common}
 *
 * **Relationship to LNC**: LNC has a DUAL tetragram where:
 * - LNC is PROVABLE in: {LK, LJ}
 * - LNC is NON_PROVABLE in: {LDJ}
 * - LNC is UNPROVABLE_AND_REFUTABLE in: {Common}
 *
 * The duality is mediated by double negation operators.
 * Neither LEM nor LNC are provable in the common logic.
 */
@TheoremFamily(
    name = "LEM",
    displayName = "Law of Excluded Middle",
    classicalTheorem = "lemInLK",
    intuitionisticTheorem = "lemInLJ",
    paraconsistentTheorem = "lemInLDJ",
    commonLogicTheorem = "lemInCommon",
    description = "The tetragram of LEM across classical (LK), intuitionistic (LJ), " +
                  "paraconsistent (LDJ), and common logics. " +
                  "Key insight: LEM is PROVABLE in both LK and LDJ (paraconsistent), " +
                  "but NOT in LJ. This reflects the complementary nature of LEM vs LNC across logics."
)
public class LEMTetragam {

    /**
     * LEM in LK (Classical Sequent Calculus)
     * Status: PROVABLE
     *
     * In classical logic, LEM is a fundamental theorem.
     * Proof: The sequent (⊢ A ∨ ¬A) is derivable.
     *
     * Classical logic assumes:
     * - Full set of structural rules (W, C, E)
     * - Functionally complete connectives
     * - Law of Excluded Middle as a derived theorem
     *
     * The proof works by cases on the value of A:
     * 1. If A is true, then (A ∨ ¬A) is true
     * 2. If A is false, then ¬A is true, so (A ∨ ¬A) is true
     *
     * This captures the classical principle: every proposition is either true or false.
     */
    @Theorem(
        name = "LEM_in_LK",
        signature = "LK",
        status = "PROVABLE",
        proofReference = "Gentzen (1935). Classical sequent calculus derives (⊢ A ∨ ¬A) from empty assumptions.",
        description = "Law of Excluded Middle is provable in classical logic LK. " +
                      "The classical two-valued semantics makes this a tautology."
    )
    public static void lemInLK() {
        // This method is purely a container for annotation metadata.
        // The actual proof exists in the referenced publication.
        // Proof sketch: (A ∨ ¬A) is true under all valuations in classical logic.
    }

    /**
     * LEM in LJ (Intuitionistic Sequent Calculus)
     * Status: NON_PROVABLE
     *
     * In intuitionistic logic, LEM is NOT provable.
     * This is one of the key differences between intuitionistic and classical logic.
     *
     * Intuitionistic logic restricts the right-hand side of sequents to at most
     * one formula (single-conclusion property).
     *
     * Why LEM fails in LJ:
     * - To prove (A ∨ ¬A), we must prove either A or ¬A
     * - Neither can be proven from empty assumptions in general
     * - The restriction prevents the case analysis that works in classical logic
     * - Intuitionistic logic requires constructive evidence
     *
     * This reflects intuitionistic philosophy: we can only assert A or ¬A if we have
     * constructive proof of one of them. The mere absence of a proof for A doesn't
     * constitute a proof of ¬A in intuitionistic logic.
     */
    @Theorem(
        name = "LEM_in_LJ",
        signature = "LJ",
        status = "NON_PROVABLE",
        proofReference = "Heyting (1956). Intuitionistic logic cannot prove (⊢ A ∨ ¬A). " +
                         "See also: Gödel's intuitionistic interpretation.",
        description = "Law of Excluded Middle is NOT provable in intuitionistic logic LJ. " +
                      "This reflects the constructive nature of intuitionistic reasoning."
    )
    public static void lemInLJ() {
        // This method documents the absence of a proof.
        // The non-provability is proven via countermodels in Kripke semantics.
    }

    /**
     * LEM in LDJ (Urbas-Rauszer Paraconsistent Dual)
     * Status: PROVABLE
     *
     * CRITICAL INSIGHT: LEM IS PROVABLE in LDJ (the paraconsistent dual of LJ).
     * This is the exact dual of LJ's behavior: where LJ fails to prove LEM,
     * LDJ succeeds in proving it.
     *
     * LDJ is obtained by syntactically exchanging left and right sides from LJ:
     * - Single-formula restriction on the LEFT (instead of right as in LJ)
     * - Makes the logic paraconsistent (doesn't validate explosion)
     * - Law of Non-Contradiction (LNC: ¬(A ∧ ¬A)) is NOT provable (dual to LEM in LJ)
     *
     * The negation structure in LDJ:
     * - Has double negation elimination (A ⊢ ¬¬A): PRESENT
     * - Lacks double negation introduction (¬¬A ⊢ A): ABSENT
     * This is symmetric to LJ which has introduction but not elimination.
     *
     * Proof of LEM in LDJ: Uses the paraconsistent structure to establish (A ∨ ¬A)
     * through double negation elimination without requiring classical excluded middle.
     */
    @Theorem(
        name = "LEM_in_LDJ",
        signature = "LDJ",
        status = "PROVABLE",
        proofReference = "Urbas & Rauszer (1990). Paraconsistent logic LDJ proves LEM. " +
                         "This is the exact dual of LJ's non-provability of LEM. " +
                         "Conversely, LDJ does NOT prove LNC, which LJ does prove.",
        description = "Law of Excluded Middle IS provable in the paraconsistent dual LDJ. " +
                      "This demonstrates the fundamental tetragram duality: " +
                      "LEM provable in LK and LDJ, not provable in LJ and Common. " +
                      "LNC is the inverse: provable in LK and LJ, not provable in LDJ and Common."
    )
    public static void lemInLDJ() {
        // This method documents the dual nature of LEM and LNC across the tetragram.
        // LEM in LDJ is provable because LDJ has double negation elimination.
        // LNC is unprovable in LDJ for the same reason it's unprovable in paraconsistent logics.
    }

    /**
     * LEM in Common Logic (Intersection)
     * Status: NEITHER PROVABLE NOR REFUTABLE
     *
     * In the common logic (intersection of LJ and LDJ), LEM is neither provable nor refutable.
     *
     * CRITICAL LOGICAL CONSTRAINT:
     * - LEM cannot be REFUTABLE in common logic because LEM is PROVABLE in LDJ and LK
     *   (which are extensions of common logic). If LEM were refutable in common logic,
     *   this would create a contradiction: the negation of LEM would be provable in the
     *   intersection, thus provable in all extensions, but it's not provable in LDJ.
     * - LEM cannot be PROVABLE in common logic for symmetric reasons: it's not provable
     *   in LJ (which is an extension), so it cannot be provable in their intersection.
     *
     * Therefore, LEM is simply UNDETERMINED in common logic:
     * - Neither (A ∨ ¬A) nor ¬(A ∨ ¬A) is derivable
     * - No contradiction arises from either assumption
     * - The proposition is logically independent in the common logic
     *
     * This is consistent with common logic being the most conservative:
     * it contains only theorems that are theorems in ALL four logics.
     */
    @Theorem(
        name = "LEM_in_Common",
        signature = "Common",
        status = "UNPROVABLE_AND_REFUTABLE",
        proofReference = "Common logic is the intersection of all four logics. " +
                         "LEM is undetermined here: if it were provable, it would be provable in LJ (contradiction). " +
                         "If it were refutable, it would be refutable in LDJ (contradiction). " +
                         "Therefore it is neither.",
        description = "Law of Excluded Middle is neither provable nor refutable in common logic. " +
                      "This reflects the logical independence of LEM in the most restrictive logic."
    )
    public static void lemInCommon() {
        // LEM is undetermined in common logic by logical necessity:
        // Its provability in extensions (LDJ, LK) forbids refutability here.
        // Its non-provability in extensions (LJ) forbids provability here.
    }

    /**
     * Construct and validate the LEM tetragram.
     * This demonstrates how to programmatically build the tetragram
     * and verify its consistency.
     *
     * The corrected tetragram based on Urbas-Rauszer semantics:
     * - LEM is PROVABLE in LK (classical): LEM ⊢ A ∨ ¬A
     * - LEM is NON_PROVABLE in LJ (intuitionistic): lacks LEM due to right-restriction
     * - LEM is PROVABLE in LDJ (paraconsistent): has double negation elimination
     * - LEM is UNPROVABLE_AND_REFUTABLE in Common: lacks all negation structure
     *
     * Dual theorem (LNC - Law of Non-Contradiction):
     * - LNC is PROVABLE in LK: classical
     * - LNC is PROVABLE in LJ: intuitionistic (you can prove ¬(A ∧ ¬A))
     * - LNC is NON_PROVABLE in LDJ: paraconsistent lacks LNC
     * - LNC is UNPROVABLE_AND_REFUTABLE in Common: undetermined
     */
    public static Tetragram<String> buildLEMTetragram() {
        // Create the four signatures
        LogicalSignature lkSig = SignatureDefinitions.createLKSignature();
        LogicalSignature ljSig = SignatureDefinitions.createLJSignature();
        LogicalSignature ldjSig = SignatureDefinitions.createLDJSignature();
        LogicalSignature commonSig = SignatureDefinitions.createCommonLogicSignature();

        // Create the four tetragram nodes
        TetragamNode classicalNode = new TetragamNode(true, true, lkSig,
            "LK (Classical): LEM is provable, LNC is provable");
        TetragamNode intuitionisticNode = new TetragamNode(true, false, ljSig,
            "LJ (Intuitionistic): LEM is NOT provable, LNC is provable");
        TetragamNode paraconsistentCompleteNode = new TetragamNode(false, true, ldjSig,
            "LDJ (Paraconsistent): LEM is provable, LNC is NOT provable (Urbas-Rauszer)");
        TetragamNode commonLogicNode = new TetragamNode(false, false, commonSig,
            "Common Logic: Both LEM and LNC are undetermined");

        // Create the tetragram
        Tetragram<String> tetragram = new Tetragram<>(
            "LEM",
            classicalNode,
            intuitionisticNode,
            paraconsistentCompleteNode,
            commonLogicNode
        );

        // Register the theorem with its status in each node (corrected)
        tetragram.registerTheorem("LEM",
            TheoremStatus.PROVABLE,                    // LK: provable
            TheoremStatus.NON_PROVABLE,                // LJ: not provable
            TheoremStatus.PROVABLE,                    // LDJ: provable (CORRECTED from NON_PROVABLE)
            TheoremStatus.UNPROVABLE_AND_REFUTABLE     // Common: undetermined
        );

        return tetragram;
    }

    /**
     * Demonstrate the LEM tetragram and its properties.
     */
    public static void main(String[] args) {
        System.out.println("=== LEM Tetragram ===\n");

        Tetragram<String> lem = buildLEMTetragram();

        // Print theorem status across all nodes
        System.out.println(lem.summarizeTheorem("LEM"));

        // Validate the tetragram constraints
        System.out.println("\n=== Tetragram Validation ===");
        System.out.println("Duality constraint satisfied: " +
            lem.validateDualityConstraint("LEM"));
        System.out.println("Completeness constraint satisfied: " +
            lem.validateCompletenessConstraint("LEM"));

        // Print node details
        System.out.println("\n=== Node Details ===");
        for (TetragamNode node : lem.getAllNodes()) {
            System.out.println(node);
            System.out.println("  Signature: " + node.getSignature().getDisplayName());
            System.out.println("  Functionally complete: " +
                node.getSignature().isFunctionallyComplete());
            System.out.println("  LEM status: " + lem.getTheoremStatus("LEM", node));
            System.out.println();
        }
    }
}
