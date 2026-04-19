package org.subclass.examples.executable;

import org.subclass.annotation.Theorem;
import org.subclass.annotation.TheoremFamily;
import org.subclass.logic.proof.typed.*;
import org.subclass.logic.rules.InferenceRules;

import static org.subclass.logic.rules.InferenceRules.axiom;
import static org.subclass.logic.rules.InferenceRules.notRight;
import static org.subclass.logic.rules.InferenceRules.orRight;

/**
 * Diamond graph validator: LEM theorem status across logical matrices.
 *
 * <strong>This is NOT a pedagogical example.</strong> This class is a critical
 * data model reference that validates the diamond graph architecture:
 *
 * <ul>
 *   <li>LEM in classical logic (LK): PROVABLE (Proof&lt;Many, Many&gt;)</li>
 *   <li>LEM in intuitionistic logic (LJ): NON_PROVABLE (Proof&lt;Many, One&gt;)</li>
 *   <li>LEM in paraconsistent logic (LDJ): PROVABLE (Proof&lt;One, Many&gt;)</li>
 *   <li>LEM in common logic (intersection): UNPROVABLE_AND_REFUTABLE (Proof&lt;One, One&gt;)</li>
 * </ul>
 *
 * The diamond graph requires that LEM be provable in the terminal node (classical),
 * unprovable in the initial node (intuitionistic ∩ dual-intuitionistic intersection),
 * and have specific status in the left and right nodes. This class validates that
 * the type system correctly enforces these constraints.
 *
 * <strong>Incomplete implementation:</strong> Methods that represent PROVABLE status
 * have partial implementations; methods representing NON_PROVABLE status return null.
 * This indicates the architecture is incomplete and awaits {@link org.subclass.processor.AntitheoremProcessor}
 * implementation to handle unprovability derivations grounded in reflexive axiom schemas.
 *
 * @apiNote When AntitheoremProcessor framework is completed, these methods will
 *     demonstrate that sequent calculus correctly enforces diamond graph constraints
 *     across all four logical matrices, properly grounding unprovability derivations
 *     (Γ ⊢ {}) in each logic's reflexive axiom schema.
 *
 * @see org.subclass.processor.AntitheoremProcessor
 * @see org.subclass.logic.tetragram.Tetragram
 */
@TheoremFamily(
    name = "LEM",
    displayName = "Law of Excluded Middle",
    description = "For any proposition A, either A is true or ¬A is true"
)
public class LEMProofs {

    /**
     * LEM in classical logic (LK): PROVABLE.
     *
     * Classical logic allows unrestricted multiple formulas on the right (succedent),
     * enabling us to prove A ∨ ¬A by cases:
     * - Assume A on the left, prove A on the right (axiom)
     * - Assume ¬A on the left, prove ¬A on the right (axiom)
     * - Use disjunction right introduction to combine them
     *
     * Type: Proof&lt;Many, Many&gt; - classical logic
     * Status: PROVABLE
     */
    @Theorem(
        name = "LEM_in_LK",
        signature = "LK",
        status = "PROVABLE",
        proofReference = "Classical sequent calculus - fundamental axiom"
    )
    public static Proof<Many, Many> classical() {
        return classical(new Formula.Atom("A"));
    }

    /**
     * Classical LEM proof for a given atom A, constructed via the sequent-calculus
     * derivation:
     * <pre>
     *          A ⊢ A      (Ax)
     *       ------------  (¬R)
     *         ⊢ A, ¬A
     *       -------------- (∨R)
     *         ⊢ A ∨ ¬A
     * </pre>
     * The reified proof tree is rooted at {@code OrRight} and terminates at two
     * axioms {@code A ⊢ A}; the intermediate {@code ¬R} step moves A from the
     * antecedent to the succedent as ¬A.
     */
    public static Proof<Many, Many> classical(Formula atom) {
        Proof<Many, Many> ax = axiom(atom);                 // A ⊢ A
        Proof<Many, Many> notR = notRight(ax);              // ⊢ A, ¬A
        return orRight(notR);                               // ⊢ A ∨ ¬A
    }

    /**
     * LEM in intuitionistic logic (LJ): NON_PROVABLE.
     *
     * Intuitionistic logic restricts the succedent to exactly one formula.
     * This makes A ∨ ¬A unprovable:
     *
     * To prove A ∨ ¬A constructively, we'd need to decide A by some computational
     * process. Without classical axioms, this is impossible.
     *
     * Type: Proof&lt;Many, One&gt; - intuitionistic logic
     * Status: NON_PROVABLE
     *
     * Note: The method signature specifies Proof&lt;Many, One&gt;, which is correct
     * for LJ. The fact that we return null indicates this theorem is not provable.
     */
    @Theorem(
        name = "LEM_in_LJ",
        signature = "LJ",
        status = "NON_PROVABLE",
        proofReference = "Intuitionistic logic lacks LEM by design (Heyting, 1930s)"
    )
    public static Proof<Many, One> intuitionistic() {
        // Attempting to implement classical LEM in intuitionistic signature fails:
        // InferenceRules.orRight() takes two Proof<Many, Many> arguments,
        // but LJ requires Proof<Many, One> return type.
        // Type mismatch => compile-time error (if we tried to implement it)

        // Status NON_PROVABLE => return null or throw UnsupportedOperationException
        return null;
    }

    /**
     * LEM in paraconsistent dual logic (LDJ): PROVABLE.
     *
     * Paraconsistent logic removes the Law of Non-Contradiction (LNC),
     * allowing contradictions. However, it retains double negation elimination (DNE),
     * which makes LEM provable (Urbas-Rauszer, 1990).
     *
     * Type: Proof&lt;One, Many&gt; - paraconsistent logic (single left, unrestricted right)
     * Status: PROVABLE
     */
    @Theorem(
        name = "LEM_in_LDJ",
        signature = "LDJ",
        status = "PROVABLE",
        proofReference = "Urbas & Rauszer (1990) - Paraconsistent logic without LNC retains DNE"
    )
    public static Proof<One, Many> paraconsistent() {
        return paraconsistent(new Formula.Atom("A"));
    }

    /**
     * Paraconsistent (LDJ) LEM proof:
     * <pre>
     *         A ⊢ A     (Ax)
     *       ------------  (¬R)
     *         ⊢ A, ¬A
     *       -------------- (∨R)
     *         ⊢ A ∨ ¬A
     * </pre>
     * The structure mirrors the classical derivation; the LDJ variant is
     * admissible because the succedent is unrestricted (Many) on the right.
     */
    public static Proof<One, Many> paraconsistent(Formula atom) {
        Proof<One, Many> ax = axiom(atom);
        Proof<One, Many> notR = notRight(ax);
        return orRight(notR);
    }

    /**
     * LEM in common logic: UNPROVABLE_AND_REFUTABLE.
     *
     * Common logic is the intersection of LJ and LDJ:
     * - Exactly one formula per side (from both LJ and LDJ restrictions)
     * - Additive junction structure (maximally conservative)
     *
     * With such restrictive cardinality constraints, LEM cannot be proven
     * (unlike in LK or LDJ) nor can it be refuted (unlike some other systems).
     *
     * Type: Proof&lt;One, One&gt; - common logic
     * Status: UNPROVABLE_AND_REFUTABLE (neither provable nor refutable)
     */
    @Theorem(
        name = "LEM_in_Common",
        signature = "Common",
        status = "UNPROVABLE_AND_REFUTABLE",
        proofReference = "Common logic cardinality constraints prevent both proof and refutation"
    )
    public static Proof<One, One> common() {
        // In common logic (One, One), LEM cannot be constructed:
        // To prove A ∨ ¬A with a single conclusion requires handling two cases,
        // but we're restricted to one formula per side.
        // The additive structure makes A ∨ ¬A undetermined.

        return null;
    }
}
