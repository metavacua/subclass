package org.subclass.examples.executable;

import org.subclass.annotation.Theorem;
import org.subclass.annotation.TheoremFamily;
import org.subclass.logic.proof.typed.*;
import org.subclass.logic.rules.InferenceRules;

/**
 * Type-safe executable proofs for the Law of Excluded Middle (LEM).
 *
 * This class demonstrates how the type system enforces proof validity:
 * - classical() returns Proof&lt;Many, Many&gt; (LK)
 * - intuitionistic() cannot return Proof&lt;Many, Many&gt; (would not type-check)
 *
 * The key insight: method return types ARE proof witnesses. Type mismatches
 * are compile-time errors, not runtime failures.
 *
 * @apiNote Method bodies are <strong>stubs</strong>: the PROVABLE witnesses
 *     ({@link #classical()}, {@link #paraconsistent()}) throw
 *     {@link UnsupportedOperationException} at runtime, and the NON_PROVABLE /
 *     UNPROVABLE_AND_REFUTABLE witnesses ({@link #intuitionistic()},
 *     {@link #common()}) return {@code null} by design. The value of this
 *     class today is that its return-type annotations are validated by
 *     {@link org.subclass.processor.TheoremProcessor} at compile time, not
 *     that the methods execute. Real proof construction is tracked on the
 *     roadmap in the top-level {@code README.md}.
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
        // Stub implementation: return type is the proof witness
        // In reality, this would construct:
        //   A ⊢ A ∨ ¬A  (via orRight with axiom)
        //   ¬A ⊢ A ∨ ¬A (via orRight with axiom)
        // Then combine using cases (LK allows both)

        // Type signature is what matters for annotation processor validation
        throw new UnsupportedOperationException("LEM classical proof not yet implemented");
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
        // LDJ allows multiple conclusions and has double negation elimination,
        // making LEM provable via DNE on ¬¬(A ∨ ¬A).

        throw new UnsupportedOperationException("LEM paraconsistent proof not yet implemented");
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
