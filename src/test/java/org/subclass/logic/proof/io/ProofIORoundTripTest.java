package org.subclass.logic.proof.io;

import org.junit.jupiter.api.Test;
import org.subclass.examples.executable.LEMProofs;
import org.subclass.examples.executable.LNCProofs;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.One;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.rules.InferenceRules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProofIORoundTripTest {

    private static final Formula A = new Formula.Atom("A");
    private static final Formula B = new Formula.Atom("B");

    @Test
    void jsonRoundTripAxiom() {
        Proof<Many, Many> p = InferenceRules.axiom(A);
        String json = ProofIO.writeJson(p);
        Proof<?, ?> back = ProofIO.readJson(json);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void xmlRoundTripAxiom() {
        Proof<Many, Many> p = InferenceRules.axiom(A);
        String xml = ProofIO.writeXml(p);
        Proof<?, ?> back = ProofIO.readXml(xml);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void sexprRoundTripAxiom() {
        Proof<Many, Many> p = InferenceRules.axiom(A);
        String s = ProofIO.writeSExpr(p);
        Proof<?, ?> back = ProofIO.readSExpr(s);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void jsonRoundTripClassicalLEM() {
        Proof<Many, Many> p = LEMProofs.classical();
        String json = ProofIO.writeJson(p);
        Proof<?, ?> back = ProofIO.readJson(json);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void xmlRoundTripClassicalLEM() {
        Proof<Many, Many> p = LEMProofs.classical();
        String xml = ProofIO.writeXml(p);
        assertNotNull(xml);
        Proof<?, ?> back = ProofIO.readXml(xml);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void sexprRoundTripClassicalLEM() {
        Proof<Many, Many> p = LEMProofs.classical();
        String s = ProofIO.writeSExpr(p);
        Proof<?, ?> back = ProofIO.readSExpr(s);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void jsonRoundTripClassicalLNC() {
        Proof<Many, Many> p = LNCProofs.classical();
        String json = ProofIO.writeJson(p);
        Proof<?, ?> back = ProofIO.readJson(json);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void sexprRoundTripClassicalLNC() {
        Proof<Many, Many> p = LNCProofs.classical();
        String s = ProofIO.writeSExpr(p);
        Proof<?, ?> back = ProofIO.readSExpr(s);
        assertEquals(p.conclusion(), back.conclusion());
    }

    @Test
    void jsonRoundTripAndRight() {
        Proof<Many, One> axA = InferenceRules.axiom(A);
        Proof<Many, One> axB = InferenceRules.axiom(B);
        Proof<Many, One> aR = InferenceRules.andRight(axA, axB);
        String json = ProofIO.writeJson(aR);
        Proof<?, ?> back = ProofIO.readJson(json);
        assertEquals(aR.conclusion(), back.conclusion());
    }

    @Test
    void xmlRoundTripAndRight() {
        Proof<Many, One> axA = InferenceRules.axiom(A);
        Proof<Many, One> axB = InferenceRules.axiom(B);
        Proof<Many, One> aR = InferenceRules.andRight(axA, axB);
        String xml = ProofIO.writeXml(aR);
        Proof<?, ?> back = ProofIO.readXml(xml);
        assertEquals(aR.conclusion(), back.conclusion());
    }

    @Test
    void sexprRoundTripAndRight() {
        Proof<Many, One> axA = InferenceRules.axiom(A);
        Proof<Many, One> axB = InferenceRules.axiom(B);
        Proof<Many, One> aR = InferenceRules.andRight(axA, axB);
        String s = ProofIO.writeSExpr(aR);
        Proof<?, ?> back = ProofIO.readSExpr(s);
        assertEquals(aR.conclusion(), back.conclusion());
    }
}
