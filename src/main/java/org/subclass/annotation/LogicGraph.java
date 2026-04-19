package org.subclass.annotation;

import java.lang.annotation.*;

/**
 * Defines a logic graph structure (diamond or K4) that serves as a template for generating
 * logic implementations. A graph specification describes the structure and relationships between
 * logical systems in terms of morphisms.
 *
 * <p><b>Graph Types:</b>
 * <ul>
 *   <li>DIAMOND: 4 nodes with 5 EXTENSION morphisms (forms lattice of logics)</li>
 *   <li>K4: 4 nodes with 5 EXTENSION + 1 DUALITY morphism (mutually-definable calculi)</li>
 * </ul>
 *
 * <p>The duality morphism in K4 represents an external negation relation between calculi,
 * while extension morphisms represent logical/theoretical extensions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LogicGraph {

    /**
     * Name of this graph specification (e.g., "LemmaGraphDiamond").
     */
    String name();

    /**
     * Graph type: DIAMOND (5 edges) or K4 (6 edges).
     */
    GraphType graphType();

    /**
     * Node specifications defining the logical systems in this graph.
     * Must be exactly 4 nodes.
     */
    GraphNode[] nodes();

    /**
     * Edge specifications defining morphisms between nodes.
     * Diamond: 5 EXTENSION morphisms
     * K4: 5 EXTENSION + 1 DUALITY morphism
     */
    GraphEdge[] edges();

    /**
     * Reference to base logical signature definitions that logics in this graph inherit from.
     * Example: "LK", "LJ", "LDJ", "Common"
     */
    String baseSignature() default "LK";

    /**
     * Package where generated logic implementations will be placed.
     * Example: "org.subclass.logic.generated"
     */
    String generatedPackage() default "org.subclass.logic.generated";

    /**
     * Human-readable display name for documentation purposes.
     */
    String displayName() default "";

    /**
     * Reference documentation or citations for this graph specification.
     */
    String reference() default "";

    enum GraphType {
        DIAMOND,  // 4 nodes, 5 extension morphisms
        K4        // 4 nodes, 5 extension morphisms + 1 duality morphism
    }
}
