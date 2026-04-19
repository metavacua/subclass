package org.subclass.logic.rules.structural;

import org.junit.jupiter.api.Test;
import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;
import org.subclass.logic.rules.InferenceRules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StructuralRulesTest {

    private static final Formula A = new Formula.Atom("A");
    private static final Formula B = new Formula.Atom("B");

    private Proof<Many, Many> identityOnA() {
        return InferenceRules.axiom(A);
    }

    @Test
    void weakeningLeftAddsFormulaToAntecedent() {
        Proof<Many, Many> premise = identityOnA();
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(B, A), List.of(A));
        WeakeningLeft<Many, Many> wl = new WeakeningLeft<>(premise, B, conclusion);

        assertEquals("WeakeningLeft", wl.ruleName());
        assertEquals(List.of(premise), wl.premises());
        assertEquals(List.of(B, A), wl.conclusion().antecedent());
        assertEquals(List.of(A), wl.conclusion().succedent());
    }

    @Test
    void weakeningRightAddsFormulaToSuccedent() {
        Proof<Many, Many> premise = identityOnA();
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(A), List.of(A, B));
        WeakeningRight<Many, Many> wr = new WeakeningRight<>(premise, B, conclusion);

        assertEquals("WeakeningRight", wr.ruleName());
        assertEquals(List.of(A, B), wr.conclusion().succedent());
    }

    @Test
    void contractionLeftRecordsPosition() {
        Proof<Many, Many> premise =
            new Axiomatic(new Sequent<>(List.of(A, A), List.of(A)));
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(A), List.of(A));
        ContractionLeft<Many, Many> cl = new ContractionLeft<>(premise, 0, conclusion);

        assertEquals(0, cl.position());
        assertEquals("ContractionLeft", cl.ruleName());
    }

    @Test
    void contractionRightRecordsPosition() {
        Proof<Many, Many> premise =
            new Axiomatic(new Sequent<>(List.of(A), List.of(A, A)));
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(A), List.of(A));
        ContractionRight<Many, Many> cr = new ContractionRight<>(premise, 1, conclusion);

        assertEquals(1, cr.position());
        assertEquals("ContractionRight", cr.ruleName());
    }

    @Test
    void adjacentExchangeLeftSwapsAntecedentPositions() {
        Proof<Many, Many> premise =
            new Axiomatic(new Sequent<>(List.of(A, B), List.of(A)));
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(B, A), List.of(A));
        AdjacentExchangeLeft<Many, Many> ex =
            new AdjacentExchangeLeft<>(premise, 0, conclusion);

        assertEquals(0, ex.position());
        assertEquals(List.of(B, A), ex.conclusion().antecedent());
    }

    @Test
    void adjacentExchangeLeftRejectsNegativePosition() {
        Proof<Many, Many> premise = identityOnA();
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(A), List.of(A));
        assertThrows(IllegalArgumentException.class,
            () -> new AdjacentExchangeLeft<>(premise, -1, conclusion));
    }

    @Test
    void classicalExchangeLeftCarriesTwoPositions() {
        Proof<Many, Many> premise =
            new Axiomatic(new Sequent<>(List.of(A, B, A), List.of(A)));
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(A, A, B), List.of(A));
        ClassicalExchangeLeft<Many, Many> ex =
            new ClassicalExchangeLeft<>(premise, 1, 2, conclusion);

        assertEquals(1, ex.i());
        assertEquals(2, ex.j());
    }

    @Test
    void classicalExchangeRightCarriesTwoPositions() {
        Proof<Many, Many> premise =
            new Axiomatic(new Sequent<>(List.of(A), List.of(A, B, A)));
        Sequent<Many, Many> conclusion = new Sequent<>(List.of(A), List.of(A, A, B));
        ClassicalExchangeRight<Many, Many> ex =
            new ClassicalExchangeRight<>(premise, 1, 2, conclusion);

        assertEquals(1, ex.i());
        assertEquals(2, ex.j());
    }

    @Test
    void everyStructuralRuleIsAnnotatedStructural() {
        Class<?>[] rules = {
            AdjacentExchangeLeft.class, AdjacentExchangeRight.class,
            ClassicalExchangeLeft.class, ClassicalExchangeRight.class,
            WeakeningLeft.class, WeakeningRight.class,
            ContractionLeft.class, ContractionRight.class
        };
        for (Class<?> rule : rules) {
            RuleSpec spec = rule.getAnnotation(RuleSpec.class);
            assertNotNull(spec, rule.getSimpleName() + " missing @RuleSpec");
            assertEquals("structural", spec.side(),
                rule.getSimpleName() + " side should be \"structural\"");
            assertEquals(1, spec.premiseCount(),
                rule.getSimpleName() + " should have exactly one premise");
        }
    }

    @Test
    void adjacentVsClassicalExchangeAreDistinctRules() {
        assertTrue(
            !AdjacentExchangeLeft.class.getAnnotation(RuleSpec.class).name()
                .equals(ClassicalExchangeLeft.class.getAnnotation(RuleSpec.class).name()),
            "adjacent and classical exchange are distinct structural rules"
        );
    }

    /**
     * Minimal stand-in premise for tests: a {@link ProofNode} with no sub-
     * premises, carrying a hand-constructed conclusion sequent.
     */
    private record Axiomatic(Sequent<Many, Many> conclusion)
            implements ProofNode<Many, Many> {
        @Override public String ruleName() { return "TestAxiomatic"; }
        @Override public List<Proof<?, ?>> premises() { return List.of(); }
    }
}
