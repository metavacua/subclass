package org.subclass.logic.proof.check;

import org.junit.jupiter.api.Test;
import org.subclass.examples.executable.LEMProofs;
import org.subclass.examples.executable.LNCProofs;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.rules.InferenceRules;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProofWalkerTest {

    private static final Formula A = new Formula.Atom("A");

    @Test
    void walkVisitsLeavesFirst() {
        Proof<Many, Many> ax = InferenceRules.axiom(A);
        Proof<Many, Many> notR = InferenceRules.notRight(ax);
        Proof<Many, Many> orR = InferenceRules.orRight(notR);

        List<String> order = new ArrayList<>();
        ProofWalker.walk(orR, node -> order.add(node.ruleName()));

        assertEquals(List.of("Axiom", "NotRight", "OrRight"), order);
    }

    @Test
    void checkClassicalLEMIsOk() {
        ProofWalker.CheckReport r = ProofWalker.check(LEMProofs.classical());
        assertTrue(r.isOk(), "Violations: " + r.violations());
    }

    @Test
    void checkClassicalLNCIsOk() {
        ProofWalker.CheckReport r = ProofWalker.check(LNCProofs.classical());
        assertTrue(r.isOk(), "Violations: " + r.violations());
    }

    @Test
    void checkParaconsistentLEMIsOk() {
        ProofWalker.CheckReport r = ProofWalker.check(LEMProofs.paraconsistent());
        assertTrue(r.isOk(), "Violations: " + r.violations());
    }

    @Test
    void checkIntuitionisticLNCIsOk() {
        ProofWalker.CheckReport r = ProofWalker.check(LNCProofs.intuitionistic());
        assertTrue(r.isOk(), "Violations: " + r.violations());
    }

    @Test
    void checkDetectsPremiseCountMismatch() {
        Proof<Many, Many> ax = InferenceRules.axiom(A);
        ProofNode<Many, Many> liar = new LyingNode<>(ax);
        ProofWalker.CheckReport r = ProofWalker.check(liar);
        assertTrue(r.violations().stream().anyMatch(v -> v.contains("premiseCount")));
    }

    @org.subclass.annotation.RuleSpec(
        name = "LyingNode", connective = "", side = "right", premiseCount = 42
    )
    public static record LyingNode<L extends org.subclass.logic.proof.typed.Cardinality,
                                    R extends org.subclass.logic.proof.typed.Cardinality>(
            Proof<L, R> premise)
        implements ProofNode<L, R> {

        @Override public org.subclass.logic.proof.typed.Sequent<L, R> conclusion() {
            return premise.conclusion();
        }
        @Override public String ruleName() { return "LyingNode"; }
        @Override public List<Proof<?, ?>> premises() { return List.of(premise); }
    }
}
