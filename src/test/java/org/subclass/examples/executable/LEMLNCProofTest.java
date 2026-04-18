package org.subclass.examples.executable;

import org.junit.jupiter.api.Test;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.One;
import org.subclass.logic.proof.typed.Proof;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LEMLNCProofTest {

    private static final Formula A = new Formula.Atom("A");

    @Test
    void classicalLEMProducesAOrNotA() {
        Proof<Many, Many> p = LEMProofs.classical();
        assertEquals(List.of(), p.conclusion().antecedent());
        assertEquals(List.of(new Formula.Or(A, new Formula.Not(A))), p.conclusion().succedent());
    }

    @Test
    void paraconsistentLEMProducesAOrNotA() {
        Proof<One, Many> p = LEMProofs.paraconsistent();
        assertEquals(List.of(new Formula.Or(A, new Formula.Not(A))), p.conclusion().succedent());
    }

    @Test
    void classicalLNCProducesNotAAndNotA() {
        Proof<Many, Many> p = LNCProofs.classical();
        Formula expected = new Formula.Not(new Formula.And(A, new Formula.Not(A)));
        assertEquals(List.of(expected), p.conclusion().succedent());
    }

    @Test
    void intuitionisticLNCProducesNotAAndNotA() {
        Proof<Many, One> p = LNCProofs.intuitionistic();
        Formula expected = new Formula.Not(new Formula.And(A, new Formula.Not(A)));
        assertEquals(List.of(expected), p.conclusion().succedent());
    }
}
