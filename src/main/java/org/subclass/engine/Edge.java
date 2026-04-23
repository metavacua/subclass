package org.subclass.engine;

import java.util.Objects;

/**
 * A directed edge between two nodes in the Ranked Graph.
 *
 * @param sourceId ID of the source node.
 * @param targetId ID of the target node.
 * @param type The type of relationship between the nodes.
 */
public record Edge(String sourceId, String targetId, EdgeType type) {
    public Edge {
        Objects.requireNonNull(sourceId, "sourceId cannot be null");
        Objects.requireNonNull(targetId, "targetId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
    }
}
