package org.subclass.engine;

import java.util.*;

/**
 * A container for nodes and edges with efficient lookup.
 * Follows the pattern of maintaining indexed maps for fast traversal.
 */
public class RankedGraph {
    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Map<String, List<Edge>> outgoingEdges = new HashMap<>();
    private final Map<String, List<Edge>> incomingEdges = new HashMap<>();

    /**
     * Add a node to the graph.
     * @param node The node to add.
     */
    public void addNode(Node node) {
        nodes.put(node.id(), node);
    }

    /**
     * Add an edge to the graph.
     * Both source and target nodes must already exist in the graph.
     * @param edge The edge to add.
     * @throws IllegalArgumentException if source or target node is missing.
     */
    public void addEdge(Edge edge) {
        if (!nodes.containsKey(edge.sourceId())) {
            throw new IllegalArgumentException("Source node " + edge.sourceId() + " must exist in the graph");
        }
        if (!nodes.containsKey(edge.targetId())) {
            throw new IllegalArgumentException("Target node " + edge.targetId() + " must exist in the graph");
        }
        edges.add(edge);
        outgoingEdges.computeIfAbsent(edge.sourceId(), k -> new ArrayList<>()).add(edge);
        incomingEdges.computeIfAbsent(edge.targetId(), k -> new ArrayList<>()).add(edge);
    }

    /**
     * Get a node by its unique identifier.
     * @param id Node ID.
     * @return Optional containing the node if found.
     */
    public Optional<Node> getNode(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    /**
     * Get all nodes in the graph.
     * @return Unmodifiable collection of nodes.
     */
    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    /**
     * Get all edges in the graph.
     * @return Unmodifiable list of edges.
     */
    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    /**
     * Get all outgoing edges from a specific node.
     * @param sourceId ID of the source node.
     * @return List of outgoing edges.
     */
    public List<Edge> getEdgesFrom(String sourceId) {
        return Collections.unmodifiableList(outgoingEdges.getOrDefault(sourceId, Collections.emptyList()));
    }

    /**
     * Get all incoming edges to a specific node.
     * @param targetId ID of the target node.
     * @return List of incoming edges.
     */
    public List<Edge> getEdgesTo(String targetId) {
        return Collections.unmodifiableList(incomingEdges.getOrDefault(targetId, Collections.emptyList()));
    }
}
