package org.subclass.examples.executable;

import org.subclass.annotation.Theorem;
import org.subclass.annotation.TheoremFamily;
import org.subclass.logic.proof.typed.*;
import org.subclass.logic.rules.InferenceRules;

import static org.subclass.logic.rules.InferenceRules.andLeft;
import static org.subclass.logic.rules.InferenceRules.axiom;
import static org.subclass.logic.rules.InferenceRules.notLeft;
import static org.subclass.logic.rules.InferenceRules.notRight;

/**
 * Diamond graph validator: LNC theorem status across logical matrices.
 *
 * <strong>This is NOT a pedagogical example.</strong> This class is a critical
 * data model reference that validates the dual property of the diamond graph.
 *
 * LNC exhibits perfect duality with LEM across the diamond:
 *
 * <table border="1" cellpadding="5">
 *   <tr><th>Logic</th><th>LEM</th><th>LNC</th><th>Type</th></tr>
 *   <tr><td>LK (classical)</td><td>✓</td><td>✓</td><td>Proof&lt;Many, Many&gt;</td></tr>
 *   <tr><td>LJ (intuitionistic)</td><td>✗</td><td>✓</td><td>Proof&lt;Many, One&gt;</td></tr>
 *   <tr><td>LDJ (paraconsistent)</td><td>✓</td><td>✗</td><td>Proof&lt;One, Many&gt;</td></tr>
 *   <tr><td>Common (intersection)</td><td>?</td><td>?</td><td>Proof&lt;One, One&gt;</td></tr>
 * </table>
 *
 * Where LEM is provable, LNC is not, and vice versa (in left/right nodes).
 * This dual structure validates the diamond graph's structural constraint:
 * the four logical matrices form a complete lattice of provability properties.
 *
 * <strong>Incomplete implementation:</strong> Methods have stub bodies.
 * This indicates the architecture awaits {@link org.subclass.processor.AntitheoremProcessor}
 * implementation to handle unprovability/refutability derivations grounded in axiom schemas.
 *
 * @apiNote When AntitheoremProcessor framework is completed, these methods will
 *     validate that the dual structure of LEM/LNC is correctly enforced by the
 *     diamond graph's logical matrices and their respective axiom schemas.
 *
 * @see org.subclass.processor.AntitheoremProcessor
 * @see org.subclass.examples.executable.LEMProofs (complementary validator)
 * @see org.subclass.logic.tetragram.Tetragram
 */
@TheoremFamily(
    name = "LNC",
    displayName = "Law of Non-Contradiction",
    description = "For any proposition A, it is not the case that both A and ¬A are true"
)
public class LNCProofs {

    /**
     * LNC in classical logic (LK): PROVABLE.
     *
     * Classical logic proves LNC as a fundamental axiom: ¬(A ∧ ¬A).
     *
     * Type: Proof&lt;Many, Many&gt; - classical logic
     * Status: PROVABLE
     */
    @Theorem(
        name = "LNC_in_LK",
        signature = "LK",
        status = "PROVABLE",
        proofReference = "Classical sequent calculus - fundamental axiom"
    )
    public static Proof<Many, Many> classical() {
        return classical(new Formula.Atom("A"));
    }

    /**
     * Classical LNC proof for a given atom A:
     * <pre>
     *           A ⊢ A            (Ax)
     *        ------------         (¬L)
     *         A, ¬A ⊢
     *        -------------        (∧L)
     *         A ∧ ¬A ⊢
     *        --------------       (¬R)
     *         ⊢ ¬(A ∧ ¬A)
     * </pre>
     * Axiom at the leaves, ¬L moves ¬A into the antecedent, ∧L combines A
     * and ¬A into A ∧ ¬A, ¬R derives ¬(A ∧ ¬A) on the right.
     */
    public static Proof<Many, Many> classical(Formula atom) {
        Proof<Many, Many> ax = axiom(atom);         // A ⊢ A
        Proof<Many, Many> nL = notLeft(ax);         // A, ¬A ⊢
        Proof<Many, Many> aL = andLeft(nL);         // A ∧ ¬A ⊢
        return notRight(aL);                        // ⊢ ¬(A ∧ ¬A)
    }

    /**
     * LNC in intuitionistic logic (LJ): PROVABLE.
     *
     * Intuitionistic logic accepts LNC (unlike LEM).
     * LNC is constructively valid: we can always prove that a contradiction is absurd.
     *
     * To prove ¬(A ∧ ¬A), assume A ∧ ¬A, extract ¬A, apply it to A to get ⊥ (absurd).
     *
     * Type: Proof&lt;Many, One&gt; - intuitionistic logic
     * Status: PROVABLE
     */
    @Theorem(
        name = "LNC_in_LJ",
        signature = "LJ",
        status = "PROVABLE",
        proofReference = "Intuitionistic logic accepts LNC (constructively provable)"
    )
    public static Proof<Many, One> intuitionistic() {
        return intuitionistic(new Formula.Atom("A"));
    }

    /**
     * Intuitionistic LNC proof: identical shape to the classical derivation,
     * admissible in LJ because {@code ¬R} is valid on a single succedent and
     * the antecedent is unrestricted.
     */
    public static Proof<Many, One> intuitionistic(Formula atom) {
        Proof<Many, One> ax = axiom(atom);          // A ⊢ A
        Proof<Many, One> nL = notLeft(ax);          // A, ¬A ⊢ (succedent empty at One)
        Proof<Many, One> aL = andLeft(nL);          // A ∧ ¬A ⊢
        return notRight(aL);                        // ⊢ ¬(A ∧ ¬A)
    }

    /**
     * LNC in paraconsistent dual logic (LDJ): NON_PROVABLE.
     *
     * Paraconsistent logic explicitly rejects LNC to allow contradictions.
     * This is the defining feature of paraconsistency.
     *
     * Unlike LJ which rejects LEM (a positive truth principle),
     * LDJ rejects LNC (a negative truth principle).
     *
     * Type: Proof&lt;One, Many&gt; - paraconsistent dual logic
     * Status: NON_PROVABLE
     */
    @Theorem(
        name = "LNC_in_LDJ",
        signature = "LDJ",
        status = "NON_PROVABLE",
        proofReference = "Paraconsistent logic explicitly rejects LNC (Urbas-Rauszer)"
    )
    public static Proof<One, Many> paraconsistent() {
        // In LDJ, LNC is unprovable by design.
        // The signature (One, Many) reflects the dual restriction
        // (single premise, unrestricted conclusions).

        // Attempting to prove ¬(A ∧ ¬A) fails: we cannot derive a contradiction
        // from A ∧ ¬A in a logic that permits contradictions.

        return null;
    }

    /**
     * LNC in common logic: UNPROVABLE_AND_REFUTABLE.
     *
     * Like LEM in common logic, LNC is undetermined in the intersection logic.
     * The cardinality restriction (One, One) prevents both proof and refutation.
     *
     * Type: Proof&lt;One, One&gt; - common logic
     * Status: UNPROVABLE_AND_REFUTABLE (neither provable nor refutable)
     */
    @Theorem(
        name = "LNC_in_Common",
        signature = "Common",
        status = "UNPROVABLE_AND_REFUTABLE",
        proofReference = "Common logic cardinality constraints prevent both proof and refutation"
    )
    public static Proof<One, One> common() {
        // In common logic (One, One), ¬(A ∧ ¬A) cannot be constructed
        // with the severe cardinality restrictions. Like LEM, LNC is undetermined.

        return null;
    }

    /**
     * Demonstrates perfect duality with LEM tetragram.
     *
     * Visual representation:
     *
     *     LK (consistent, complete)
     *     /  ✓ (LEM)  |  ✓ (LNC)  \
     *    /            |            \
     *   LJ           Common        LDJ
     *  (consis,     (paracons,    (paracons,
     *   parcomp)     parcomp)      comp)
     *   ✗ (LEM)    ? (both)      ✓ (LEM)
     *   ✓ (LNC)    ? (both)      ✗ (LNC)
     *
     * LEM and LNC have opposite provability at opposite corners (LJ vs LDJ),
     * demonstrating the duality between classical restrictions on left (LJ)
     * versus right (LDJ).
     */
}
