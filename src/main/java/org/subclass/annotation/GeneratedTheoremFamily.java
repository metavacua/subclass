package org.subclass.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as a template for theorem family generation. The annotation processor will
 * generate a complete @TheoremFamily with 4 @Theorem-annotated methods (one per tetragram node)
 * and optional anti-theorem family with relationship-aware duality status mappings.
 *
 * <p><b>Generated Artifacts:</b>
 * <ul>
 *   <li>{TheoremName}Family - Implements @TheoremFamily with 4 @Theorem methods</li>
 *   <li>{TheoremName}Proofs - Proof implementations for each node</li>
 *   <li>If antiTheoremDefinition provided:
 *     <ul>
 *       <li>{AntiTheoremName}Family - Separate family for anti-theorem variant</li>
 *       <li>{AntiTheoremName}Proofs - Anti-theorem proof objects</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Anti-Theorem Semantics:</b>
 * Anti-theorems are NOT simple status inversions. They capture unprovability/refutability
 * distinctions and double negation elimination/introduction failures across logics.
 * Example: LEM (A ∨ ¬A) vs LEM' (double negation variant)
 *          LNC (¬(A ∧ ¬A)) vs LNC' (unprovability variant)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GeneratedTheoremFamily {

    /**
     * Name of this theorem family (e.g., "LEM", "LNC").
     */
    String name();

    /**
     * Reference to the @LogicGraph specification.
     * Example: "org.subclass.examples.GeneratedLEMDiamond"
     */
    String graphReference();

    /**
     * Sequent or formula notation describing the theorem.
     * Example: "A ∨ ¬A" or "⟹ A ∨ ¬A" in sequent notation
     */
    String theoremNotation();

    /**
     * Optional sequent notation for anti-theorem variant.
     * If provided, processor generates a separate anti-theorem family.
     * Example: "A ∧ ¬A ⟹" (anti-theorem variant of unprovability)
     */
    String antiTheoremNotation() default "";

    /**
     * Name for the anti-theorem family (e.g., "LEM_PRIME", "LNC_PRIME").
     * Required if antiTheoremNotation is provided.
     */
    String antiTheoremName() default "";

    /**
     * Status mapping for theorem across tetragram nodes.
     * Format: "LK:PROVABLE,LJ:NON_PROVABLE,LDJ:PROVABLE,COMMON:UNPROVABLE_AND_REFUTABLE"
     * Each logicName:status pair maps a node to its theorem status.
     */
    String theoremStatusMapping();

    /**
     * Status mapping for anti-theorem (if antiTheoremNotation is provided).
     * Format same as theoremStatusMapping.
     * Example: "LK:PROVABLE,LJ:PROVABLE,LDJ:NON_PROVABLE,COMMON:UNPROVABLE_AND_REFUTABLE"
     */
    String antiTheoremStatusMapping() default "";

    /**
     * Describes the relationship between theorem and anti-theorem in terms of
     * double negation elimination/introduction failures.
     * Example: "LEM captures DNE failures; LEM' captures DNI failures in paraconsistent logic"
     */
    String doubleNegationVariance() default "";

    /**
     * Human-readable display name for documentation.
     */
    String displayName() default "";

    /**
     * Reference documentation or citations for this theorem.
     */
    String reference() default "";

    /**
     * Description of what this theorem represents in proof theory.
     */
    String description() default "";
}
