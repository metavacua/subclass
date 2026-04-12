package org.subclass.logic.proof;

import java.util.List;
import java.util.Objects;

/**
 * Represents a proof in sequent calculus as a sequence of rule applications.
 *
 * A proof consists of:
 * 1. A starting sequent to be proven (e.g., ⊢A∨¬A)
 * 2. A sequence of rule applications that eliminate object connectives
 * 3. Closure with the reflexive axiom (Ax)
 *
 * Linear proofs are represented as a flat sequence: (⊢A∨¬A, L∨, L¬, Ax)
 * Branching proofs have nested proof sequences where a rule creates multiple premises.
 *
 * Example linear proof:
 *   new Proof("⊢A∨¬A", Arrays.asList("L∨", "L¬", "Ax"))
 *
 * The subformula property is maintained: every formula in the proof is a subformula
 * of the starting sequent. This bounds the proof space and makes verification tractable.
 */
public class Proof {
    private final String startingSequent;
    private final List<String> ruleSequence;
    private final List<Proof> branchingProofs; // null for linear proofs, non-empty for branching

    /**
     * Construct a linear proof.
     *
     * @param startingSequent The sequent to prove (e.g., "⊢A∨¬A")
     * @param ruleSequence List of rule names in application order (e.g., ["L∨", "L¬", "Ax"])
     * @throws IllegalArgumentException if sequent or rules are null/empty, or if rules don't end with "Ax"
     */
    public Proof(String startingSequent, List<String> ruleSequence) {
        this.startingSequent = Objects.requireNonNull(startingSequent, "Starting sequent cannot be null");
        this.ruleSequence = Objects.requireNonNull(ruleSequence, "Rule sequence cannot be null");

        if (startingSequent.trim().isEmpty()) {
            throw new IllegalArgumentException("Starting sequent cannot be empty");
        }
        if (ruleSequence.isEmpty()) {
            throw new IllegalArgumentException("Rule sequence cannot be empty");
        }
        if (!ruleSequence.get(ruleSequence.size() - 1).equals("Ax")) {
            throw new IllegalArgumentException("Proof must close with reflexive axiom (Ax)");
        }

        this.branchingProofs = null; // Linear proof
    }

    /**
     * Construct a branching proof.
     *
     * @param startingSequent The sequent to prove
     * @param ruleSequence List of rule names up to the branching point
     * @param branchingProofs Nested proofs for each branch created by the rule
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Proof(String startingSequent, List<String> ruleSequence, List<Proof> branchingProofs) {
        this.startingSequent = Objects.requireNonNull(startingSequent, "Starting sequent cannot be null");
        this.ruleSequence = Objects.requireNonNull(ruleSequence, "Rule sequence cannot be null");
        this.branchingProofs = Objects.requireNonNull(branchingProofs, "Branching proofs cannot be null");

        if (startingSequent.trim().isEmpty()) {
            throw new IllegalArgumentException("Starting sequent cannot be empty");
        }
        if (ruleSequence.isEmpty()) {
            throw new IllegalArgumentException("Rule sequence cannot be empty");
        }
        if (branchingProofs.isEmpty()) {
            throw new IllegalArgumentException("Branching proofs list cannot be empty");
        }
    }

    public String getStartingSequent() {
        return startingSequent;
    }

    public List<String> getRuleSequence() {
        return List.copyOf(ruleSequence);
    }

    public boolean isBranching() {
        return branchingProofs != null;
    }

    public List<Proof> getBranchingProofs() {
        return branchingProofs != null ? List.copyOf(branchingProofs) : List.of();
    }

    /**
     * Get the last rule applied (should be Ax for valid proofs).
     */
    public String getLastRule() {
        return ruleSequence.get(ruleSequence.size() - 1);
    }

    /**
     * Get the number of rule applications.
     */
    public int getProofLength() {
        return ruleSequence.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(startingSequent);
        for (String rule : ruleSequence) {
            sb.append(", ").append(rule);
        }
        if (isBranching()) {
            sb.append(", [");
            for (int i = 0; i < branchingProofs.size(); i++) {
                if (i > 0) sb.append("; ");
                sb.append(branchingProofs.get(i).toString());
            }
            sb.append("]");
        }
        sb.append(")");
        return sb.toString();
    }
}
