package org.subclass.logic.sexpr;

import org.junit.jupiter.api.Test;
import org.subclass.logic.proof.typed.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for S-expression serialization/deserialization.
 *
 * Tests verify:
 * 1. Roundtrip property: parse(serialize(obj)) reconstructs structurally identical objects
 * 2. Formula serialization: all connectives and atoms
 * 3. Sequent serialization: with various cardinalities
 * 4. Error handling: malformed S-expressions
 */
public class SExpressionSerDeTest {

    // ============================================================================
    // FORMULA SERIALIZATION TESTS
    // ============================================================================

    @Test
    void testSerializeAtom() {
        Formula atom = new Formula.Atom("a");
        String sexpr = SExpressionSerDe.serialize(atom);
        assertEquals("(: Atom (name \"a\"))", sexpr);
    }

    @Test
    void testSerializeAtomWithSpecialChars() {
        Formula atom = new Formula.Atom("a_1");
        String sexpr = SExpressionSerDe.serialize(atom);
        assertEquals("(: Atom (name \"a_1\"))", sexpr);
    }

    @Test
    void testSerializeAtomEscaping() {
        Formula atom = new Formula.Atom("a\"b");
        String sexpr = SExpressionSerDe.serialize(atom);
        assertEquals("(: Atom (name \"a\\\"b\"))", sexpr);
    }

    @Test
    void testSerializeNot() {
        Formula not = new Formula.Not(new Formula.Atom("a"));
        String sexpr = SExpressionSerDe.serialize(not);
        assertEquals("(: Not (formula (: Atom (name \"a\"))))", sexpr);
    }

    @Test
    void testSerializeOr() {
        Formula or = new Formula.Or(new Formula.Atom("a"), new Formula.Atom("b"));
        String sexpr = SExpressionSerDe.serialize(or);
        assertEquals("(: Or (left (: Atom (name \"a\"))) (right (: Atom (name \"b\"))))", sexpr);
    }

    @Test
    void testSerializeAnd() {
        Formula and = new Formula.And(new Formula.Atom("a"), new Formula.Atom("b"));
        String sexpr = SExpressionSerDe.serialize(and);
        assertEquals("(: And (left (: Atom (name \"a\"))) (right (: Atom (name \"b\"))))", sexpr);
    }

    @Test
    void testSerializeImplies() {
        Formula implies = new Formula.Implies(new Formula.Atom("a"), new Formula.Atom("b"));
        String sexpr = SExpressionSerDe.serialize(implies);
        assertEquals("(: Implies (antecedent (: Atom (name \"a\"))) (consequent (: Atom (name \"b\"))))", sexpr);
    }

    @Test
    void testSerializeComplexFormula() {
        Formula formula = new Formula.Or(
            new Formula.Atom("a"),
            new Formula.Not(new Formula.Atom("a"))
        );
        String sexpr = SExpressionSerDe.serialize(formula);
        assertNotNull(sexpr);
        assertTrue(sexpr.contains("Or"));
        assertTrue(sexpr.contains("Atom"));
        assertTrue(sexpr.contains("Not"));
    }

    // ============================================================================
    // FORMULA DESERIALIZATION TESTS
    // ============================================================================

    @Test
    void testParseAtom() {
        Formula atom = SExpressionSerDe.parseFormula("(: Atom (name \"x\"))");
        assertInstanceOf(Formula.Atom.class, atom);
        assertEquals("x", ((Formula.Atom) atom).name());
    }

    @Test
    void testParseNot() {
        Formula not = SExpressionSerDe.parseFormula("(: Not (formula (: Atom (name \"a\"))))");
        assertInstanceOf(Formula.Not.class, not);
        Formula inner = ((Formula.Not) not).formula();
        assertInstanceOf(Formula.Atom.class, inner);
        assertEquals("a", ((Formula.Atom) inner).name());
    }

    @Test
    void testParseOr() {
        Formula or = SExpressionSerDe.parseFormula("(: Or (left (: Atom (name \"a\"))) (right (: Atom (name \"b\"))))");
        assertInstanceOf(Formula.Or.class, or);
        Formula.Or orFormula = (Formula.Or) or;
        assertInstanceOf(Formula.Atom.class, orFormula.left());
        assertInstanceOf(Formula.Atom.class, orFormula.right());
    }

    @Test
    void testParseAnd() {
        Formula and = SExpressionSerDe.parseFormula("(: And (left (: Atom (name \"a\"))) (right (: Atom (name \"b\"))))");
        assertInstanceOf(Formula.And.class, and);
    }

    @Test
    void testParseImplies() {
        Formula implies = SExpressionSerDe.parseFormula("(: Implies (antecedent (: Atom (name \"a\"))) (consequent (: Atom (name \"b\"))))");
        assertInstanceOf(Formula.Implies.class, implies);
    }

    // ============================================================================
    // ROUNDTRIP TESTS: Serialize → Parse → Compare
    // ============================================================================

    @Test
    void testRoundtripAtom() {
        Formula original = new Formula.Atom("p");
        String serialized = SExpressionSerDe.serialize(original);
        Formula parsed = SExpressionSerDe.parseFormula(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testRoundtripNot() {
        Formula original = new Formula.Not(new Formula.Atom("a"));
        String serialized = SExpressionSerDe.serialize(original);
        Formula parsed = SExpressionSerDe.parseFormula(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testRoundtripOr() {
        Formula original = new Formula.Or(new Formula.Atom("a"), new Formula.Atom("b"));
        String serialized = SExpressionSerDe.serialize(original);
        Formula parsed = SExpressionSerDe.parseFormula(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testRoundtripAnd() {
        Formula original = new Formula.And(new Formula.Atom("a"), new Formula.Atom("b"));
        String serialized = SExpressionSerDe.serialize(original);
        Formula parsed = SExpressionSerDe.parseFormula(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testRoundtripImplies() {
        Formula original = new Formula.Implies(new Formula.Atom("a"), new Formula.Atom("b"));
        String serialized = SExpressionSerDe.serialize(original);
        Formula parsed = SExpressionSerDe.parseFormula(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testRoundtripComplexFormula() {
        Formula original = new Formula.Or(
            new Formula.And(new Formula.Atom("a"), new Formula.Atom("b")),
            new Formula.Not(new Formula.Atom("c"))
        );
        String serialized = SExpressionSerDe.serialize(original);
        Formula parsed = SExpressionSerDe.parseFormula(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testRoundtripDeeplyNestedFormula() {
        Formula original = new Formula.Not(
            new Formula.Or(
                new Formula.And(
                    new Formula.Implies(new Formula.Atom("a"), new Formula.Atom("b")),
                    new Formula.Atom("c")
                ),
                new Formula.Atom("d")
            )
        );
        String serialized = SExpressionSerDe.serialize(original);
        Formula parsed = SExpressionSerDe.parseFormula(serialized);
        assertEquals(original, parsed);
    }

    // ============================================================================
    // SEQUENT SERIALIZATION TESTS
    // ============================================================================

    @Test
    void testSerializeEmptySequent() {
        Sequent<Many, Many> seq = Sequent.classical(List.of(), List.of());
        String sexpr = SExpressionSerDe.serialize(seq);
        assertTrue(sexpr.contains("Sequent"));
        assertTrue(sexpr.contains("antecedent"));
        assertTrue(sexpr.contains("succedent"));
    }

    @Test
    void testSerializeClassicalSequent() {
        Sequent<Many, Many> seq = Sequent.classical(
            List.of(new Formula.Atom("a")),
            List.of(new Formula.Atom("b"))
        );
        String sexpr = SExpressionSerDe.serialize(seq);
        assertTrue(sexpr.contains("Sequent"));
        assertTrue(sexpr.contains("Atom"));
    }

    @Test
    void testSerializeMultiFormulaSequent() {
        Sequent<Many, Many> seq = Sequent.classical(
            List.of(new Formula.Atom("a"), new Formula.Atom("b")),
            List.of(new Formula.Or(new Formula.Atom("c"), new Formula.Atom("d")))
        );
        String sexpr = SExpressionSerDe.serialize(seq);
        assertNotNull(sexpr);
        assertTrue(sexpr.contains("Or"));
    }

    // ============================================================================
    // SEQUENT DESERIALIZATION TESTS
    // ============================================================================

    @Test
    void testParseEmptySequent() {
        Sequent<Many, Many> seq = SExpressionSerDe.parseSequentClassical(
            "(: Sequent (antecedent ()) (succedent ()))"
        );
        assertEquals(0, seq.antecedent().size());
        assertEquals(0, seq.succedent().size());
    }

    @Test
    void testParseSimpleSequent() {
        Sequent<Many, Many> seq = SExpressionSerDe.parseSequentClassical(
            "(: Sequent (antecedent ((: Atom (name \"a\")))) (succedent ((: Atom (name \"b\")))))"
        );
        assertEquals(1, seq.antecedent().size());
        assertEquals(1, seq.succedent().size());
    }

    @Test
    void testParseMultiFormulaSequent() {
        Sequent<Many, Many> seq = SExpressionSerDe.parseSequentClassical(
            "(: Sequent (antecedent ((: Atom (name \"a\")) (: Atom (name \"b\")))) (succedent ((: Or (left (: Atom (name \"c\"))) (right (: Atom (name \"d\")))))))"
        );
        assertEquals(2, seq.antecedent().size());
        assertEquals(1, seq.succedent().size());
    }

    // ============================================================================
    // SEQUENT ROUNDTRIP TESTS
    // ============================================================================

    @Test
    void testSequentRoundtripEmpty() {
        Sequent<Many, Many> original = Sequent.classical(List.of(), List.of());
        String serialized = SExpressionSerDe.serialize(original);
        Sequent<Many, Many> parsed = SExpressionSerDe.parseSequentClassical(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testSequentRoundtripSimple() {
        Sequent<Many, Many> original = Sequent.classical(
            List.of(new Formula.Atom("a")),
            List.of(new Formula.Atom("b"))
        );
        String serialized = SExpressionSerDe.serialize(original);
        Sequent<Many, Many> parsed = SExpressionSerDe.parseSequentClassical(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testSequentRoundtripComplex() {
        Sequent<Many, Many> original = Sequent.classical(
            List.of(
                new Formula.Atom("a"),
                new Formula.And(new Formula.Atom("b"), new Formula.Atom("c"))
            ),
            List.of(
                new Formula.Or(new Formula.Atom("d"), new Formula.Atom("e")),
                new Formula.Not(new Formula.Atom("f"))
            )
        );
        String serialized = SExpressionSerDe.serialize(original);
        Sequent<Many, Many> parsed = SExpressionSerDe.parseSequentClassical(serialized);
        assertEquals(original, parsed);
    }

    @Test
    void testSequentRoundtripLEM() {
        Sequent<Many, Many> original = Sequent.classical(
            List.of(),
            List.of(new Formula.Or(new Formula.Atom("a"), new Formula.Not(new Formula.Atom("a"))))
        );
        String serialized = SExpressionSerDe.serialize(original);
        Sequent<Many, Many> parsed = SExpressionSerDe.parseSequentClassical(serialized);
        assertEquals(original, parsed);
    }

    // ============================================================================
    // ERROR HANDLING TESTS
    // ============================================================================

    @Test
    void testParseEmptyInput() {
        assertThrows(IllegalArgumentException.class, () ->
            SExpressionSerDe.parseFormula("")
        );
    }

    @Test
    void testParseUnclosedList() {
        assertThrows(IllegalArgumentException.class, () ->
            SExpressionSerDe.parseFormula("(: Atom (name \"a\")")
        );
    }

    @Test
    void testParseUnexpectedTag() {
        assertThrows(IllegalArgumentException.class, () ->
            SExpressionSerDe.parseFormula("(: UnknownType (name \"a\"))")
        );
    }

    @Test
    void testParseMissingField() {
        assertThrows(IllegalArgumentException.class, () ->
            SExpressionSerDe.parseFormula("(: Atom (other \"a\"))")
        );
    }

    @Test
    void testParseInvalidUnclosedString() {
        assertThrows(IllegalArgumentException.class, () ->
            SExpressionSerDe.parseFormula("(: Atom (name \"a))")
        );
    }

    @Test
    void testParseNestedListAsAtomName() {
        assertThrows(IllegalArgumentException.class, () ->
            SExpressionSerDe.parseFormula("(: Atom (name (invalid)))")
        );
    }
}
