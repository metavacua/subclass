package org.subclass.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining a logical signature in the annotation processor.
 * This allows logical signatures to be registered during annotation processing
 * so they can be referenced in @Theorem annotations.
 *
 * A signature in minimal canonical form should list only essential connectives
 * and structural rules without redundant symbols.
 *
 * Example:
 * @LogicalSignatureDefinition(
 *     name = "LK",
 *     displayName = "Classical Sequent Calculus",
 *     connectives = {"∧", "∨", "¬", "→"},
 *     structuralRules = {"W", "C", "E"},
 *     functionallyComplete = true,
 *     description = "Gentzen's classical sequent calculus with all structural rules"
 * )
 * public class LKSignature { }
 *
 * Another example:
 * @LogicalSignatureDefinition(
 *     name = "LJ",
 *     displayName = "Intuitionistic Logic",
 *     connectives = {"∧", "∨", "¬", "→"},
 *     structuralRules = {"W", "C", "E"},
 *     functionallyComplete = false,
 *     description = "Intuitionistic sequent calculus with restricted right side"
 * )
 * public class LJSignature { }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicalSignatureDefinition {
    /**
     * Internal name/identifier for this signature.
     * Should match the signature references in @Theorem annotations.
     * Example: "LK", "LJ", "Linear", "Affine"
     */
    String name();

    /**
     * Human-readable display name.
     * Example: "Classical Sequent Calculus", "Intuitionistic Logic"
     */
    String displayName() default "";

    /**
     * Array of connective symbols in this signature.
     * Use Unicode symbols: ∧, ∨, ¬, →, ⊗, ⊕, !, ?, ∀, ∃, etc.
     * These should be minimal and necessary for the logic (canonical form).
     */
    String[] connectives() default {};

    /**
     * Array of structural rules.
     * Use abbreviations: "W" for Weakening, "C" for Contraction, "E" for Exchange
     * Example: {"W", "C", "E"} for classical logic, {"E"} for linear logic
     */
    String[] structuralRules() default {};

    /**
     * Whether this signature is functionally complete.
     * A functionally complete signature can express all possible truth functions.
     * Classical logic is functionally complete; intuitionistic logic is not.
     */
    boolean functionallyComplete() default false;

    /**
     * Description of this signature's key properties.
     * Should explain what makes this signature unique and which properties
     * characterize it (e.g., "Lacks LEM", "Lacks LNC", "Substructural").
     */
    String description() default "";

    /**
     * Optional reference to where this signature is formally defined.
     * Can be a paper, textbook chapter, or URL.
     */
    String reference() default "";
}
