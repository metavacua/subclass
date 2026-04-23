package org.subclass.engine;

import java.util.*;

/**
 * Functor C (Canonicalization): Performs transitive closure on the extensive layer.
 * This simplifies reachability queries by ensuring that if A can reach B via a sequence 
 * of EXTENSIVE edges, there is a direct EXTENSIVE edge from A to B.
 */
public class FunctorC {

    /**
     * Applies transitive closure to the EXTENSIVE edges of the given graph.
     * @param graph The graph to modify.
     */
    public void apply(RankedGraph graph) {
        List<Node> nodes = new ArrayList<>(graph.getNodes());
        
        for (Node startNode : nodes) {
            Set<String> reachable = findExtensiveReachable(graph, startNode.id());
            for (String targetId : reachable) {
                if (!startNode.id().equals(targetId)) {
                    // Ensure direct EXTENSIVE edge exists
                    boolean exists = graph.getEdgesFrom(startNode.id()).stream()
                            .anyMatch(e -> e.targetId().equals(targetId) && e.type() == EdgeType.EXTENSIVE);
                    
                    if (!exists) {
                        graph.addEdge(new Edge(startNode.id(), targetId, EdgeType.EXTENSIVE));
                    }
                }
            }
        }
    }

    private Set<String> findExtensiveReachable(RankedGraph graph, String startId) {
        Set<String> reachable = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(startId);

        while (!stack.isEmpty()) {
            String currentId = stack.pop();
            Node current = graph.getNode(currentId).orElseThrow();

            for (Edge edge : graph.getEdgesFrom(currentId)) {
                if (edge.type() == EdgeType.EXTENSIVE) {
                    Node target = graph.getNode(edge.targetId()).orElseThrow();
                    // Adhere to rank discipline
                    if (target.rank() > current.rank()) {
                        if (reachable.add(target.id())) {
                            stack.push(target.id());
                        }
                    }
                }
            }
        }
        return reachable;
    }
}
