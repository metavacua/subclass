package org.subclass.logic.proof.typed;

/**
 * Sealed interface representing a proof in sequent calculus with type-safe cardinality constraints.
 *
 * A Proof&lt;L, R&gt; is a witness that the sequent conclusion() is derivable in a specific logic.
 * The type parameters L and R encode the cardinality constraints (how many formulas are allowed
 * on each side), and the implementation class determines which structural rules are available.
 *
 * The key insight: proof validity is enforced by the type system. You cannot write a proof
 * that violates cardinality constraints because the necessary factory methods won't type-check.
 *
 * For example:
 * - CommonProof cannot call weakenLeft() because it doesn't implement WeakeningLeft
 * - IntuitionisticProof can call weakenLeft() but not weaken on the right
 * - ClassicalProof can call any structural rule
 *
 * @param <L> antecedent cardinality
 * @param <R> succedent cardinality
 */
public sealed interface Proof<L extends Cardinality, R extends Cardinality>
    permits CommonProof, IntuitionisticProof, ClassicalProof, DualProof {

    /**
     * Get the conclusion of this proof (the sequent that is proven).
     *
     * @return the proven sequent
     */
    Sequent<L, R> conclusion();
}
