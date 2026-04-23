package org.subclass.engine;

import java.util.*;
import java.util.Objects;

/**
 * Engine for traversing the Ranked Graph according to rank discipline.
 */
public class RankedGraphWalkEngine {
    private final RankedGraph graph;

    public RankedGraphWalkEngine(RankedGraph graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    /**
     * Finds all nodes reachable from a starting node while adhering to rank rules.
     * @param startNodeId ID of the starting node.
     * @return Set of reachable node IDs.
     */
    public Set<String> getReachableNodes(String startNodeId) {
        Set<String> reachable = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        
        stack.push(startNodeId);
        reachable.add(startNodeId);

        while (!stack.isEmpty()) {
            String currentId = stack.pop();
            Node current = graph.getNode(currentId).orElseThrow();

            for (Edge edge : graph.getEdgesFrom(currentId)) {
                Node target = graph.getNode(edge.targetId()).orElseThrow();
                if (isValidMove(current, target, edge.type())) {
                    if (reachable.add(target.id())) {
                        stack.push(target.id());
                    }
                }
            }
        }
        return reachable;
    }

    /**
     * Validates if a move between two nodes is allowed based on edge type and ranks.
     * - EXTENSIVE: Must increase rank.
     * - DUALITY: Must preserve rank.
     * - SIGMA: Arbitrary (usually connects leaf nodes).
     *
     * @param source Source node.
     * @param target Target node.
     * @param type Edge type.
     * @return true if the move is valid.
     */
    public boolean isValidMove(Node source, Node target, EdgeType type) {
        return switch (type) {
            case EXTENSIVE -> target.rank() > source.rank();
            case DUALITY -> target.rank() == source.rank();
            case SIGMA -> true; 
        };
    }
    
    /**
     * Get the underlying graph.
     * @return The ranked graph.
     */
    public RankedGraph getGraph() {
        return graph;
    }
}
