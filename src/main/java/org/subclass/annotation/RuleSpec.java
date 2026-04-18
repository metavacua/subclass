package org.subclass.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Metadata for an inference-rule class in the Curry–Howard–Java correspondence.
 *
 * Every reified rule class under {@code org.subclass.logic.rules} carries a
 * {@code @RuleSpec} describing the sequent-calculus rule it implements. The
 * annotation is consumed at compile time by
 * {@link org.subclass.processor.TheoremProcessor} (to cross-check that the
 * declaring class actually implements {@code Proof<L,R>} with cardinalities
 * compatible with {@link #side()}) and at runtime by the service-loader-based
 * rule registry and the reflective proof walker.
 *
 * Example:
 * <pre>{@code
 * @RuleSpec(name = "AndRight", connective = "∧", side = "right", premiseCount = 2)
 * public record AndRight<L extends Cardinality, R extends Cardinality>(
 *         Proof<L,R> left, Proof<L,R> right, Sequent<L,R> conclusion)
 *     implements ProofNode<L,R> { ... }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuleSpec {
    /**
     * The sequent-calculus rule name (e.g. "Axiom", "Cut", "AndRight").
     */
    String name();

    /**
     * The connective introduced by this rule, or "" for axiom, cut, and
     * structural rules. Examples: "∧", "∨", "→", "¬".
     */
    String connective() default "";

    /**
     * "left", "right", "axiom", "cut", or "structural" — identifies which side
     * of the sequent the connective is introduced on, or that this is not an
     * introduction rule.
     */
    String side();

    /**
     * The number of proof-premises (children of this rule node). Zero for
     * axioms, one for unary rules like {@code ¬R}, two for binary rules like
     * {@code ∧R}.
     */
    int premiseCount();

    /**
     * Optional rule description for documentation / diagnostics.
     */
    String description() default "";
}
