package org.subclass.logic.proof.io;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.xml.bind.annotation.XmlSeeAlso;

import java.util.Objects;

/**
 * Serialization-friendly mirror of {@link org.subclass.logic.proof.typed.Formula}.
 *
 * Polymorphic over the five propositional-core subtypes (atom, negation,
 * conjunction, disjunction, implication). Jackson dispatches on the
 * {@code "type"} JSON property; JAXB dispatches via {@link XmlSeeAlso}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = FormulaDto.Atom.class, name = "Atom"),
    @JsonSubTypes.Type(value = FormulaDto.Not.class, name = "Not"),
    @JsonSubTypes.Type(value = FormulaDto.And.class, name = "And"),
    @JsonSubTypes.Type(value = FormulaDto.Or.class, name = "Or"),
    @JsonSubTypes.Type(value = FormulaDto.Implies.class, name = "Implies")
})
@XmlSeeAlso({
    FormulaDto.Atom.class,
    FormulaDto.Not.class,
    FormulaDto.And.class,
    FormulaDto.Or.class,
    FormulaDto.Implies.class
})
public abstract sealed class FormulaDto permits
    FormulaDto.Atom, FormulaDto.Not, FormulaDto.And, FormulaDto.Or, FormulaDto.Implies {

    public static final class Atom extends FormulaDto {
        public String name;
        public Atom() {}
        public Atom(String name) { this.name = name; }
        @Override public boolean equals(Object o) {
            return o instanceof Atom a && Objects.equals(name, a.name);
        }
        @Override public int hashCode() { return Objects.hash(name); }
    }

    public static final class Not extends FormulaDto {
        public FormulaDto formula;
        public Not() {}
        public Not(FormulaDto f) { this.formula = f; }
        @Override public boolean equals(Object o) {
            return o instanceof Not n && Objects.equals(formula, n.formula);
        }
        @Override public int hashCode() { return Objects.hash(formula); }
    }

    public static final class And extends FormulaDto {
        public FormulaDto left;
        public FormulaDto right;
        public And() {}
        public And(FormulaDto l, FormulaDto r) { this.left = l; this.right = r; }
        @Override public boolean equals(Object o) {
            return o instanceof And a && Objects.equals(left, a.left) && Objects.equals(right, a.right);
        }
        @Override public int hashCode() { return Objects.hash(left, right); }
    }

    public static final class Or extends FormulaDto {
        public FormulaDto left;
        public FormulaDto right;
        public Or() {}
        public Or(FormulaDto l, FormulaDto r) { this.left = l; this.right = r; }
        @Override public boolean equals(Object o) {
            return o instanceof Or x && Objects.equals(left, x.left) && Objects.equals(right, x.right);
        }
        @Override public int hashCode() { return Objects.hash(left, right); }
    }

    public static final class Implies extends FormulaDto {
        public FormulaDto antecedent;
        public FormulaDto consequent;
        public Implies() {}
        public Implies(FormulaDto a, FormulaDto c) { this.antecedent = a; this.consequent = c; }
        @Override public boolean equals(Object o) {
            return o instanceof Implies i && Objects.equals(antecedent, i.antecedent) && Objects.equals(consequent, i.consequent);
        }
        @Override public int hashCode() { return Objects.hash(antecedent, consequent); }
    }
}
