package org.subclass.engine;

/**
 * Types of edges in the Ranked Graph.
 * - EXTENSIVE: Rank-increasing moves (poset layer).
 * - DUALITY: Rank-preserving moves (duality layer).
 * - SIGMA: Axiom links between polar opposites.
 */
public enum EdgeType {
    EXTENSIVE,
    DUALITY,
    SIGMA
}
