package org.subclass.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Expected theorem status derivation for a specific theorem across all four logics.
 * Used within @DiamondGraph to validate auto-derived statuses.
 *
 * The processor will auto-derive statuses algorithmically and warn if they mismatch
 * these expectations. Serves as both documentation and validation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TheoremStatusDerivation {

    /**
     * Name of the theorem (e.g., "LEM" for Law of Excluded Middle).
     */
    String theoremName();

    /**
     * Expected status in classical logic (Many,Many).
     * Valid values: "PROVABLE", "NON_PROVABLE", "REFUTABLE".
     */
    String classicalStatus();

    /**
     * Expected status in intuitionistic logic (Many,One).
     */
    String intuitionisticStatus();

    /**
     * Expected status in paraconsistent logic (One,Many).
     */
    String paraconsistentStatus();

    /**
     * Expected status in common logic (One,One).
     */
    String commonLogicStatus();

    /**
     * Optional brief explanation or proof sketch for the classical status.
     */
    String classicalReasoning() default "";

    /**
     * Optional brief explanation or proof sketch for the intuitionistic status.
     */
    String intuitionisticReasoning() default "";

    /**
     * Optional brief explanation or proof sketch for the paraconsistent status.
     */
    String paraconsistentReasoning() default "";

    /**
     * Optional brief explanation or proof sketch for the common logic status.
     */
    String commonLogicReasoning() default "";
}
