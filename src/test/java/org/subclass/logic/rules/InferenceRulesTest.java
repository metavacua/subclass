package org.subclass.logic.rules;

import org.junit.jupiter.api.Test;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.One;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.rules.axiom.Axiom;
import org.subclass.logic.rules.conjunction.AndLeft;
import org.subclass.logic.rules.conjunction.AndRight;
import org.subclass.logic.rules.disjunction.OrRight;
import org.subclass.logic.rules.implication.ImpliesRight;
import org.subclass.logic.rules.negation.NotLeft;
import org.subclass.logic.rules.negation.NotRight;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InferenceRulesTest {

    private static final Formula A = new Formula.Atom("A");
    private static final Formula B = new Formula.Atom("B");

    @Test
    void axiomConstructsIdentitySequent() {
        Proof<Many, Many> p = InferenceRules.axiom(A);
        assertInstanceOf(Axiom.class, p);
        assertEquals(List.of(A), p.conclusion().antecedent());
        assertEquals(List.of(A), p.conclusion().succedent());
        assertEquals(List.of(), ((ProofNode<?, ?>) p).premises());
        assertEquals("Axiom", ((ProofNode<?, ?>) p).ruleName());
    }

    @Test
    void notRightMovesAntecedentToSuccedentAsNegation() {
        Proof<Many, Many> ax = InferenceRules.axiom(A);
        Proof<Many, Many> notR = InferenceRules.notRight(ax);
        assertInstanceOf(NotRight.class, notR);
        assertEquals(List.of(), notR.conclusion().antecedent());
        assertEquals(List.of(A, new Formula.Not(A)), notR.conclusion().succedent());
    }

    @Test
    void notLeftMovesSuccedentToAntecedentAsNegation() {
        Proof<Many, Many> ax = InferenceRules.axiom(A);
        Proof<Many, Many> notL = InferenceRules.notLeft(ax);
        assertInstanceOf(NotLeft.class, notL);
        assertEquals(List.of(A, new Formula.Not(A)), notL.conclusion().antecedent());
        assertEquals(List.of(), notL.conclusion().succedent());
    }

    @Test
    void orRightCombinesLastTwoSuccedentFormulas() {
        Proof<Many, Many> ax = InferenceRules.axiom(A);
        Proof<Many, Many> notR = InferenceRules.notRight(ax);
        Proof<Many, Many> orR = InferenceRules.orRight(notR);
        assertInstanceOf(OrRight.class, orR);
        assertEquals(
            List.of(new Formula.Or(A, new Formula.Not(A))),
            orR.conclusion().succedent());
    }

    @Test
    void andLeftCombinesLastTwoAntecedentFormulas() {
        Proof<Many, One> ax = InferenceRules.axiom(A);
        Proof<Many, One> nL = InferenceRules.notLeft(ax);
        Proof<Many, One> aL = InferenceRules.andLeft(nL);
        assertInstanceOf(AndLeft.class, aL);
        assertEquals(
            List.of(new Formula.And(A, new Formula.Not(A))),
            aL.conclusion().antecedent());
    }

    @Test
    void andRightCombinesTwoPremiseSuccedents() {
        Proof<Many, One> axA = InferenceRules.axiom(A);
        Proof<Many, One> axB = InferenceRules.axiom(B);
        Proof<Many, One> aR = InferenceRules.andRight(axA, axB);
        assertInstanceOf(AndRight.class, aR);
        assertEquals(List.of(new Formula.And(A, B)), aR.conclusion().succedent());
        assertEquals(2, ((ProofNode<?, ?>) aR).premises().size());
    }

    @Test
    void impliesRightBuildsImplicationFromLastAntecedentAndLastSuccedent() {
        Proof<Many, One> ax = InferenceRules.axiom(A);
        Proof<Many, One> iR = InferenceRules.impliesRight(ax);
        assertInstanceOf(ImpliesRight.class, iR);
        assertEquals(List.of(new Formula.Implies(A, A)), iR.conclusion().succedent());
        assertTrue(iR.conclusion().antecedent().isEmpty());
    }
}
