package org.subclass.examples;

import org.subclass.annotation.Theorem;
import org.subclass.annotation.TheoremFamily;
import org.subclass.logic.signature.LogicalSignature;
import org.subclass.logic.tetragram.Tetragram;
import org.subclass.logic.tetragram.TetragamNode;
import org.subclass.logic.tetragram.TheoremStatus;

/**
 * Case study: Law of Excluded Middle (LEM) formalized across a tetragram.
 *
 * LEM is the statement: A ∨ ¬A (for any proposition A)
 *
 * This class demonstrates how LEM has different provability statuses
 * across four logically related systems forming a diamond/tetragram:
 *
 * LK (LEM provable)          ← Classical logic, <Consistent, Complete>
 *     ↗                ↖
 *  LJ  ↔  Dual(LJ)           ← Intuitionistic and paraconsistent, <Consistent, Paracomplete> × <Paraconsistent, Complete>
 *     ↖                ↗
 *   Common (neither)          ← Intersection, <Paraconsistent, Paracomplete>
 *
 * The meta-theorem is: How theorem status varies systematically across the tetragram
 * reflects the deep structure of logical systems and their relationships.
 */
@TheoremFamily(
    name = "LEM",
    displayName = "Law of Excluded Middle",
    classicalTheorem = "lemInLK",
    intuitionisticTheorem = "lemInLJ",
    paraconsistentTheorem = "lemInDual",
    commonLogicTheorem = "lemInCommon",
    description = "The tetragram of LEM across classical, intuitionistic, paraconsistent, and common logics. " +
                  "Demonstrates how a single theorem participates in different provability relationships depending on the logic."
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
     * LEM in Dual(LJ) (Paraconsistent Dual)
     * Status: NON_PROVABLE
     *
     * In the paraconsistent dual of LJ, LEM is also NOT provable.
     * This logic is obtained by syntactically exchanging left and right sides
     * and restricted contexts (now on the left instead of right).
     *
     * The dual has:
     * - Single-formula restriction on the LEFT (instead of right as in LJ)
     * - Makes the logic paraconsistent (explosion rule doesn't apply)
     * - Law of Non-Contradiction (LNC: ¬(A ∧ ¬A)) is not provable
     *
     * By duality, the dual's inability to prove LEM mirrors LJ's inability.
     * This demonstrates the tetragram's symmetry: what is unprovable in one
     * dimension (right restriction) has a dual that is unprovable in the
     * perpendicular dimension (left restriction).
     */
    @Theorem(
        name = "LEM_in_Dual",
        signature = "Dual(LJ)",
        status = "NON_PROVABLE",
        proofReference = "Duality principle: dual(LJ) obtains by swapping left/right roles. " +
                         "By duality, non-provability of LEM in LJ implies non-provability in dual.",
        description = "Law of Excluded Middle is NOT provable in the paraconsistent dual of LJ. " +
                      "This demonstrates the structural duality of the tetragram."
    )
    public static void lemInDual() {
        // This method documents the consequence of duality.
        // The dual logic is the mirror image of LJ along the tetragram structure.
    }

    /**
     * LEM in Common Logic (Intersection)
     * Status: UNPROVABLE_AND_REFUTABLE
     *
     * In the common logic (intersection of LJ and its dual), LEM is neither
     * provable nor refutable.
     *
     * The common logic is:
     * - Paraconsistent AND paracomplete
     * - Has minimal or no structural rules
     * - The most conservative logic embeddable in all four
     *
     * In this logic:
     * - LEM cannot be proven (inherited from LJ's restriction)
     * - ¬LEM also cannot be proven (inherited from dual's restriction)
     * - Neither (A ∨ ¬A) nor ¬(A ∨ ¬A) is derivable
     *
     * This is the defining characteristic of a paracomplete logic:
     * some propositions are neither provable nor refutable.
     * The common logic is the natural meeting point of the tetragram.
     */
    @Theorem(
        name = "LEM_in_Common",
        signature = "Common",
        status = "UNPROVABLE_AND_REFUTABLE",
        proofReference = "Common logic is the intersection of all four logics in the tetragram. " +
                         "Propositions undetermined in both LJ and Dual(LJ) remain undetermined here.",
        description = "Law of Excluded Middle is neither provable nor refutable in common logic. " +
                      "This demonstrates the paracomplete nature of the common logic node."
    )
    public static void lemInCommon() {
        // This method documents the undetermined status in common logic.
        // Common logic is the most restrictive: it has no theorems in many cases.
    }

    /**
     * Construct and validate the LEM tetragram.
     * This demonstrates how to programmatically build the tetragram
     * and verify its consistency.
     */
    public static Tetragram<String> buildLEMTetragram() {
        // Create the four signatures
        LogicalSignature lkSig = SignatureDefinitions.createLKSignature();
        LogicalSignature ljSig = SignatureDefinitions.createLJSignature();
        LogicalSignature dualSig = SignatureDefinitions.createDualOfLJSignature();
        LogicalSignature commonSig = SignatureDefinitions.createCommonLogicSignature();

        // Create the four tetragram nodes
        TetragamNode classicalNode = new TetragamNode(true, true, lkSig,
            "Classical logic: LEM is provable");
        TetragamNode intuitionisticNode = new TetragamNode(true, false, ljSig,
            "Intuitionistic logic: LEM is not provable");
        TetragamNode paraconsistentCompleteNode = new TetragamNode(false, true, dualSig,
            "Paraconsistent complete: LEM is not provable");
        TetragamNode commonLogicNode = new TetragamNode(false, false, commonSig,
            "Common logic: LEM is undetermined");

        // Create the tetragram
        Tetragram<String> tetragram = new Tetragram<>(
            "LEM",
            classicalNode,
            intuitionisticNode,
            paraconsistentCompleteNode,
            commonLogicNode
        );

        // Register the theorem with its status in each node
        tetragram.registerTheorem("LEM",
            TheoremStatus.PROVABLE,           // LK: provable
            TheoremStatus.NON_PROVABLE,       // LJ: not provable
            TheoremStatus.NON_PROVABLE,       // Dual: not provable
            TheoremStatus.UNPROVABLE_AND_REFUTABLE  // Common: undetermined
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
