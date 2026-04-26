package org.subclass.processor;

import org.junit.jupiter.api.Test;
import org.subclass.processor.metadata.DiamondGraphInfo;
import org.subclass.processor.metadata.DiamondGraphNodeInfo;
import org.subclass.processor.metadata.TheoremStatusExpectation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for diamond graph parsing and theorem status derivation.
 */
public class DiamondGraphProcessorTest {

    @Test
    void testCardinalityValidation() {
        // Valid graph should construct without error
        DiamondGraphNodeInfo classicalNode = createNode(
            "classical", "Many", "Many", "Γ,A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo intuitionisticNode = createNode(
            "intuitionistic", "Many", "One", "Γ,A⊢A", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo paraconsistentNode = createNode(
            "paraconsistent", "One", "Many", "A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo commonNode = createNode(
            "common", "One", "One", "A⊢A", Set.of()
        );

        DiamondGraphInfo graphInfo = new DiamondGraphInfo(
            "TestGraph", "Test Graph", "",
            classicalNode, intuitionisticNode, paraconsistentNode, commonNode,
            new HashMap<>()
        );

        assertEquals("TestGraph", graphInfo.name());
        assertTrue(graphInfo.classicalNode().isFunctionallyComplete() == false); // Just checking it exists
    }

    @Test
    void testCardinalityValidationFailureClassical() {
        // Invalid: classical node with wrong cardinality
        // The node itself is valid, but putting it in the graph should fail
        assertThrows(IllegalArgumentException.class, () -> {
            DiamondGraphNodeInfo badClassicalNode = createNode(
                "classical", "One", "One", "Γ,A⊢A,Δ", Set.of("W", "C", "E")
            );
            DiamondGraphNodeInfo intuitionisticNode = createNode(
                "intuitionistic", "Many", "One", "Γ,A⊢A", Set.of("W", "C", "E")
            );
            DiamondGraphNodeInfo paraconsistentNode = createNode(
                "paraconsistent", "One", "Many", "A⊢A,Δ", Set.of("W", "C", "E")
            );
            DiamondGraphNodeInfo commonNode = createNode(
                "common", "One", "One", "A⊢A", Set.of()
            );
            // Creating the graph with bad classical node should throw
            new DiamondGraphInfo(
                "BadGraph", "Bad Graph", "",
                badClassicalNode, intuitionisticNode, paraconsistentNode, commonNode,
                new HashMap<>()
            );
        });
    }

    @Test
    void testLEMStatusDerivation() {
        TheoremStatusDeriver deriver = new TheoremStatusDeriver();

        DiamondGraphNodeInfo classicalNode = createNode(
            "classical", "Many", "Many", "Γ,A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo intuitionisticNode = createNode(
            "intuitionistic", "Many", "One", "Γ,A⊢A", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo paraconsistentNode = createNode(
            "paraconsistent", "One", "Many", "A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo commonNode = createNode(
            "common", "One", "One", "A⊢A", Set.of()
        );

        DiamondGraphInfo graphInfo = new DiamondGraphInfo(
            "LEM", "Law of Excluded Middle", "Test",
            classicalNode, intuitionisticNode, paraconsistentNode, commonNode,
            new HashMap<>()
        );

        // Derive LEM status for each node
        assertEquals("PROVABLE", deriver.deriveStatus("LEM", classicalNode, graphInfo));
        assertEquals("NON_PROVABLE", deriver.deriveStatus("LEM", intuitionisticNode, graphInfo));
        assertEquals("PROVABLE", deriver.deriveStatus("LEM", paraconsistentNode, graphInfo));
        assertEquals("NON_PROVABLE", deriver.deriveStatus("LEM", commonNode, graphInfo));
    }

    @Test
    void testLNCStatusDerivation() {
        TheoremStatusDeriver deriver = new TheoremStatusDeriver();

        DiamondGraphNodeInfo classicalNode = createNode(
            "classical", "Many", "Many", "Γ,A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo intuitionisticNode = createNode(
            "intuitionistic", "Many", "One", "Γ,A⊢A", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo paraconsistentNode = createNode(
            "paraconsistent", "One", "Many", "A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo commonNode = createNode(
            "common", "One", "One", "A⊢A", Set.of()
        );

        DiamondGraphInfo graphInfo = new DiamondGraphInfo(
            "LNC", "Law of Non-Contradiction", "Test",
            classicalNode, intuitionisticNode, paraconsistentNode, commonNode,
            new HashMap<>()
        );

        // Derive LNC status for each node (should be dual to LEM pattern)
        assertEquals("PROVABLE", deriver.deriveStatus("LNC", classicalNode, graphInfo));
        assertEquals("PROVABLE", deriver.deriveStatus("LNC", intuitionisticNode, graphInfo));
        assertEquals("NON_PROVABLE", deriver.deriveStatus("LNC", paraconsistentNode, graphInfo));
        assertEquals("NON_PROVABLE", deriver.deriveStatus("LNC", commonNode, graphInfo));
    }

    @Test
    void testLEMLNCDuality() {
        TheoremStatusDeriver deriver = new TheoremStatusDeriver();

        DiamondGraphNodeInfo classicalNode = createNode(
            "classical", "Many", "Many", "Γ,A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo intuitionisticNode = createNode(
            "intuitionistic", "Many", "One", "Γ,A⊢A", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo paraconsistentNode = createNode(
            "paraconsistent", "One", "Many", "A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo commonNode = createNode(
            "common", "One", "One", "A⊢A", Set.of()
        );

        DiamondGraphInfo graphInfo = new DiamondGraphInfo(
            "Duality", "Test Duality", "Test",
            classicalNode, intuitionisticNode, paraconsistentNode, commonNode,
            new HashMap<>()
        );

        // Verify duality: where LEM is PROVABLE, LNC tends to be PROVABLE (classical)
        // But in intuitionistic: LEM NON_PROVABLE, LNC PROVABLE (opposite)
        // In paraconsistent: LEM PROVABLE, LNC NON_PROVABLE (opposite)
        // In common: both NON_PROVABLE (intersection)

        String lemClassical = deriver.deriveStatus("LEM", classicalNode, graphInfo);
        String lncClassical = deriver.deriveStatus("LNC", classicalNode, graphInfo);
        assertEquals("PROVABLE", lemClassical);
        assertEquals("PROVABLE", lncClassical);

        String lemIntuitionistic = deriver.deriveStatus("LEM", intuitionisticNode, graphInfo);
        String lncIntuitionistic = deriver.deriveStatus("LNC", intuitionisticNode, graphInfo);
        assertEquals("NON_PROVABLE", lemIntuitionistic);
        assertEquals("PROVABLE", lncIntuitionistic);

        String lemParaconsistent = deriver.deriveStatus("LEM", paraconsistentNode, graphInfo);
        String lncParaconsistent = deriver.deriveStatus("LNC", paraconsistentNode, graphInfo);
        assertEquals("PROVABLE", lemParaconsistent);
        assertEquals("NON_PROVABLE", lncParaconsistent);

        String lemCommon = deriver.deriveStatus("LEM", commonNode, graphInfo);
        String lncCommon = deriver.deriveStatus("LNC", commonNode, graphInfo);
        assertEquals("NON_PROVABLE", lemCommon);
        assertEquals("NON_PROVABLE", lncCommon);
    }

    @Test
    void testDoubleNegationIntroduction() {
        TheoremStatusDeriver deriver = new TheoremStatusDeriver();

        DiamondGraphNodeInfo classicalNode = createNode(
            "classical", "Many", "Many", "Γ,A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo intuitionisticNode = createNode(
            "intuitionistic", "Many", "One", "Γ,A⊢A", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo paraconsistentNode = createNode(
            "paraconsistent", "One", "Many", "A⊢A,Δ", Set.of("W", "C", "E")
        );
        DiamondGraphNodeInfo commonNode = createNode(
            "common", "One", "One", "A⊢A", Set.of()
        );

        DiamondGraphInfo graphInfo = new DiamondGraphInfo(
            "DNI", "Double Negation Introduction", "Test",
            classicalNode, intuitionisticNode, paraconsistentNode, commonNode,
            new HashMap<>()
        );

        // Double negation introduction should be provable in all logics
        assertEquals("PROVABLE", deriver.deriveStatus("DoubleNegationIntroduction", classicalNode, graphInfo));
        assertEquals("PROVABLE", deriver.deriveStatus("DoubleNegationIntroduction", intuitionisticNode, graphInfo));
        assertEquals("PROVABLE", deriver.deriveStatus("DoubleNegationIntroduction", paraconsistentNode, graphInfo));
        assertEquals("PROVABLE", deriver.deriveStatus("DoubleNegationIntroduction", commonNode, graphInfo));
    }

    // Helper to create a node
    private DiamondGraphNodeInfo createNode(
        String position,
        String antecedentCard,
        String succedentCard,
        String axiomSchema,
        Set<String> rules
    ) {
        return new DiamondGraphNodeInfo(
            position, antecedentCard, succedentCard, axiomSchema,
            rules, Set.of("∧", "∨", "¬", "→"), false
        );
    }
}
