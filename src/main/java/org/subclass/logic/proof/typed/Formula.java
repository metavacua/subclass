package org.subclass.logic.proof.typed;

import java.util.Objects;

/**
 * Represents a logical formula in sequent calculus.
 *
 * Formulas are the building blocks of sequents. A sequent Γ ⊢ Δ consists of
 * antecedent formulas (Γ) on the left and succedent formulas (Δ) on the right.
 *
 * Implementations: Atom, Not, Or, And, Implies, etc.
 */
public sealed interface Formula permits Atom, Not, Or, And, Implies {
    /**
     * Get a human-readable representation of this formula.
     */
    String representation();

    /**
     * An atomic formula (propositional variable like A, B, C).
     */
    final class Atom implements Formula {
        private final String name;

        public Atom(String name) {
            this.name = Objects.requireNonNull(name, "Atom name cannot be null");
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Atom name cannot be empty");
            }
        }

        public String name() {
            return name;
        }

        @Override
        public String representation() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Atom a && name.equals(a.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    /**
     * Negation (¬A).
     */
    final class Not implements Formula {
        private final Formula formula;

        public Not(Formula formula) {
            this.formula = Objects.requireNonNull(formula, "Formula cannot be null");
        }

        public Formula formula() {
            return formula;
        }

        @Override
        public String representation() {
            return "¬" + formula.representation();
        }

        @Override
        public String toString() {
            return "¬(" + formula + ")";
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Not n && formula.equals(n.formula);
        }

        @Override
        public int hashCode() {
            return Objects.hash(formula);
        }
    }

    /**
     * Disjunction (A ∨ B).
     */
    final class Or implements Formula {
        private final Formula left;
        private final Formula right;

        public Or(Formula left, Formula right) {
            this.left = Objects.requireNonNull(left, "Left formula cannot be null");
            this.right = Objects.requireNonNull(right, "Right formula cannot be null");
        }

        public Formula left() {
            return left;
        }

        public Formula right() {
            return right;
        }

        @Override
        public String representation() {
            return left.representation() + " ∨ " + right.representation();
        }

        @Override
        public String toString() {
            return "(" + left + " ∨ " + right + ")";
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Or or && left.equals(or.left) && right.equals(or.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }

    /**
     * Conjunction (A ∧ B).
     */
    final class And implements Formula {
        private final Formula left;
        private final Formula right;

        public And(Formula left, Formula right) {
            this.left = Objects.requireNonNull(left, "Left formula cannot be null");
            this.right = Objects.requireNonNull(right, "Right formula cannot be null");
        }

        public Formula left() {
            return left;
        }

        public Formula right() {
            return right;
        }

        @Override
        public String representation() {
            return left.representation() + " ∧ " + right.representation();
        }

        @Override
        public String toString() {
            return "(" + left + " ∧ " + right + ")";
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof And a && left.equals(a.left) && right.equals(a.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }

    /**
     * Implication (A → B).
     */
    final class Implies implements Formula {
        private final Formula antecedent;
        private final Formula consequent;

        public Implies(Formula antecedent, Formula consequent) {
            this.antecedent = Objects.requireNonNull(antecedent, "Antecedent cannot be null");
            this.consequent = Objects.requireNonNull(consequent, "Consequent cannot be null");
        }

        public Formula antecedent() {
            return antecedent;
        }

        public Formula consequent() {
            return consequent;
        }

        @Override
        public String representation() {
            return antecedent.representation() + " → " + consequent.representation();
        }

        @Override
        public String toString() {
            return "(" + antecedent + " → " + consequent + ")";
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Implies i && antecedent.equals(i.antecedent) && consequent.equals(i.consequent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(antecedent, consequent);
        }
    }
}
