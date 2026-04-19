package org.subclass.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as a template or trigger for logic generation. The annotation processor
 * will generate complete logic implementations (signatures, proof types, rules) from the
 * graph specification referenced here.
 *
 * <p>When a class is annotated with @GeneratedLogic, the processor generates:
 * <ul>
 *   <li>{LogicName}Signature - Logic definition with connectives and rules</li>
 *   <li>{LogicName}Proof - Sealed proof interface for this logic's Proof&lt;L,R&gt; types</li>
 *   <li>{LogicName}Rules - Inference rule registry</li>
 *   <li>{LogicName}Connector - Inter-logic relationship bridge</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GeneratedLogic {

    /**
     * Reference to the @LogicGraph annotation that specifies the overall graph structure.
     * The processor uses this to locate the graph specification.
     * Example: "org.subclass.examples.GeneratedLEMDiamond"
     */
    String graphReference();

    /**
     * ID of the node in the referenced graph that this logic implements.
     * Must match one of the node IDs in the graph specification.
     * Example: "LK", "LJ", "LDJ"
     */
    String nodeReference();

    /**
     * Optional override connectives for this node.
     * If provided, replaces inherited connectives from base signature.
     * Format: "AND, OR, NOT, IMPLIES" or similar.
     */
    String connectives() default "";

    /**
     * Optional override structural rules for this node.
     * If provided, replaces inherited rules.
     * Format: "WEAKENING, CONTRACTION, EXCHANGE" or similar.
     */
    String structuralRules() default "";

    /**
     * Whether this logic is functionally complete (can express all boolean functions).
     * If not specified, computed from the graph structure.
     */
    boolean functionallyComplete() default true;

    /**
     * Optional description for documentation.
     */
    String description() default "";
}
