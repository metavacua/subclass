package org.subclass.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an unprovability or refutability theorem.
 *
 * Used with {@link AntitheoremProcessor} to validate metalinguistic derivations
 * of the form Γ ⊢ {} (empty consequent = unprovability in the logic).
 *
 * The AntitheoremProcessor will verify that such derivations ground in the
 * reflexive axiom schema of the specific logic, and that the framework correctly
 * distinguishes between unprovability and refutability across the diamond graph nodes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Antitheorem {
    /**
     * The name of this unprovability theorem (e.g., "LEM is not provable in intuitionistic logic").
     */
    String value();

    /**
     * The logical context in which this formula is unprovable/non-refutable.
     * Expected values: "classical", "intuitionistic", "dual-intuitionistic", "initial"
     */
    String logic() default "";

    /**
     * Whether this represents refutability (classical side) or non-provability (intuitionistic side).
     * Used to distinguish axiom schemas: classical axioms yield refutability, intuitionistic axioms yield unprovability.
     */
    boolean isRefutable() default false;
}
