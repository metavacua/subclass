package org.subclass.processor;

import org.junit.jupiter.api.Test;
import org.subclass.logic.tetragram.TheoremStatus;
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

        // Derive LEM status for each node using 2D model
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, deriver.deriveStatus("LEM", classicalNode, graphInfo));
        assertEquals(TheoremStatus.NON_PROVABLE_UNREFUTABLE, deriver.deriveStatus("LEM", intuitionisticNode, graphInfo));
        assertEquals(TheoremStatus.PROVABLE_REFUTABLE, deriver.deriveStatus("LEM", paraconsistentNode, graphInfo));
        assertEquals(TheoremStatus.NON_PROVABLE_UNREFUTABLE, deriver.deriveStatus("LEM", commonNode, graphInfo));
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

        // Derive LNC status for each node (provability duals of LEM)
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, deriver.deriveStatus("LNC", classicalNode, graphInfo));
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, deriver.deriveStatus("LNC", intuitionisticNode, graphInfo));
        assertEquals(TheoremStatus.NON_PROVABLE_REFUTABLE, deriver.deriveStatus("LNC", paraconsistentNode, graphInfo));
        assertEquals(TheoremStatus.NON_PROVABLE_UNREFUTABLE, deriver.deriveStatus("LNC", commonNode, graphInfo));
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

        // Verify 2D duality: provability dimension swaps between LEM and LNC
        // Classical: both PROVABLE_UNREFUTABLE
        TheoremStatus lemClassical = deriver.deriveStatus("LEM", classicalNode, graphInfo);
        TheoremStatus lncClassical = deriver.deriveStatus("LNC", classicalNode, graphInfo);
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, lemClassical);
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, lncClassical);

        // Intuitionistic: LEM=NON_PROVABLE_UNREFUTABLE, LNC=PROVABLE_UNREFUTABLE (provability opposite)
        TheoremStatus lemIntuitionistic = deriver.deriveStatus("LEM", intuitionisticNode, graphInfo);
        TheoremStatus lncIntuitionistic = deriver.deriveStatus("LNC", intuitionisticNode, graphInfo);
        assertEquals(TheoremStatus.NON_PROVABLE_UNREFUTABLE, lemIntuitionistic);
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, lncIntuitionistic);
        // Note: these are provability duals (opposite in provability dimension)
        assertTrue(lemIntuitionistic.isNonProvable());
        assertTrue(lncIntuitionistic.isProvable());

        // Paraconsistent: LEM=PROVABLE_REFUTABLE, LNC=NON_PROVABLE_REFUTABLE (provability opposite)
        TheoremStatus lemParaconsistent = deriver.deriveStatus("LEM", paraconsistentNode, graphInfo);
        TheoremStatus lncParaconsistent = deriver.deriveStatus("LNC", paraconsistentNode, graphInfo);
        assertEquals(TheoremStatus.PROVABLE_REFUTABLE, lemParaconsistent);
        assertEquals(TheoremStatus.NON_PROVABLE_REFUTABLE, lncParaconsistent);
        // Both refutable, but provability opposite
        assertTrue(lemParaconsistent.isProvable());
        assertTrue(lncParaconsistent.isNonProvable());

        // Common: both NON_PROVABLE_UNREFUTABLE (intersection, most restrictive)
        TheoremStatus lemCommon = deriver.deriveStatus("LEM", commonNode, graphInfo);
        TheoremStatus lncCommon = deriver.deriveStatus("LNC", commonNode, graphInfo);
        assertEquals(TheoremStatus.NON_PROVABLE_UNREFUTABLE, lemCommon);
        assertEquals(TheoremStatus.NON_PROVABLE_UNREFUTABLE, lncCommon);
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

        // Double negation introduction should be provable in all logics (constructive)
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, deriver.deriveStatus("DoubleNegationIntroduction", classicalNode, graphInfo));
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, deriver.deriveStatus("DoubleNegationIntroduction", intuitionisticNode, graphInfo));
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, deriver.deriveStatus("DoubleNegationIntroduction", paraconsistentNode, graphInfo));
        assertEquals(TheoremStatus.PROVABLE_UNREFUTABLE, deriver.deriveStatus("DoubleNegationIntroduction", commonNode, graphInfo));
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
