package org.subclass.processor;

import org.subclass.annotation.GraphEdge.MorphismType;
import java.util.*;

/**
 * In-memory model of a logic graph specification. Provides structured access to nodes and edges
 * and validation of graph properties.
 */
public class GraphSpecification {

    public enum GraphType {
        DIAMOND,  // 5 extension morphisms
        K4        // 5 extension + 1 duality morphism
    }

    private final String name;
    private final GraphType type;
    private final String baseSignature;
    private final String generatedPackage;
    private final String displayName;
    private final String reference;

    private final Map<String, NodeSpec> nodes; // id -> NodeSpec
    private final List<EdgeSpec> edges;

    public GraphSpecification(
            String name,
            GraphType type,
            String baseSignature,
            String generatedPackage,
            String displayName,
            String reference) {
        this.name = name;
        this.type = type;
        this.baseSignature = baseSignature;
        this.generatedPackage = generatedPackage;
        this.displayName = displayName;
        this.reference = reference;
        this.nodes = new LinkedHashMap<>();
        this.edges = new ArrayList<>();
    }

    public void addNode(NodeSpec node) {
        if (nodes.containsKey(node.id())) {
            throw new IllegalArgumentException("Duplicate node ID: " + node.id());
        }
        nodes.put(node.id(), node);
    }

    public void addEdge(EdgeSpec edge) {
        edges.add(edge);
    }

    public void validate() throws GraphValidationException {
        // Must have exactly 4 nodes
        if (nodes.size() != 4) {
            throw new GraphValidationException("Graph must have exactly 4 nodes, found: " + nodes.size());
        }

        // Validate all edge endpoints exist
        for (EdgeSpec edge : edges) {
            if (!nodes.containsKey(edge.from())) {
                throw new GraphValidationException("Edge references unknown node: " + edge.from());
            }
            if (!nodes.containsKey(edge.to())) {
                throw new GraphValidationException("Edge references unknown node: " + edge.to());
            }
        }

        // Validate edge count and morphism types
        long extensionCount = edges.stream()
            .filter(e -> e.morphismType() == MorphismType.EXTENSION)
            .count();
        long dualityCount = edges.stream()
            .filter(e -> e.morphismType() == MorphismType.DUALITY)
            .count();

        if (type == GraphType.DIAMOND) {
            if (extensionCount != 5 || dualityCount != 0) {
                throw new GraphValidationException(
                    "Diamond must have 5 EXTENSION morphisms and 0 DUALITY morphisms, found: "
                    + extensionCount + " extensions, " + dualityCount + " duality");
            }
        } else if (type == GraphType.K4) {
            if (extensionCount != 5 || dualityCount != 1) {
                throw new GraphValidationException(
                    "K4 must have 5 EXTENSION morphisms and 1 DUALITY morphism, found: "
                    + extensionCount + " extensions, " + dualityCount + " duality");
            }
        }
    }

    // Accessors
    public String name() { return name; }
    public GraphType type() { return type; }
    public String baseSignature() { return baseSignature; }
    public String generatedPackage() { return generatedPackage; }
    public String displayName() { return displayName; }
    public String reference() { return reference; }

    public Collection<NodeSpec> nodes() { return nodes.values(); }
    public NodeSpec node(String id) { return nodes.get(id); }
    public List<EdgeSpec> edges() { return edges; }

    /**
     * Node specification within a graph.
     */
    public record NodeSpec(
            String id,
            String name,
            String displayName,
            boolean consistent,
            boolean complete,
            String[] connectives,
            String[] structuralRules,
            String[] properties) {}

    /**
     * Edge (morphism) specification within a graph.
     */
    public record EdgeSpec(
            String from,
            String to,
            MorphismType morphismType,
            String description) {}

    /**
     * Validation error in graph specification.
     */
    public static class GraphValidationException extends Exception {
        public GraphValidationException(String message) {
            super(message);
        }
        public GraphValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
