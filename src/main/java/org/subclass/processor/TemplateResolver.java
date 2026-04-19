package org.subclass.processor;

import java.util.*;

/**
 * Resolves proof and signature templates for code generation.
 * Maps from logic properties and graph structure to appropriate Proof<L,R> type implementations.
 */
public class TemplateResolver {

    /**
     * Proof type templates based on logic node properties.
     */
    public enum ProofTemplate {
        // Classical logic - both sides unrestricted
        CLASSICAL_PROOF("Proof<Many, Many>"),

        // Intuitionistic logic - right side restricted to 1
        INTUITIONISTIC_PROOF("Proof<Many, One>"),

        // Paraconsistent dual - left side restricted to 1
        PARACONSISTENT_PROOF("Proof<One, Many>"),

        // Common/intersection logic - both sides restricted to 1
        COMMON_PROOF("Proof<One, One>");

        private final String typeSignature;

        ProofTemplate(String typeSignature) {
            this.typeSignature = typeSignature;
        }

        public String typeSignature() {
            return typeSignature;
        }
    }

    /**
     * Maps node properties (consistent, complete) to proof templates.
     */
    public static ProofTemplate resolveProofTemplate(boolean consistent, boolean complete) {
        if (consistent && complete) {
            return ProofTemplate.CLASSICAL_PROOF;
        } else if (consistent && !complete) {
            return ProofTemplate.INTUITIONISTIC_PROOF;
        } else if (!consistent && complete) {
            return ProofTemplate.PARACONSISTENT_PROOF;
        } else {
            return ProofTemplate.COMMON_PROOF;
        }
    }

    /**
     * Resolves the cardinality class names for a given proof template.
     */
    public static String[] resolveCardinalities(ProofTemplate template) {
        return switch (template) {
            case CLASSICAL_PROOF -> new String[]{"Many", "Many"};
            case INTUITIONISTIC_PROOF -> new String[]{"Many", "One"};
            case PARACONSISTENT_PROOF -> new String[]{"One", "Many"};
            case COMMON_PROOF -> new String[]{"One", "One"};
        };
    }

    /**
     * Maps theorem status to proof implementation strategy.
     */
    public enum ProofStrategy {
        /**
         * Theorem is PROVABLE - implement with valid Proof<L,R> instance.
         */
        PROVABLE_WITNESS,

        /**
         * Theorem is NON_PROVABLE - return marker/witness indicating unprovability.
         */
        UNPROVABLE_MARKER,

        /**
         * Theorem is REFUTABLE - return marker/witness indicating refutability.
         */
        REFUTABLE_MARKER,

        /**
         * Theorem is UNPROVABLE_AND_REFUTABLE - return dual marker.
         */
        UNPROVABLE_AND_REFUTABLE_MARKER
    }

    public static ProofStrategy resolveProofStrategy(String status) {
        return switch (status.toUpperCase()) {
            case "PROVABLE" -> ProofStrategy.PROVABLE_WITNESS;
            case "NON_PROVABLE" -> ProofStrategy.UNPROVABLE_MARKER;
            case "REFUTABLE" -> ProofStrategy.REFUTABLE_MARKER;
            case "UNPROVABLE_AND_REFUTABLE" -> ProofStrategy.UNPROVABLE_AND_REFUTABLE_MARKER;
            default -> throw new IllegalArgumentException("Unknown theorem status: " + status);
        };
    }

    /**
     * Generates proof implementation code based on status.
     * For now, returns skeleton implementations suitable for compile-time witnesses.
     */
    public static String generateProofImplementation(String logicName, ProofTemplate template,
                                                     String status) {
        StringBuilder sb = new StringBuilder();
        String[] cardinalities = resolveCardinalities(template);
        String left = cardinalities[0];
        String right = cardinalities[1];

        // Placeholder implementation - can be extended with actual proof objects
        sb.append("    // Proof implementation for ").append(logicName).append(" ")
          .append("(").append(status).append(")\n");
        sb.append("    return new Proof<").append(left).append(", ").append(right).append(">() {\n");
        sb.append("        // Compiled witness that proof is ").append(status).append("\n");
        sb.append("    };\n");

        return sb.toString();
    }

    /**
     * Signature template for different logic nodes.
     */
    public record SignatureTemplate(
            String logicName,
            boolean consistent,
            boolean complete,
            String[] connectives,
            String[] structuralRules,
            boolean functionallyComplete) {}

    /**
     * Creates a signature template for a node.
     */
    public static SignatureTemplate createSignatureTemplate(String logicName,
                                                            boolean consistent,
                                                            boolean complete,
                                                            String[] connectives,
                                                            String[] structuralRules,
                                                            boolean functionallyComplete) {
        return new SignatureTemplate(logicName, consistent, complete, connectives,
                                     structuralRules, functionallyComplete);
    }

    /**
     * Generates Java code for signature creation.
     */
    public static String generateSignatureCode(SignatureTemplate template) {
        StringBuilder sb = new StringBuilder();
        sb.append("    public static LogicalSignature create() {\n");
        sb.append("        LogicalSignature.Builder builder = LogicalSignature.builder()\n");
        sb.append("            .name(\"").append(template.logicName()).append("\")\n");
        sb.append("            .functionallyComplete(").append(template.functionallyComplete()).append(")\n");

        // Connectives
        if (template.connectives().length > 0) {
            sb.append("            .connectives(new String[]{");
            for (int i = 0; i < template.connectives().length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(template.connectives()[i]).append("\"");
            }
            sb.append("})\n");
        }

        // Structural rules
        if (template.structuralRules().length > 0) {
            sb.append("            .structuralRules(new String[]{");
            for (int i = 0; i < template.structuralRules().length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(template.structuralRules()[i]).append("\"");
            }
            sb.append("})\n");
        }

        sb.append("            .build();\n");
        sb.append("        return builder;\n");
        sb.append("    }\n");

        return sb.toString();
    }
}
