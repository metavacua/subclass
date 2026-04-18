package org.subclass.logic.rules;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleRegistryTest {

    @Test
    void serviceLoaderDiscoversAllPropositionalRules() {
        Set<String> names = RuleRegistry.instance().ruleNames();
        assertTrue(names.contains("Axiom"), "Axiom should be registered");
        assertTrue(names.contains("Cut"), "Cut should be registered");
        assertTrue(names.contains("AndLeft"));
        assertTrue(names.contains("AndRight"));
        assertTrue(names.contains("OrLeft"));
        assertTrue(names.contains("OrRight"));
        assertTrue(names.contains("ImpliesLeft"));
        assertTrue(names.contains("ImpliesRight"));
        assertTrue(names.contains("NotLeft"));
        assertTrue(names.contains("NotRight"));
    }

    @Test
    void axiomRuleSpecHasZeroPremises() {
        RuleDescriptor axiom = RuleRegistry.instance()
            .find("Axiom")
            .orElseThrow();
        assertEquals(0, axiom.ruleSpec().premiseCount());
        assertEquals("axiom", axiom.ruleSpec().side());
    }

    @Test
    void cutRuleSpecHasTwoPremises() {
        RuleDescriptor cut = RuleRegistry.instance()
            .find("Cut")
            .orElseThrow();
        assertEquals(2, cut.ruleSpec().premiseCount());
        assertEquals("cut", cut.ruleSpec().side());
    }

    @Test
    void connectiveRulesAdvertiseCorrectConnectiveAndSide() {
        assertEquals("∧", RuleRegistry.instance().find("AndRight").orElseThrow().ruleSpec().connective());
        assertEquals("right", RuleRegistry.instance().find("AndRight").orElseThrow().ruleSpec().side());
        assertEquals("∨", RuleRegistry.instance().find("OrLeft").orElseThrow().ruleSpec().connective());
        assertEquals("left", RuleRegistry.instance().find("OrLeft").orElseThrow().ruleSpec().side());
        assertEquals("→", RuleRegistry.instance().find("ImpliesRight").orElseThrow().ruleSpec().connective());
        assertEquals("¬", RuleRegistry.instance().find("NotLeft").orElseThrow().ruleSpec().connective());
    }
}
