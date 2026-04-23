package org.subclass.engine;

import java.util.Objects;

/**
 * A node in the Ranked Graph.
 *
 * @param id Unique identifier for the node.
 * @param rank The rank of the node (nesting depth).
 * @param polarity The logical polarity (positive or negative).
 * @param formulaHash Hash code of the associated logical formula for comparison.
 */
public record Node(String id, int rank, Polarity polarity, int formulaHash) {
    public Node {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(polarity, "polarity cannot be null");
    }
}
