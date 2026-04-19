package org.subclass.logic.proof.typed;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The empty sequent {@code ⊢} is excluded: it is the model of inconsistency
 * and triviality, not a well-formed sequent. These tests pin that invariant.
 *
 * <p>The three initial-object ("Common") sequent shapes {@code A ⊢ B},
 * {@code A ⊢}, and {@code ⊢ B} are all admissible.
 */
class SequentInvariantTest {

    private static final Formula A = new Formula.Atom("A");
    private static final Formula B = new Formula.Atom("B");

    @Test
    void emptySequentIsRejected() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new Sequent<Many, Many>(List.of(), List.of()));
        assertTrue(ex.getMessage().contains("empty sequent"),
            "error should mention that the empty sequent is excluded");
    }

    @Test
    void oneEntailsOneIsAdmitted() {
        Sequent<One, One> s = Sequent.common(A, B);
        assertEquals(List.of(A), s.antecedent());
        assertEquals(List.of(B), s.succedent());
        assertEquals("A ⊢ B", s.toString());
    }

    @Test
    void antiTheoremShapeIsAdmitted() {
        Sequent<One, One> s = Sequent.antiTheorem(A);
        assertEquals(List.of(A), s.antecedent());
        assertEquals(List.of(), s.succedent());
        assertEquals("A ⊢", s.toString());
    }

    @Test
    void refutationalTheoremShapeIsAdmitted() {
        Sequent<One, One> s = Sequent.refutationalTheorem(A);
        assertEquals(List.of(), s.antecedent());
        assertEquals(List.of(A), s.succedent());
        assertEquals("⊢ A", s.toString());
    }

    @Test
    void nonEmptySequentsAreAdmittedAtEveryCardinality() {
        new Sequent<Many, Many>(List.of(A), List.of());
        new Sequent<Many, Many>(List.of(), List.of(B));
        new Sequent<Many, Many>(List.of(A, B), List.of(A, B));
        // no exceptions means success
    }
}
