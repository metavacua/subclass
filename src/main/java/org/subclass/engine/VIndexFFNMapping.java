package org.subclass.engine;

/**
 * Provides a mapping from Gated FFN components to a Ranked Graph.
 * FFN(x) = (SiLU(x * W_gate) * (x * W_up)) * W_down
 *
 * Ranks are assigned to mirror the sequence of operations:
 * Rank 0: Inputs (x, W_gate, W_up)
 * Rank 1: Linear projections (h_gate, h_up)
 * Rank 2: Activation (h_silu)
 * Rank 3: Element-wise product and W_down parameter
 * Rank 4: Output (y)
 */
public class VIndexFFNMapping {

    /**
     * Builds a RankedGraph representing the gated FFN operation.
     * @return A RankedGraph populated with nodes and edges for the FFN.
     */
    public RankedGraph buildFFNGraph() {
        RankedGraph graph = new RankedGraph();

        // --- Rank 0: Inputs ---
        Node x = new Node("x", 0, Polarity.POSITIVE, "input_x".hashCode());
        Node wGate = new Node("W_gate", 0, Polarity.POSITIVE, "weight_gate".hashCode());
        Node wUp = new Node("W_up", 0, Polarity.POSITIVE, "weight_up".hashCode());
        
        graph.addNode(x);
        graph.addNode(wGate);
        graph.addNode(wUp);

        // --- Rank 1: Projections ---
        Node hGate = new Node("h_gate", 1, Polarity.POSITIVE, "hidden_gate".hashCode());
        Node hUp = new Node("h_up", 1, Polarity.POSITIVE, "hidden_up".hashCode());
        
        graph.addNode(hGate);
        graph.addNode(hUp);

        // Extension edges for projections
        graph.addEdge(new Edge("x", "h_gate", EdgeType.EXTENSIVE));
        graph.addEdge(new Edge("W_gate", "h_gate", EdgeType.EXTENSIVE));
        graph.addEdge(new Edge("x", "h_up", EdgeType.EXTENSIVE));
        graph.addEdge(new Edge("W_up", "h_up", EdgeType.EXTENSIVE));
        
        // Duality edge between parallel projections
        graph.addEdge(new Edge("h_gate", "h_up", EdgeType.DUALITY));

        // --- Rank 2: Activation ---
        Node hSiLU = new Node("h_silu", 2, Polarity.POSITIVE, "hidden_silu".hashCode());
        graph.addNode(hSiLU);
        
        graph.addEdge(new Edge("h_gate", "h_silu", EdgeType.EXTENSIVE));

        // --- Rank 3: Product ---
        Node hProd = new Node("h_prod", 3, Polarity.POSITIVE, "hidden_prod".hashCode());
        Node wDown = new Node("W_down", 3, Polarity.POSITIVE, "weight_down".hashCode());
        
        graph.addNode(hProd);
        graph.addNode(wDown);
        
        graph.addEdge(new Edge("h_silu", "h_prod", EdgeType.EXTENSIVE));
        graph.addEdge(new Edge("h_up", "h_prod", EdgeType.EXTENSIVE));
        
        // Duality edge between product result and down-projection weight
        graph.addEdge(new Edge("h_prod", "W_down", EdgeType.DUALITY));

        // --- Rank 4: Output ---
        Node y = new Node("y", 4, Polarity.POSITIVE, "output_y".hashCode());
        graph.addNode(y);
        
        graph.addEdge(new Edge("h_prod", "y", EdgeType.EXTENSIVE));
        graph.addEdge(new Edge("W_down", "y", EdgeType.EXTENSIVE));

        return graph;
    }
}
