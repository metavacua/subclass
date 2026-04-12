package org.subclass.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking a theorem assertion in a specific logical system.
 * This annotation documents a theorem's existence, status, and proof reference
 * in a particular logic with a particular signature.
 *
 * Example:
 * @Theorem(
 *     name = "LEM_in_LK",
 *     signature = "LK",
 *     status = "PROVABLE",
 *     proofReference = "Classical logic textbook, Chapter 3"
 * )
 * public static void lawOfExcludedMiddleClassical() {}
 *
 * Note: The actual theorem is not executable Java code; the method
 * is merely a container for metadata.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Theorem {
    /**
     * Name of this theorem instance.
     * Should be unique within a TheoremFamily.
     * Example: "LEM_in_LK", "LEM_in_LJ"
     */
    String name();

    /**
     * Name of the logical signature this theorem belongs to.
     * Example: "LK", "LJ", "Linear", "Affine"
     */
    String signature();

    /**
     * The provability status of this theorem in the specified signature.
     * Valid values: "PROVABLE", "NON_PROVABLE", "REFUTABLE", "UNPROVABLE_AND_REFUTABLE"
     *
     * - PROVABLE: The theorem can be derived from axioms
     * - NON_PROVABLE: The theorem cannot be derived
     * - REFUTABLE: The negation can be derived
     * - UNPROVABLE_AND_REFUTABLE: Neither derivable (in paracomplete logics)
     */
    String status();

    /**
     * Reference to where the proof or proof sketch can be found.
     * This can be:
     * - A citation to a textbook (e.g., "Gentzen 1935, Theorem 3.2.1")
     * - A reference to a formal proof in another system
     * - A link to external documentation
     * - A brief description of why it's not provable
     *
     * Example: "Hindley & Seldin, Theorem 2.4.5"
     */
    String proofReference();

    /**
     * Optional human-readable description of this theorem instance.
     * Provides context about why this theorem matters in this logic.
     */
    String description() default "";

    /**
     * Optional DOI, URL, or unique identifier for the proof reference.
     * Useful for linking to external resources.
     */
    String proofReferenceId() default "";
}
