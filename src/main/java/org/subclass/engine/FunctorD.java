package org.subclass.engine;

import java.util.*;

/**
 * Functor D (Dualization): Identifies polar opposites and adds axiom links (SIGMA edges).
 * Polar opposites are nodes that:
 * 1. Have antipodal polarities.
 * 2. Represent the same formula (matching formula hashes).
 * 3. Are incomparable in the extensive layer (no extensive path between them).
 * 4. Share at least one common sink in the extensive layer.
 */
public class FunctorD {

    /**
     * Applies dualization to the graph, adding SIGMA edges between polar opposites.
     * @param graph The graph to modify.
     */
    public void apply(RankedGraph graph) {
        List<Node> nodes = new ArrayList<>(graph.getNodes());
        
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Node n1 = nodes.get(i);
                Node n2 = nodes.get(j);

                if (isPolarOpposite(graph, n1, n2)) {
                    graph.addEdge(new Edge(n1.id(), n2.id(), EdgeType.SIGMA));
                    graph.addEdge(new Edge(n2.id(), n1.id(), EdgeType.SIGMA));
                }
            }
        }
    }

    private boolean isPolarOpposite(RankedGraph graph, Node n1, Node n2) {
        // 1. Antipodal polarities
        if (n1.polarity() == n2.polarity()) {
            return false;
        }

        // 2. Same formula content
        if (n1.formulaHash() != n2.formulaHash()) {
            return false;
        }

        // 3. Incomparable in the extensive layer
        // Assumes Functor C (transitive closure) has been applied for efficiency.
        if (hasDirectExtensiveEdge(graph, n1.id(), n2.id()) || hasDirectExtensiveEdge(graph, n2.id(), n1.id())) {
            return false;
        }

        // 4. Share a common sink in the extensive layer
        Set<String> sinks1 = findExtensiveSinks(graph, n1.id());
        Set<String> sinks2 = findExtensiveSinks(graph, n2.id());
        
        // Check for intersection
        for (String sinkId : sinks1) {
            if (sinks2.contains(sinkId)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasDirectExtensiveEdge(RankedGraph graph, String sourceId, String targetId) {
        return graph.getEdgesFrom(sourceId).stream()
                .anyMatch(e -> e.type() == EdgeType.EXTENSIVE && e.targetId().equals(targetId));
    }

    private Set<String> findExtensiveSinks(RankedGraph graph, String startId) {
        Set<String> sinks = new HashSet<>();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        
        stack.push(startId);
        visited.add(startId);

        while (!stack.isEmpty()) {
            String currentId = stack.pop();
            
            boolean hasOutgoingExtensive = false;
            for (Edge edge : graph.getEdgesFrom(currentId)) {
                if (edge.type() == EdgeType.EXTENSIVE) {
                    hasOutgoingExtensive = true;
                    if (visited.add(edge.targetId())) {
                        stack.push(edge.targetId());
                    }
                }
            }
            
            if (!hasOutgoingExtensive) {
                sinks.add(currentId);
            }
        }
        return sinks;
    }
}
