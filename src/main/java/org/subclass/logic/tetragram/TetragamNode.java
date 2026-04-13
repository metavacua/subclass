package org.subclass.logic.tetragram;

import org.subclass.logic.signature.LogicalSignature;
import java.util.Objects;

/**
 * Represents a single node in a tetragram (diamond graph).
 * A node corresponds to a specific logical system characterized by its
 * consistency and completeness properties.
 *
 * The four nodes of a standard tetragram are:
 * 1. <Consistent=true, Complete=true> - Classical logic (e.g., LK)
 * 2. <Consistent=true, Complete=false> - Complete but not allowing LEM (e.g., LJ)
 * 3. <Consistent=false, Complete=true> - Paraconsistent but complete (e.g., dual of LJ)
 * 4. <Consistent=false, Complete=false> - Neither consistent nor complete (e.g., common logic)
 *
 * Each node is associated with a logical signature that defines its vocabulary and rules.
 */
public class TetragamNode {
    private final boolean consistent;
    private final boolean complete;
    private final LogicalSignature signature;
    private final String description;

    /**
     * Create a tetragram node.
     *
     * @param consistent Whether this logic satisfies the Law of Non-Contradiction (LNC)
     * @param complete Whether this logic satisfies the Law of Excluded Middle (LEM) or similar completeness
     * @param signature The logical signature for this node
     * @param description Human-readable description of this node's properties
     */
    public TetragamNode(boolean consistent, boolean complete, LogicalSignature signature, String description) {
        this.consistent = consistent;
        this.complete = complete;
        this.signature = Objects.requireNonNull(signature);
        this.description = description;
    }

    public boolean isConsistent() {
        return consistent;
    }

    public boolean isComplete() {
        return complete;
    }

    public LogicalSignature getSignature() {
        return signature;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get the coordinate of this node in the tetragram.
     * Returns a string like "<true, false>" representing (consistent, complete).
     */
    public String getCoordinate() {
        return "<" + consistent + ", " + complete + ">";
    }

    /**
     * Get the quadrant of this node.
     * - "I": Consistent & Complete (classical)
     * - "II": Consistent & Paracomplete (intuitionistic)
     * - "III": Paraconsistent & Complete (dual)
     * - "IV": Paraconsistent & Paracomplete (common logic)
     */
    public String getQuadrant() {
        if (consistent && complete) return "I";
        if (consistent && !complete) return "II";
        if (!consistent && complete) return "III";
        return "IV";
    }

    /**
     * Check if this node is the classical logic node (consistent & complete).
     */
    public boolean isClassical() {
        return consistent && complete;
    }

    /**
     * Check if this node is paraconsistent.
     */
    public boolean isParaconsistent() {
        return !consistent;
    }

    /**
     * Check if this node is paracomplete (not complete).
     */
    public boolean isParacomplete() {
        return !complete;
    }

    @Override
    public String toString() {
        return "Node " + getQuadrant() + " " + getCoordinate() + ": " + signature.name()
               + " (" + signature.displayName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TetragamNode that = (TetragamNode) o;
        return consistent == that.consistent && complete == that.complete
               && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consistent, complete, signature);
    }
}
