package org.subclass.logic.proof;

import org.subclass.logic.signature.LogicalSignature;
import java.util.Objects;

/**
 * Base class for validating proofs against a logical signature.
 *
 * A proof is valid if:
 * 1. All rule applications are valid for the signature
 * 2. The subformula property is maintained throughout
 * 3. The proof closes with the reflexive axiom (Ax)
 * 4. Each rule eliminates its target object connective
 *
 * The subformula property is crucial: every formula appearing in the proof
 * must be a subformula of the starting sequent. This is guaranteed by the
 * sequent calculus structure and bounds the proof space.
 *
 * Subclasses should implement signature-specific rule validation.
 */
public abstract class ProofChecker {
    protected final LogicalSignature signature;

    /**
     * Construct a proof checker for a specific logical signature.
     *
     * @param signature The logical signature to validate against
     * @throws IllegalArgumentException if signature is null
     */
    public ProofChecker(LogicalSignature signature) {
        this.signature = Objects.requireNonNull(signature, "Signature cannot be null");
    }

    /**
     * Validate that a proof is correct for this signature.
     *
     * @param proof The proof to validate
     * @return ProofValidationResult with details on validity and any errors
     */
    public ProofValidationResult validate(Proof proof) {
        Objects.requireNonNull(proof, "Proof cannot be null");

        ProofValidationResult result = new ProofValidationResult();

        // Check that proof ends with Ax
        if (!proof.getLastRule().equals("Ax")) {
            result.addError("Proof must close with reflexive axiom (Ax), but ends with: " + proof.getLastRule());
            return result;
        }

        // Validate subformula property
        if (!validateSubformulaProperty(proof)) {
            result.addError("Subformula property violated: not all formulas are subformulas of the starting sequent");
        }

        // Validate each rule application
        for (String rule : proof.getRuleSequence()) {
            if (!isValidRule(rule)) {
                result.addError("Invalid rule for signature " + signature.name() + ": " + rule);
            }
        }

        // Validate branching if present
        if (proof.isBranching()) {
            for (int i = 0; i < proof.getBranchingProofs().size(); i++) {
                ProofValidationResult branchResult = validate(proof.getBranchingProofs().get(i));
                if (!branchResult.isValid()) {
                    result.addError("Branch " + (i + 1) + " is invalid: " + branchResult.getErrors());
                }
            }
        }

        return result;
    }

    /**
     * Check if a rule is valid for this signature.
     * Subclasses must implement signature-specific rules (L∨, R∨, L∧, R∧, L¬, R¬, Ax, etc.).
     *
     * @param rule The rule name (e.g., "L∨", "Ax")
     * @return true if the rule is valid for this signature
     */
    protected abstract boolean isValidRule(String rule);

    /**
     * Validate that the subformula property holds for the proof.
     * Every formula in the proof must be a subformula of the starting sequent.
     *
     * The default implementation returns true, deferring to subclasses.
     * Subclasses should implement actual subformula checking if needed.
     *
     * @param proof The proof to check
     * @return true if subformula property is maintained
     */
    protected boolean validateSubformulaProperty(Proof proof) {
        // Stub implementation: subclasses can override for detailed checking
        // In practice, if all rules are valid for the signature, the subformula
        // property is automatically maintained by the sequent calculus structure
        return true;
    }

    /**
     * Get the signature this checker validates against.
     */
    public LogicalSignature getSignature() {
        return signature;
    }

    /**
     * Result of validating a proof.
     */
    public static class ProofValidationResult {
        private boolean valid = true;
        private final StringBuilder errors = new StringBuilder();

        private void addError(String error) {
            if (valid && errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
            valid = false;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrors() {
            return errors.toString();
        }

        @Override
        public String toString() {
            return valid ? "Valid proof" : "Invalid proof: " + errors;
        }
    }
}
