package org.subclass.logic.sexpr;

import org.subclass.logic.proof.typed.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reflection-based serialization/deserialization for SubClass logical objects to/from S-expressions.
 *
 * Core principle: Java objects are canonical; S-expressions are faithful structural serializations.
 * No custom DSL—the format is determined entirely by Java's type structure.
 *
 * S-expression format for tagged objects:
 *   (: ClassName (field1 value1) (field2 value2) ...)
 *
 * Roundtrip property: parse(serialize(obj)) reconstructs structurally identical object.
 */
public final class SExpressionSerDe {
    private SExpressionSerDe() {}

    // ============================================================================
    // SERIALIZATION (Java → S-expression)
    // ============================================================================

    /**
     * Serialize a Formula to S-expression.
     */
    public static String serialize(Formula formula) {
        return serializeFormula(formula);
    }

    /**
     * Serialize a Sequent to S-expression.
     */
    public static <L extends Cardinality, R extends Cardinality> String serialize(Sequent<L, R> sequent) {
        return serializeSequent(sequent);
    }

    private static String serializeFormula(Formula formula) {
        if (formula instanceof Formula.Atom atom) {
            return "(: Atom (name \"" + escapeString(atom.name()) + "\"))";
        } else if (formula instanceof Formula.Not not) {
            return "(: Not (formula " + serializeFormula(not.formula()) + "))";
        } else if (formula instanceof Formula.Or or) {
            return "(: Or (left " + serializeFormula(or.left()) + ") (right " + serializeFormula(or.right()) + "))";
        } else if (formula instanceof Formula.And and) {
            return "(: And (left " + serializeFormula(and.left()) + ") (right " + serializeFormula(and.right()) + "))";
        } else if (formula instanceof Formula.Implies implies) {
            return "(: Implies (antecedent " + serializeFormula(implies.antecedent()) + ") (consequent " + serializeFormula(implies.consequent()) + "))";
        } else {
            throw new IllegalArgumentException("Unknown formula type: " + formula.getClass());
        }
    }

    private static <L extends Cardinality, R extends Cardinality> String serializeSequent(Sequent<L, R> sequent) {
        StringBuilder sb = new StringBuilder();
        sb.append("(: Sequent ");
        sb.append("(antecedent (");
        for (Formula f : sequent.antecedent()) {
            sb.append(serializeFormula(f)).append(" ");
        }
        sb.append(")) ");
        sb.append("(succedent (");
        for (Formula f : sequent.succedent()) {
            sb.append(serializeFormula(f)).append(" ");
        }
        sb.append(")))");
        return sb.toString();
    }

    private static String escapeString(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // ============================================================================
    // DESERIALIZATION (S-expression → Java)
    // ============================================================================

    /**
     * Parse an S-expression into a Formula.
     */
    public static Formula parseFormula(String sexpr) {
        SExprValue val = parseSExpr(sexpr.trim());
        return formulaFromSExprValue(val);
    }

    /**
     * Parse an S-expression into a classical Sequent (Sequent<Many, Many>).
     */
    public static Sequent<Many, Many> parseSequentClassical(String sexpr) {
        SExprValue val = parseSExpr(sexpr.trim());
        return sequentFromSExprValue(val);
    }

    private static SExprValue parseSExpr(String input) {
        Parser parser = new Parser(input);
        return parser.parseValue();
    }

    private static Formula formulaFromSExprValue(SExprValue val) {
        if (!(val instanceof SExprValue.SExprList list) || list.elements.isEmpty()) {
            throw new IllegalArgumentException("Expected list for formula");
        }

        if (!(list.elements.getFirst() instanceof SExprValue.SExprSymbol tagSym) || !tagSym.value.equals(":")) {
            throw new IllegalArgumentException("Expected tagged value with ':'");
        }

        if (list.elements.size() < 2) {
            throw new IllegalArgumentException("Tagged value needs type name");
        }

        if (!(list.elements.get(1) instanceof SExprValue.SExprSymbol typeSym)) {
            throw new IllegalArgumentException("Expected type name symbol");
        }

        return switch (typeSym.value) {
            case "Atom" -> parseAtom(list);
            case "Not" -> parseNot(list);
            case "Or" -> parseOr(list);
            case "And" -> parseAnd(list);
            case "Implies" -> parseImplies(list);
            default -> throw new IllegalArgumentException("Unknown formula type: " + typeSym.value);
        };
    }

    private static Formula.Atom parseAtom(SExprValue.SExprList list) {
        java.lang.String name = extractStringField(list, "name");
        return new Formula.Atom(name);
    }

    private static Formula.Not parseNot(SExprValue.SExprList list) {
        Formula inner = extractFormulaField(list, "formula");
        return new Formula.Not(inner);
    }

    private static Formula.Or parseOr(SExprValue.SExprList list) {
        Formula left = extractFormulaField(list, "left");
        Formula right = extractFormulaField(list, "right");
        return new Formula.Or(left, right);
    }

    private static Formula.And parseAnd(SExprValue.SExprList list) {
        Formula left = extractFormulaField(list, "left");
        Formula right = extractFormulaField(list, "right");
        return new Formula.And(left, right);
    }

    private static Formula.Implies parseImplies(SExprValue.SExprList list) {
        Formula ant = extractFormulaField(list, "antecedent");
        Formula cons = extractFormulaField(list, "consequent");
        return new Formula.Implies(ant, cons);
    }

    private static <L extends Cardinality, R extends Cardinality> Sequent<L, R> sequentFromSExprValue(SExprValue val) {
        if (!(val instanceof SExprValue.SExprList list) || list.elements.isEmpty()) {
            throw new IllegalArgumentException("Expected list for sequent");
        }

        if (!(list.elements.getFirst() instanceof SExprValue.SExprSymbol tagSym) || !tagSym.value.equals(":")) {
            throw new IllegalArgumentException("Expected tagged value");
        }

        if (list.elements.size() < 2 || !(list.elements.get(1) instanceof SExprValue.SExprSymbol typeSym) || !typeSym.value.equals("Sequent")) {
            throw new IllegalArgumentException("Expected Sequent type");
        }

        List<Formula> antecedent = extractFormulaListField(list, "antecedent");
        List<Formula> succedent = extractFormulaListField(list, "succedent");

        return new Sequent<>(antecedent, succedent);
    }

    private static java.lang.String extractStringField(SExprValue.SExprList list, java.lang.String fieldName) {
        for (int i = 2; i < list.elements.size(); i++) {
            SExprValue elem = list.elements.get(i);
            if (elem instanceof SExprValue.SExprList fieldList && fieldList.elements.size() >= 2) {
                if (fieldList.elements.getFirst() instanceof SExprValue.SExprSymbol fieldSym && fieldSym.value.equals(fieldName)) {
                    if (fieldList.elements.get(1) instanceof SExprValue.SExprString str) {
                        return str.value;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }

    private static Formula extractFormulaField(SExprValue.SExprList list, java.lang.String fieldName) {
        for (int i = 2; i < list.elements.size(); i++) {
            SExprValue elem = list.elements.get(i);
            if (elem instanceof SExprValue.SExprList fieldList && fieldList.elements.size() >= 2) {
                if (fieldList.elements.getFirst() instanceof SExprValue.SExprSymbol fieldSym && fieldSym.value.equals(fieldName)) {
                    return formulaFromSExprValue(fieldList.elements.get(1));
                }
            }
        }
        throw new IllegalArgumentException("Formula field not found: " + fieldName);
    }

    private static List<Formula> extractFormulaListField(SExprValue.SExprList list, java.lang.String fieldName) {
        for (int i = 2; i < list.elements.size(); i++) {
            SExprValue elem = list.elements.get(i);
            if (elem instanceof SExprValue.SExprList fieldList && fieldList.elements.size() >= 2) {
                if (fieldList.elements.getFirst() instanceof SExprValue.SExprSymbol fieldSym && fieldSym.value.equals(fieldName)) {
                    if (fieldList.elements.get(1) instanceof SExprValue.SExprList formulaList) {
                        List<Formula> result = new ArrayList<>();
                        for (SExprValue formVal : formulaList.elements) {
                            result.add(formulaFromSExprValue(formVal));
                        }
                        return result;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Formula list field not found: " + fieldName);
    }

    // ============================================================================
    // S-EXPRESSION PARSING
    // ============================================================================

    sealed interface SExprValue {
        record SExprList(java.util.List<SExprValue> elements) implements SExprValue {}
        record SExprSymbol(java.lang.String value) implements SExprValue {}
        record SExprString(java.lang.String value) implements SExprValue {}
    }

    static class Parser {
        private final String input;
        private int pos = 0;

        Parser(String input) {
            this.input = input;
        }

        SExprValue parseValue() {
            skipWhitespace();
            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unexpected end of input");
            }

            char ch = input.charAt(pos);
            if (ch == '(') {
                return parseList();
            } else if (ch == '"') {
                return parseString();
            } else {
                return parseSymbol();
            }
        }

        private SExprValue.SExprList parseList() {
            if (input.charAt(pos) != '(') {
                throw new IllegalArgumentException("Expected '('");
            }
            pos++;

            List<SExprValue> elements = new ArrayList<>();
            skipWhitespace();

            while (pos < input.length() && input.charAt(pos) != ')') {
                elements.add(parseValue());
                skipWhitespace();
            }

            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unclosed list");
            }
            pos++; // skip ')'

            return new SExprValue.SExprList(elements);
        }

        private SExprValue.SExprString parseString() {
            if (input.charAt(pos) != '"') {
                throw new IllegalArgumentException("Expected '\"'");
            }
            pos++;

            StringBuilder sb = new StringBuilder();
            while (pos < input.length() && input.charAt(pos) != '"') {
                if (input.charAt(pos) == '\\' && pos + 1 < input.length()) {
                    pos++;
                    char escaped = input.charAt(pos);
                    if (escaped == 'n') {
                        sb.append('\n');
                    } else if (escaped == '\\') {
                        sb.append('\\');
                    } else if (escaped == '"') {
                        sb.append('"');
                    } else {
                        sb.append(escaped);
                    }
                } else {
                    sb.append(input.charAt(pos));
                }
                pos++;
            }

            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unclosed string");
            }
            pos++; // skip closing '"'

            return new SExprValue.SExprString(sb.toString());
        }

        private SExprValue.SExprSymbol parseSymbol() {
            int start = pos;
            while (pos < input.length() && !Character.isWhitespace(input.charAt(pos)) && input.charAt(pos) != '(' && input.charAt(pos) != ')') {
                pos++;
            }
            if (start == pos) {
                throw new IllegalArgumentException("Empty symbol");
            }
            return new SExprValue.SExprSymbol(input.substring(start, pos));
        }

        private void skipWhitespace() {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }
        }
    }
}
