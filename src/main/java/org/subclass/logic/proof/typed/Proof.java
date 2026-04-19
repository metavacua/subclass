package org.subclass.logic.proof.typed;

/**
 * Sealed interface representing a proof in sequent calculus with type-safe cardinality constraints.
 *
 * <p>A Proof&lt;L, R&gt; is a witness that the sequent {@link #conclusion()} is derivable in a specific logic.
 * The type parameters L and R encode the cardinality constraints (how many formulas are allowed
 * on each side), and the implementation class determines which structural rules are available.
 *
 * <p>The key insight: proof validity is enforced by the type system. You cannot write a proof
 * that violates cardinality constraints because the necessary factory methods won't type-check.
 *
 * <p>For example:
 * <ul>
 *   <li>CommonProof cannot call weakenLeft() because it doesn't implement WeakeningLeft
 *   <li>IntuitionisticProof can call weakenLeft() but not weaken on the right
 *   <li>ClassicalProof can call any structural rule
 * </ul>
 *
 * <h3>Specification Reference</h3>
 * This type implements the proof representation specified in docs/part1/ProofTheory.xml.
 * See {@link org.subclass.package-info} for the architecture overview and
 * docs/MAPPING.md for the complete spec ↔ implementation correspondence.
 *
 * @param <L> antecedent cardinality ({@link Zero}, {@link One}, or {@link Many})
 * @param <R> succedent cardinality ({@link Zero}, {@link One}, or {@link Many})
 *
 * @see org.subclass.logic.proof.typed.Sequent
 * @see org.subclass.logic.proof.typed.ProofNode
 * @see org.subclass.logic.rules
 */
public sealed interface Proof<L extends Cardinality, R extends Cardinality>
    permits CommonProof, IntuitionisticProof, ClassicalProof, DualProof, ProofNode {

    /**
     * Get the conclusion of this proof (the sequent that is proven).
     *
     * @return the proven sequent
     */
    Sequent<L, R> conclusion();
}
