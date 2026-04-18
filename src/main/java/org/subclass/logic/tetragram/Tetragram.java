package org.subclass.logic.tetragram;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a tetragram (4-node diamond graph) structure.
 * A tetragram consists of four logical systems arranged in a diamond shape,
 * where each node represents a logic characterized by its consistency and completeness properties.
 *
 * The four nodes are indexed by (boolean, boolean) coordinates:
 * - (true, true): Consistent & Complete (e.g., LK - classical logic)
 * - (true, false): Consistent & Paracomplete (e.g., LJ - intuitionistic logic)
 * - (false, true): Paraconsistent & Complete (e.g., dual calculus)
 * - (false, false): Paraconsistent & Paracomplete (e.g., common logic)
 *
 * A theorem in one node has corresponding statuses in the other three nodes,
 * forming a meta-theorem about the theorem's relationship across logics.
 *
 * @param <T> The type of theorem or proposition being formalized
 */
public class Tetragram<T> {
    private final String name;
    private final Map<String, TetragramNode> nodes;
    private final Map<T, TheoremStatus[]> theoremStatuses; // Maps theorem to 4-tuple of statuses

    /**
     * Create a tetragram with four pre-configured nodes.
     *
     * @param name Name of this tetragram family (e.g., "LEM")
     * @param consistentComplete Node at (true, true)
     * @param consistentParacomplete Node at (true, false)
     * @param paraconsistentComplete Node at (false, true)
     * @param paraconsistentParacomplete Node at (false, false)
     */
    public Tetragram(String name,
                    TetragramNode consistentComplete,
                    TetragramNode consistentParacomplete,
                    TetragramNode paraconsistentComplete,
                    TetragramNode paraconsistentParacomplete) {
        this.name = Objects.requireNonNull(name);
        this.nodes = new HashMap<>();
        this.theoremStatuses = new HashMap<>();

        // Add nodes with coordinate keys
        addNode(consistentComplete);
        addNode(consistentParacomplete);
        addNode(paraconsistentComplete);
        addNode(paraconsistentParacomplete);

        // Validate we have all 4 nodes
        if (nodes.size() != 4) {
            throw new IllegalArgumentException("Tetragram must have exactly 4 nodes");
        }
    }

    private void addNode(TetragramNode node) {
        nodes.put(node.getCoordinate(), node);
    }

    public String getName() {
        return name;
    }

    /**
     * Get a specific node by its consistency and completeness properties.
     *
     * @param consistent Whether the node is consistent
     * @param complete Whether the node is complete
     * @return The node at those coordinates
     */
    public TetragramNode getNode(boolean consistent, boolean complete) {
        String key = "<" + consistent + ", " + complete + ">";
        TetragramNode node = nodes.get(key);
        if (node == null) {
            throw new IllegalArgumentException("No node at coordinates " + key);
        }
        return node;
    }

    /**
     * Get the classical logic node (consistent & complete).
     */
    public TetragramNode getClassicalNode() {
        return getNode(true, true);
    }

    /**
     * Get the intuitionistic-like node (consistent & paracomplete).
     */
    public TetragramNode getIntuitionisticNode() {
        return getNode(true, false);
    }

    /**
     * Get the paraconsistent-complete node (paraconsistent & complete).
     */
    public TetragramNode getParaconsistentCompleteNode() {
        return getNode(false, true);
    }

    /**
     * Get the common logic node (paraconsistent & paracomplete).
     */
    public TetragramNode getCommonLogicNode() {
        return getNode(false, false);
    }

    /**
     * Get all four nodes in order: [Classical, Intuitionistic, Paraconsistent-Complete, Common Logic]
     */
    public TetragramNode[] getAllNodes() {
        return new TetragramNode[]{
            getClassicalNode(),
            getIntuitionisticNode(),
            getParaconsistentCompleteNode(),
            getCommonLogicNode()
        };
    }

    /**
     * Register the status of a theorem in all four nodes.
     *
     * @param theorem The theorem being formalized
     * @param classicalStatus Status in the classical node
     * @param intuitionisticStatus Status in the intuitionistic node
     * @param paraconsistentCompleteStatus Status in the paraconsistent-complete node
     * @param commonLogicStatus Status in the common logic node
     */
    public void registerTheorem(T theorem,
                               TheoremStatus classicalStatus,
                               TheoremStatus intuitionisticStatus,
                               TheoremStatus paraconsistentCompleteStatus,
                               TheoremStatus commonLogicStatus) {
        TheoremStatus[] statuses = {
            classicalStatus,
            intuitionisticStatus,
            paraconsistentCompleteStatus,
            commonLogicStatus
        };
        theoremStatuses.put(theorem, statuses);
    }

    /**
     * Get the status of a theorem in a specific node.
     *
     * @param theorem The theorem
     * @param node The node
     * @return The theorem's status in that node
     */
    public TheoremStatus getTheoremStatus(T theorem, TetragramNode node) {
        TheoremStatus[] statuses = theoremStatuses.get(theorem);
        if (statuses == null) {
            throw new IllegalArgumentException("Theorem not registered: " + theorem);
        }

        if (node.isClassical()) return statuses[0];
        if (node.getCoordinate().equals("<true, false>")) return statuses[1];
        if (node.getCoordinate().equals("<false, true>")) return statuses[2];
        return statuses[3];
    }

    /**
     * Get the 4-tuple of theorem statuses across all nodes in order.
     *
     * @param theorem The theorem
     * @return Array [classicalStatus, intuitionisticStatus, paraconsistentCompleteStatus, commonLogicStatus]
     */
    public TheoremStatus[] getTheoremStatuses(T theorem) {
        TheoremStatus[] statuses = theoremStatuses.get(theorem);
        if (statuses == null) {
            throw new IllegalArgumentException("Theorem not registered: " + theorem);
        }
        return statuses.clone();
    }

    /**
     * Check if the tetragram satisfies the duality constraint:
     * If theorem is PROVABLE in the classical node, its dual should have
     * a consistent relationship in the paraconsistent-complete node.
     *
     * This is a meta-level constraint, not enforced at construction time.
     */
    public boolean validateDualityConstraint(T theorem) {
        TheoremStatus classicalStatus = getTheoremStatus(theorem, getClassicalNode());
        TheoremStatus paraconsistentStatus = getTheoremStatus(theorem, getParaconsistentCompleteNode());

        // If provable in classical, should not be provable in paraconsistent
        // (since paraconsistent rejects explosion, which classical relies on)
        if (classicalStatus.isProvable() && paraconsistentStatus.isProvable()) {
            return false; // Constraint violated
        }
        return true;
    }

    /**
     * Check if the tetragram satisfies the completeness constraint:
     * In complete logics, theorems should be either provable or refutable.
     * In paracomplete logics, theorems may be neither.
     */
    public boolean validateCompletenessConstraint(T theorem) {
        TheoremStatus classicalStatus = getTheoremStatus(theorem, getClassicalNode());
        TheoremStatus paraconsistentCompleteStatus = getTheoremStatus(theorem, getParaconsistentCompleteNode());

        // In complete logics, theorem must be determined (provable or refutable)
        if (!classicalStatus.isDetermined() || !paraconsistentCompleteStatus.isDetermined()) {
            return false;
        }

        // In paracomplete logics, theorem may be undetermined (UNPROVABLE_AND_REFUTABLE allowed)
        return true;
    }

    /**
     * Get a human-readable summary of the tetragram structure and a theorem's status.
     */
    public String summarizeTheorem(T theorem) {
        StringBuilder sb = new StringBuilder();
        sb.append("Theorem: ").append(theorem).append("\n");
        sb.append("Tetragram: ").append(name).append("\n\n");

        TheoremStatus[] statuses = getTheoremStatuses(theorem);
        TetragramNode[] nodeArray = getAllNodes();

        for (int i = 0; i < 4; i++) {
            sb.append(nodeArray[i].toString()).append(": ").append(statuses[i].getDisplayName()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Tetragram{" + name + ", nodes=" + nodes.size() + ", theorems=" + theoremStatuses.size() + "}";
    }
}
