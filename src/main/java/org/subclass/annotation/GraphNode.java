package org.subclass.annotation;

import java.lang.annotation.*;

/**
 * Specifies a single node in a logic graph. Each node represents a logical system
 * characterized by consistency and completeness properties.
 *
 * <p>Standard 4-node tetragram positions (using boolean coordinates):
 * <ul>
 *   <li>(consistent=true, complete=true) → Classical logic (LK)</li>
 *   <li>(consistent=true, complete=false) → Intuitionistic logic (LJ)</li>
 *   <li>(consistent=false, complete=true) → Paraconsistent dual (LDJ)</li>
 *   <li>(consistent=false, complete=false) → Common logic intersection</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GraphNode {

    /**
     * Unique identifier for this node within the graph.
     * Example: "LK", "LJ", "LDJ", "COMMON"
     */
    String id();

    /**
     * Name of the logic this node represents.
     * Example: "LK" (Classical), "LJ" (Intuitionistic), "LDJ" (Paraconsistent)
     */
    String name();

    /**
     * Human-readable display name for documentation.
     * Example: "Classical Sequent Calculus"
     */
    String displayName();

    /**
     * Whether this logical system is consistent (no true contradictions).
     * In classical logic: true
     * In paraconsistent logic: false
     */
    boolean consistent();

    /**
     * Whether this logical system is complete (all formulas are decidable).
     * In classical/complete intuitionistic: true
     * In incomplete logics: false
     */
    boolean complete();

    /**
     * Connectives available in this node's logic (e.g., "AND, OR, NOT, IMPLIES").
     * If empty, inherits from base signature.
     */
    String[] connectives() default {};

    /**
     * Structural rules available (e.g., "WEAKENING, CONTRACTION, EXCHANGE").
     * If empty, inherits from base signature.
     */
    String[] structuralRules() default {};

    /**
     * Optional metadata key-value pairs for extensibility.
     */
    String[] properties() default {};
}
