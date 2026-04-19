package org.subclass.annotation;

import java.lang.annotation.*;

/**
 * Specifies a morphism (edge) between two nodes in a logic graph.
 * Morphisms represent relationships between logical systems:
 * <ul>
 *   <li>EXTENSION: Logical or theoretical extension (subsumes diamond structure)</li>
 *   <li>DUALITY: External negation relation between calculi (additional edge in K4)</li>
 * </ul>
 *
 * <p>In a diamond graph: 5 EXTENSION morphisms forming a lattice
 * <p>In a K4 graph: 5 EXTENSION morphisms + 1 DUALITY morphism (external negation)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GraphEdge {

    /**
     * Source node ID (from).
     */
    String from();

    /**
     * Target node ID (to).
     */
    String to();

    /**
     * Type of morphism between these nodes.
     */
    MorphismType morphismType();

    /**
     * Optional description of this morphism.
     * Example: "classical extension to intuitionistic"
     */
    String description() default "";

    enum MorphismType {
        /**
         * Logical or theoretical extension morphism.
         * Indicates one logic extends or subsumes another through additional structure/constraints.
         */
        EXTENSION,

        /**
         * Duality morphism representing external negation relation.
         * In K4, connects logics via their negation properties (double negation failures).
         * Only one per K4 graph.
         */
        DUALITY
    }
}
