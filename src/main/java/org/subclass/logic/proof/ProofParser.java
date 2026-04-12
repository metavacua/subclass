package org.subclass.logic.proof;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for proof notation in the SubClass formalization.
 *
 * Parses proof strings in the format:
 *   Linear:  (⊢A∨¬A, L∨, L¬, Ax)
 *   Branching: (⊢P∧Q, L∧, [(⊢P, Ax); (⊢Q, Ax)])
 *
 * The parser is designed for compile-time use by the annotation processor,
 * so it provides detailed error messages for malformed proofs.
 */
public class ProofParser {
    private static final Pattern PROOF_PATTERN = Pattern.compile(
        "\\(([^,]+(?:,[^\\[\\]]*?)?)(?:\\[(.+)\\])?\\)"
    );

    /**
     * Parse a proof string into a Proof object.
     *
     * @param proofString String representation of a proof
     * @return Parsed Proof object
     * @throws ProofParseException if the proof notation is malformed
     */
    public static Proof parse(String proofString) throws ProofParseException {
        if (proofString == null || proofString.trim().isEmpty()) {
            throw new ProofParseException("Proof string cannot be null or empty");
        }

        proofString = proofString.trim();

        // Check basic structure
        if (!proofString.startsWith("(") || !proofString.endsWith(")")) {
            throw new ProofParseException(
                "Proof must be enclosed in parentheses. Expected format: (⊢sequent, rule1, rule2, ..., Ax)"
            );
        }

        // Remove outer parentheses
        String content = proofString.substring(1, proofString.length() - 1);

        // Check for branching (indicated by nested brackets)
        if (content.contains("[")) {
            return parseBranchingProof(content);
        } else {
            return parseLinearProof(content);
        }
    }

    /**
     * Parse a linear (non-branching) proof.
     *
     * @param content Content without outer parentheses
     * @return Parsed linear Proof
     * @throws ProofParseException if parsing fails
     */
    private static Proof parseLinearProof(String content) throws ProofParseException {
        // Split by commas, but be careful about spaces and special characters
        String[] parts = content.split("\\s*,\\s*");

        if (parts.length < 2) {
            throw new ProofParseException(
                "Linear proof must have at least sequent and Ax. Found: " + content
            );
        }

        String startingSequent = parts[0].trim();
        List<String> ruleSequence = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            ruleSequence.add(parts[i].trim());
        }

        try {
            return new Proof(startingSequent, ruleSequence);
        } catch (IllegalArgumentException e) {
            throw new ProofParseException("Invalid linear proof: " + e.getMessage());
        }
    }

    /**
     * Parse a branching proof with nested proof sequences.
     *
     * @param content Content without outer parentheses but possibly containing brackets
     * @return Parsed branching Proof
     * @throws ProofParseException if parsing fails
     */
    private static Proof parseBranchingProof(String content) throws ProofParseException {
        // Find the opening bracket
        int bracketIndex = content.indexOf('[');
        if (bracketIndex <= 0) {
            throw new ProofParseException("Branching proof must have opening bracket [");
        }

        // Extract the linear part before brackets
        String linearPart = content.substring(0, bracketIndex).trim();
        if (linearPart.endsWith(",")) {
            linearPart = linearPart.substring(0, linearPart.length() - 1).trim();
        }

        // Parse the linear part
        String[] linearParts = linearPart.split("\\s*,\\s*");
        if (linearParts.length < 1) {
            throw new ProofParseException("Branching proof must have starting sequent");
        }

        String startingSequent = linearParts[0].trim();
        List<String> ruleSequence = new ArrayList<>();

        for (int i = 1; i < linearParts.length; i++) {
            ruleSequence.add(linearParts[i].trim());
        }

        // Extract the branching part
        int closingBracket = content.lastIndexOf(']');
        if (closingBracket <= bracketIndex) {
            throw new ProofParseException("Branching proof must have closing bracket ]");
        }

        String branchingContent = content.substring(bracketIndex + 1, closingBracket).trim();
        List<Proof> branchingProofs = parseBranches(branchingContent);

        try {
            return new Proof(startingSequent, ruleSequence, branchingProofs);
        } catch (IllegalArgumentException e) {
            throw new ProofParseException("Invalid branching proof: " + e.getMessage());
        }
    }

    /**
     * Parse the individual branches within branching proof notation.
     * Branches are separated by semicolons and enclosed in parentheses.
     *
     * @param branchingContent The content between [ and ]
     * @return List of parsed Proof objects for each branch
     * @throws ProofParseException if parsing fails
     */
    private static List<Proof> parseBranches(String branchingContent) throws ProofParseException {
        List<Proof> branches = new ArrayList<>();

        // Split by semicolons to separate branches, but respect nested parentheses
        int depth = 0;
        int lastStart = 0;

        for (int i = 0; i < branchingContent.length(); i++) {
            char c = branchingContent.charAt(i);

            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == ';' && depth == 0) {
                String branch = branchingContent.substring(lastStart, i).trim();
                if (!branch.isEmpty()) {
                    branches.add(parse(branch));
                }
                lastStart = i + 1;
            }
        }

        // Add the last branch
        String lastBranch = branchingContent.substring(lastStart).trim();
        if (!lastBranch.isEmpty()) {
            branches.add(parse(lastBranch));
        }

        if (branches.isEmpty()) {
            throw new ProofParseException("Branching proof must have at least one branch");
        }

        return branches;
    }

    /**
     * Exception thrown when proof parsing fails.
     */
    public static class ProofParseException extends Exception {
        public ProofParseException(String message) {
            super(message);
        }

        public ProofParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
