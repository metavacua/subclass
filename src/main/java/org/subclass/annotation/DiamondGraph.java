package org.subclass.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarative specification of a complete diamond graph (tetragram) with
 * consistency/completeness axes. Encodes all four node configurations:
 * classical, intuitionistic, paraconsistent, and common logic.
 *
 * The annotation processor DiamondGraphProcessor parses this to:
 * - Validate cardinality hierarchy (Common ⊂ {Int,Para} ⊂ Classical)
 * - Auto-derive theorem statuses (PROVABLE/NON_PROVABLE/REFUTABLE)
 * - Validate against @TheoremStatusDerivation expectations
 *
 * Example:
 * <pre>{@code
 * @DiamondGraph(
 *   name = "LEM",
 *   displayName = "Law of Excluded Middle",
 *   classicalNode = @DiamondGraphNode(
 *     position = "classical",
 *     antecedentCardinality = "Many",
 *     succedentCardinality = "Many",
 *     axiomSchema = "Γ,A⊢A,Δ",
 *     structuralRules = {"W", "C", "E"}
 *   ),
 *   intuitionisticNode = @DiamondGraphNode(...),
 *   paraconsistentNode = @DiamondGraphNode(...),
 *   commonLogicNode = @DiamondGraphNode(...)
 * )
 * public class LEMTetragram { }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DiamondGraph {

    /**
     * Name of the theorem/diamond graph (e.g., "LEM", "LNC", "DoubleNegation").
     * Used for identification and error reporting.
     */
    String name();

    /**
     * Human-readable display name for documentation.
     */
    String displayName() default "";

    /**
     * Optional description of the tetragram and its significance.
     */
    String description() default "";

    /**
     * Configuration for the classical logic node (true consistency, true completeness).
     * Cardinality: (Many, Many). Axiom schema: Γ,A⊢A,Δ. All structural rules.
     */
    DiamondGraphNode classicalNode();

    /**
     * Configuration for the intuitionistic logic node (true consistency, false completeness).
     * Cardinality: (Many, One). Axiom schema: Γ,A⊢A. Left-side structural rules only.
     */
    DiamondGraphNode intuitionisticNode();

    /**
     * Configuration for the paraconsistent logic node (false consistency, true completeness).
     * Cardinality: (One, Many). Axiom schema: A⊢A,Δ. Right-side structural rules only.
     */
    DiamondGraphNode paraconsistentNode();

    /**
     * Configuration for the common logic node (false consistency, false completeness).
     * Cardinality: (One, One). Axiom schema: A⊢A. No structural rules.
     */
    DiamondGraphNode commonLogicNode();

    /**
     * Optional theorem status derivations for validation.
     * Processor will auto-derive statuses and warn if they mismatch these expectations.
     */
    TheoremStatusDerivation[] theoremDerivations() default {};
}
