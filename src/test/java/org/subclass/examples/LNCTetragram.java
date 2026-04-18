package org.subclass.examples;

import org.subclass.annotation.Theorem;
import org.subclass.annotation.TheoremFamily;
import org.subclass.logic.signature.LogicalSignature;
import org.subclass.logic.tetragram.Tetragram;
import org.subclass.logic.tetragram.TetragramNode;
import org.subclass.logic.tetragram.TheoremStatus;

/**
 * Case study 2: Law of Non-Contradiction (LNC) formalized across a tetragram.
 *
 * LNC is the statement: ¬(A ∧ ¬A) (no proposition can be both true and false)
 *
 * This class demonstrates how LNC has different provability statuses
 * across four logically related systems, forming the DUAL tetragram to LEM.
 *
 * **LNC TETRAGRAM** (Dual to LEM tetragram):
 *        LK: LNC✓              ← Classical logic, <Consistent, Complete>
 *         ↗              ↖
 *   LJ: LNC✓  ↔  LDJ: LNC✗     ← Intuitionistic vs Paraconsistent
 *         ↖              ↗      ← <Consistent, Paracomplete> × <Paraconsistent, Complete>
 *     Common: LNC?              ← Intersection, <Paraconsistent, Paracomplete>
 *
 * **DUALITY RELATIONSHIP TO LEM:**
 *
 * LEM Tetragram:          LNC Tetragram:
 * LK: LEM✓               LK: LNC✓
 * LJ: LEM✗               LJ: LNC✓         ← Opposite corners!
 * LDJ: LEM✓              LDJ: LNC✗        ← Opposite corners!
 * Common: LEM?           Common: LNC?
 *
 * LEM and LNC are DUAL theorems:
 * - Where LEM is PROVABLE, LNC is often NOT
 * - Where LEM is NOT PROVABLE, LNC is often PROVABLE
 * - Both are UNDETERMINED in common logic
 *
 * The duality is mediated by the structure of double negation operators.
 */
@TheoremFamily(
    name = "LNC",
    displayName = "Law of Non-Contradiction",
    classicalTheorem = "lncInLK",
    intuitionisticTheorem = "lncInLJ",
    paraconsistentTheorem = "lncInLDJ",
    commonLogicTheorem = "lncInCommon",
    description = "The tetragram of LNC (dual to LEM) across classical, intuitionistic, " +
                  "paraconsistent, and common logics. Shows how LNC and LEM are complementary theorems."
)
public class LNCTetragram {

    /**
     * LNC in LK (Classical Sequent Calculus)
     * Status: PROVABLE
     *
     * In classical logic, LNC is a fundamental theorem.
     * The principle that nothing can be both true and false is a cornerstone of classical logic.
     *
     * Proof: The sequent (⊢ ¬(A ∧ ¬A)) is derivable from classical axioms.
     * Classical logic assumes the law of non-contradiction as axiomatic.
     */
    @Theorem(
        name = "LNC_in_LK",
        signature = "LK",
        status = "PROVABLE",
        proofReference = "Classical logic axiomatically includes the law of non-contradiction.",
        description = "Law of Non-Contradiction is provable in classical logic LK."
    )
    public static void lncInLK() {
    }

    /**
     * LNC in LJ (Intuitionistic Sequent Calculus)
     * Status: PROVABLE
     *
     * Importantly, LNC IS PROVABLE in intuitionistic logic.
     * Even though intuitionistic logic rejects LEM, it fully accepts LNC.
     * You can construct an intuitionistic proof of ¬(A ∧ ¬A).
     *
     * This is the key duality: where LJ fails to prove LEM, it succeeds in proving LNC.
     * And where LJ proves LNC, LDJ fails to prove LNC.
     */
    @Theorem(
        name = "LNC_in_LJ",
        signature = "LJ",
        status = "PROVABLE",
        proofReference = "Intuitionistic logic proves ¬(A ∧ ¬A). " +
                         "This is constructively valid: no constructor can produce both A and ¬A.",
        description = "Law of Non-Contradiction IS provable in intuitionistic logic LJ. " +
                      "This is the opposite of LEM, which is NOT provable in LJ."
    )
    public static void lncInLJ() {
    }

    /**
     * LNC in LDJ (Paraconsistent Dual of LJ)
     * Status: NON_PROVABLE
     *
     * In the paraconsistent dual, LNC is NOT provable.
     * This is the dual of LEM's non-provability in LJ.
     *
     * Paraconsistent logic permits contradictions (A ∧ ¬A can both be true without explosion).
     * Therefore, it cannot prove LNC: the law of non-contradiction is rejected.
     *
     * This shows the perfect duality:
     * - LJ rejects LEM but accepts LNC
     * - LDJ accepts LEM but rejects LNC
     */
    @Theorem(
        name = "LNC_in_LDJ",
        signature = "LDJ",
        status = "NON_PROVABLE",
        proofReference = "Urbas & Rauszer (1990). Paraconsistent logic LDJ does NOT prove LNC. " +
                         "Paraconsistency allows (A ∧ ¬A) without explosion, so LNC is not a theorem.",
        description = "Law of Non-Contradiction is NOT provable in paraconsistent logic LDJ. " +
                      "This is the exact dual of LEM's non-provability in LJ."
    )
    public static void lncInLDJ() {
    }

    /**
     * LNC in Common Logic (Intersection)
     * Status: NEITHER PROVABLE NOR REFUTABLE
     *
     * In the common logic, LNC is neither provable nor refutable.
     *
     * CRITICAL LOGICAL CONSTRAINT (symmetric to LEM):
     * - LNC cannot be PROVABLE in common logic because it's NOT provable in LDJ
     *   (which is an extension). If LNC were provable in common, it would be provable in all extensions.
     * - LNC cannot be REFUTABLE in common logic because it IS provable in LJ and LK
     *   (which are extensions). If LNC were refutable in common, then ¬LNC would be provable in
     *   the intersection, thus provable in all extensions, contradicting LJ and LK.
     *
     * Therefore, LNC is UNDETERMINED in common logic:
     * - Neither ¬(A ∧ ¬A) nor (A ∧ ¬A) is derivable
     * - The principle is logically independent
     * - Common logic is the intersection of logics with opposed views on LNC
     */
    @Theorem(
        name = "LNC_in_Common",
        signature = "Common",
        status = "UNPROVABLE_AND_REFUTABLE",
        proofReference = "Common logic is the intersection of LJ (which proves LNC) and LDJ (which doesn't). " +
                         "If LNC were provable here, it would contradict LDJ. " +
                         "If LNC were refutable here, it would contradict LJ. " +
                         "Therefore, LNC is undetermined.",
        description = "Law of Non-Contradiction is neither provable nor refutable in common logic. " +
                      "This is the logical consequence of common logic being the intersection of LJ and LDJ."
    )
    public static void lncInCommon() {
    }

    /**
     * Construct and validate the LNC tetragram.
     */
    public static Tetragram<String> buildLNCTetragram() {
        // Reuse the signatures
        LogicalSignature lkSig = SignatureDefinitions.createLKSignature();
        LogicalSignature ljSig = SignatureDefinitions.createLJSignature();
        LogicalSignature ldjSig = SignatureDefinitions.createLDJSignature();
        LogicalSignature commonSig = SignatureDefinitions.createCommonLogicSignature();

        // Create tetragram nodes
        TetragramNode classicalNode = new TetragramNode(true, true, lkSig,
            "LK (Classical): LNC is provable, LEM is provable");
        TetragramNode intuitionisticNode = new TetragramNode(true, false, ljSig,
            "LJ (Intuitionistic): LNC is provable, LEM is NOT provable");
        TetragramNode paraconsistentCompleteNode = new TetragramNode(false, true, ldjSig,
            "LDJ (Paraconsistent): LNC is NOT provable, LEM is provable");
        TetragramNode commonLogicNode = new TetragramNode(false, false, commonSig,
            "Common Logic: Both LNC and LEM are undetermined");

        // Create tetragram
        Tetragram<String> tetragram = new Tetragram<>(
            "LNC",
            classicalNode,
            intuitionisticNode,
            paraconsistentCompleteNode,
            commonLogicNode
        );

        // Register theorem statuses (DUAL to LEM)
        tetragram.registerTheorem("LNC",
            TheoremStatus.PROVABLE,                    // LK: provable
            TheoremStatus.PROVABLE,                    // LJ: provable (OPPOSITE of LEM)
            TheoremStatus.NON_PROVABLE,                // LDJ: not provable (OPPOSITE of LEM)
            TheoremStatus.UNPROVABLE_AND_REFUTABLE     // Common: undetermined
        );

        return tetragram;
    }

    /**
     * Demonstrate the LNC tetragram and its duality relationship to LEM.
     */
    public static void main(String[] args) {
        System.out.println("=== LNC Tetragram (Dual to LEM) ===\n");

        Tetragram<String> lnc = buildLNCTetragram();
        System.out.println(lnc.summarizeTheorem("LNC"));

        System.out.println("\n=== Duality Relationship ===");
        System.out.println("LEM Tetragram Status:         LNC Tetragram Status:");
        System.out.println("LK:     PROVABLE              LK:     PROVABLE");
        System.out.println("LJ:     NON_PROVABLE      ↔   LJ:     PROVABLE");
        System.out.println("LDJ:    PROVABLE          ↔   LDJ:    NON_PROVABLE");
        System.out.println("Common: UNDETERMINED          Common: UNDETERMINED");
        System.out.println("\nKey: LEM and LNC are COMPLEMENTARY theorems mediated by double negation.");
    }
}
