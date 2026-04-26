package org.subclass.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration for a single node in the diamond graph tetragram.
 * Specifies cardinality constraints, axiom schema, and structural rules.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DiamondGraphNode {

    /**
     * Position identifier in the diamond graph.
     * Valid values: "classical", "intuitionistic", "paraconsistent", "common".
     */
    String position();

    /**
     * Antecedent cardinality constraint.
     * Valid values: "One", "Many".
     */
    String antecedentCardinality();

    /**
     * Succedent cardinality constraint.
     * Valid values: "One", "Many".
     */
    String succedentCardinality();

    /**
     * Axiom schema that defines the minimal proof in this logic.
     * Valid values:
     * - "A⊢A" (common logic: identity only)
     * - "Γ,A⊢A" (intuitionistic: left context allowed)
     * - "A⊢A,Δ" (paraconsistent: right context allowed)
     * - "Γ,A⊢A,Δ" (classical: full context allowed)
     */
    String axiomSchema();

    /**
     * Structural rules available in this logic.
     * Valid values: "W" (weakening), "C" (contraction), "E" (exchange).
     * Cut is always implicit.
     */
    String[] structuralRules() default {};

    /**
     * Logical connectives supported (for documentation).
     */
    String[] connectives() default {"∧", "∨", "¬", "→"};

    /**
     * Whether this logic is functionally complete with the given connectives.
     */
    boolean functionallyComplete() default false;
}
