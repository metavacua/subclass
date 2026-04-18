package org.subclass.logic.rules;

import org.subclass.logic.proof.typed.*;
import java.util.function.Function;

/**
 * Static factory methods for constructing proofs via inference rules.
 *
 * Inference rules compose proofs while enforcing type constraints. By using the type system,
 * we ensure that structural rule violations are caught at compile-time.
 *
 * For example:
 * - orRight() requires two proofs of the same type and returns that type
 * - If you try to apply orRight() to a Proof&lt;Many, One&gt;, it won't type-check
 *   (because orRight needs to be able to join two disjuncts on the right)
 *
 * <h2>Stub status — read before use</h2>
 *
 * <strong>Every method in this class currently throws
 * {@link UnsupportedOperationException} at runtime.</strong> The class exists
 * today to lock in the type-level contract (the generic signatures encode the
 * structural-rule constraints of each calculus) rather than to run proofs.
 *
 * <p>This is deliberate: the phantom-typed {@link org.subclass.logic.proof.typed.Proof}
 * architecture expresses proof validity through the Java type system, so the
 * signatures here are the primary artefact. Replacing the stubs with real
 * sequent manipulation is tracked on the roadmap (see
 * <a href="../../../../../../../../CHANGELOG.md">CHANGELOG.md</a> and
 * <a href="../../../../../../../../README.md">README.md</a>).
 *
 * @apiNote Do not call these methods from production code. Call sites that
 *     need an actual proof witness should construct one directly via the
 *     appropriate {@code *Proof} implementation; the factory methods here
 *     are placeholders whose purpose is to demonstrate the typing discipline.
 * @implNote Each method body intentionally throws
 *     {@code UnsupportedOperationException("... not yet implemented")} so
 *     that accidental runtime use fails loudly rather than silently returning
 *     a malformed proof object.
 */
public final class InferenceRules {
    private InferenceRules() {
        // Utility class: no instantiation
    }

    /**
     * Axiom rule: A ⊢ A (reflexive).
     *
     * The axiom is the base case for all proofs: an atom is always provable from itself.
     *
     * @param formula the formula to prove
     * @return a proof of the axiom sequent
     */
    public static <L extends Cardinality> Proof<L, L> axiom(Formula formula) {
        // Type parameter L is unconstrained: axiom works for any cardinality
        // In practice, we'd construct a Sequent with formula on both sides
        // and return the appropriate Proof type.

        // Stub implementation: just to show the type signature
        throw new UnsupportedOperationException("Axiom rule not yet implemented");
    }

    /**
     * Right disjunction introduction: A ⊢ C, B ⊢ C implies A ∨ B ⊢ C.
     *
     * To prove a disjunction on the right, we prove each disjunct separately.
     * This rule preserves the cardinality on both sides.
     *
     * @param leftProof proof of the left disjunct
     * @param rightProof proof of the right disjunct
     * @param <L> antecedent cardinality
     * @param <R> succedent cardinality
     * @return a proof of the disjunction
     */
    public static <L extends Cardinality, R extends Cardinality>
    Proof<L, R> orRight(Proof<L, R> leftProof, Proof<L, R> rightProof) {
        // Both proofs must have the same cardinality for this to work.
        // The disjunction operation preserves that cardinality.

        // Stub: would construct Or formula and return appropriate Proof type
        throw new UnsupportedOperationException("Right disjunction rule not yet implemented");
    }

    /**
     * Right conjunction introduction: A ⊢ B, A ⊢ C implies A ⊢ B ∧ C.
     *
     * To prove a conjunction on the right (in intuitionistic logic),
     * we must prove both conjuncts. In intuitionistic logic, this returns
     * a Proof&lt;L, One&gt; (single conclusion).
     *
     * @param leftProof proof of the left conjunct
     * @param rightProof proof of the right conjunct
     * @param <L> antecedent cardinality (can be any cardinality)
     * @return a proof of the conjunction in intuitionistic form
     */
    public static <L extends Cardinality>
    Proof<L, One> andRight(Proof<L, One> leftProof, Proof<L, One> rightProof) {
        // Both proofs are in the same logic (same L type).
        // The conjunction operation reduces the right side to One (single conclusion).

        // Stub: would construct And formula and return appropriate Proof type
        throw new UnsupportedOperationException("Right conjunction rule not yet implemented");
    }

    /**
     * Implication right introduction: Γ, A ⊢ B implies Γ ⊢ A → B.
     *
     * To prove an implication, we assume the antecedent and prove the consequent.
     * This is a higher-order rule: it takes a function that accepts a proof
     * of the antecedent and returns a proof of the consequent.
     *
     * The use of Function indicates we're building a proof using the antecedent
     * as an available hypothesis.
     *
     * @param proveConsequent a function that, given the antecedent assumption, proves the consequent
     * @return a proof of the implication in intuitionistic form
     */
    public static Proof<Many, One> impliesRight(
            Function<Proof<One, One>, Proof<Many, One>> proveConsequent) {
        // The function represents the discharge of the antecedent.
        // In classical logic, this would be Proof&lt;Many, Many&gt;,
        // but for now we use intuitionistic (Many, One).

        // Stub: would apply proveConsequent to construct proof
        throw new UnsupportedOperationException("Implication right rule not yet implemented");
    }

    /**
     * Implication left introduction: A ⊢ C, Γ, B ⊢ Δ implies Γ, A → B ⊢ Δ, C.
     *
     * To use an implication hypothesis, we split into two cases:
     * - Prove the antecedent A
     * - Assume the consequent B and prove the goal
     *
     * @param antecedentProof proof of the implication's antecedent
     * @param consequentProof proof of the goal using the consequent as hypothesis
     * @param <L> antecedent cardinality
     * @param <R> succedent cardinality
     * @return a proof incorporating the implication
     */
    public static <L extends Cardinality, R extends Cardinality>
    Proof<L, R> impliesLeft(Proof<L, R> antecedentProof, Proof<L, R> consequentProof) {
        // Both proofs have the same cardinality as the result.

        // Stub: would merge proofs and return appropriate type
        throw new UnsupportedOperationException("Implication left rule not yet implemented");
    }

    /**
     * Negation right introduction: Γ, A ⊢ Δ implies Γ ⊢ ¬A, Δ.
     *
     * To prove a negation, we assume the formula and derive a contradiction
     * (or more generally, prove that the negation holds).
     *
     * @param proofFromNegation proof of the goal assuming the negation
     * @param <L> antecedent cardinality
     * @param <R> succedent cardinality
     * @return a proof of the negation
     */
    public static <L extends Cardinality, R extends Cardinality>
    Proof<L, R> notRight(Proof<L, R> proofFromNegation) {
        // The negation operation preserves cardinality.

        // Stub: would construct negation and return proof
        throw new UnsupportedOperationException("Negation right rule not yet implemented");
    }
}
