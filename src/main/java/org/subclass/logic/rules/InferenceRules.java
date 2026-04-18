package org.subclass.logic.rules;

import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.One;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.Sequent;
import org.subclass.logic.rules.axiom.Axiom;
import org.subclass.logic.rules.conjunction.AndLeft;
import org.subclass.logic.rules.conjunction.AndRight;
import org.subclass.logic.rules.cut.Cut;
import org.subclass.logic.rules.disjunction.OrLeft;
import org.subclass.logic.rules.disjunction.OrRight;
import org.subclass.logic.rules.implication.ImpliesLeft;
import org.subclass.logic.rules.implication.ImpliesRight;
import org.subclass.logic.rules.negation.NotLeft;
import org.subclass.logic.rules.negation.NotRight;

import java.util.ArrayList;
import java.util.List;

/**
 * Static factory methods for building reified proof trees.
 *
 * Each factory returns a {@link org.subclass.logic.proof.typed.ProofNode}
 * instance typed as {@link Proof}&lt;L,R&gt; — the phantom cardinalities carry
 * the same compile-time structural-rule discipline as before, but every
 * returned object now carries its rule name and premises, so proof trees
 * can be traversed, serialized, and revalidated at runtime.
 *
 * Conclusion sequents are built straightforwardly from premise conclusions
 * plus the newly-introduced connective; the factories do not perform
 * cut-elimination or other meta-theoretic transformations. Callers that
 * need those can operate on the returned {@code ProofNode} directly.
 */
public final class InferenceRules {
    private InferenceRules() {
        // Utility class: no instantiation
    }

    // ---------------------------------------------------------------------
    //  Axiom
    // ---------------------------------------------------------------------

    /**
     * Axiom: A ⊢ A.
     *
     * @param formula the formula to prove from itself
     * @return a reified axiom node with conclusion {@code [formula] ⊢ [formula]}
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> axiom(Formula formula) {
        return new Axiom<>(formula);
    }

    // ---------------------------------------------------------------------
    //  Cut
    // ---------------------------------------------------------------------

    /**
     * Cut rule:
     * <pre>
     *   Γ ⊢ Δ, A        A, Γ' ⊢ Δ'
     *   ---------------------------- (Cut)
     *          Γ, Γ' ⊢ Δ, Δ'
     * </pre>
     *
     * @param cutFormula  the formula being cut out
     * @param left  a proof of {@code Γ ⊢ Δ, A} (conclusion contains {@code cutFormula} on the right)
     * @param right a proof of {@code A, Γ' ⊢ Δ'} (conclusion contains {@code cutFormula} on the left)
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> cut(
            Formula cutFormula, Proof<L, R> left, Proof<L, R> right) {
        Sequent<L, R> leftSeq = left.conclusion();
        Sequent<L, R> rightSeq = right.conclusion();
        List<Formula> antecedent = concat(
            leftSeq.antecedent(),
            withoutFirst(rightSeq.antecedent(), cutFormula));
        List<Formula> succedent = concat(
            withoutLast(leftSeq.succedent(), cutFormula),
            rightSeq.succedent());
        return new Cut<>(cutFormula, left, right, new Sequent<>(antecedent, succedent));
    }

    // ---------------------------------------------------------------------
    //  Conjunction
    // ---------------------------------------------------------------------

    /**
     * Right conjunction introduction: Γ ⊢ Δ, A and Γ ⊢ Δ, B imply Γ ⊢ Δ, A∧B.
     *
     * Each premise must have the shared antecedent Γ and a succedent ending
     * in its respective conjunct. The conclusion replaces both trailing
     * conjuncts with their conjunction.
     */
    public static <L extends Cardinality> Proof<L, One> andRight(
            Proof<L, One> left, Proof<L, One> right) {
        Formula a = onlySuccedent(left);
        Formula b = onlySuccedent(right);
        Sequent<L, One> conclusion = new Sequent<>(
            left.conclusion().antecedent(),
            List.of(new Formula.And(a, b)));
        return new AndRight<>(left, right, conclusion);
    }

    /**
     * Left conjunction introduction: Γ, A, B ⊢ Δ implies Γ, A∧B ⊢ Δ.
     *
     * Combines the last two antecedent formulas of the premise into their
     * conjunction.
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> andLeft(Proof<L, R> premise) {
        List<Formula> antecedent = premise.conclusion().antecedent();
        if (antecedent.size() < 2) {
            throw new IllegalArgumentException(
                "AndLeft requires at least two antecedent formulas; got " + antecedent.size());
        }
        Formula a = antecedent.get(antecedent.size() - 2);
        Formula b = antecedent.get(antecedent.size() - 1);
        List<Formula> reduced = new ArrayList<>(antecedent.subList(0, antecedent.size() - 2));
        reduced.add(new Formula.And(a, b));
        Sequent<L, R> conclusion = new Sequent<>(reduced, premise.conclusion().succedent());
        return new AndLeft<>(premise, conclusion);
    }

    // ---------------------------------------------------------------------
    //  Disjunction
    // ---------------------------------------------------------------------

    /**
     * Right disjunction introduction (multi-succedent combining form):
     * Γ ⊢ Δ, A, B implies Γ ⊢ Δ, A∨B.
     *
     * Combines the last two succedent formulas of the premise into their
     * disjunction.
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> orRight(Proof<L, R> premise) {
        List<Formula> succedent = premise.conclusion().succedent();
        if (succedent.size() < 2) {
            throw new IllegalArgumentException(
                "OrRight requires at least two succedent formulas; got " + succedent.size());
        }
        Formula a = succedent.get(succedent.size() - 2);
        Formula b = succedent.get(succedent.size() - 1);
        List<Formula> combined = new ArrayList<>(succedent.subList(0, succedent.size() - 2));
        combined.add(new Formula.Or(a, b));
        Sequent<L, R> conclusion = new Sequent<>(premise.conclusion().antecedent(), combined);
        return new OrRight<>(premise, conclusion);
    }

    /**
     * Left disjunction introduction: Γ, A ⊢ Δ and Γ, B ⊢ Δ imply Γ, A∨B ⊢ Δ.
     *
     * Case analysis on an assumed disjunction. Both premises must share the
     * succedent and the context Γ; the final antecedent formula of each is
     * replaced by A∨B.
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> orLeft(
            Proof<L, R> left, Proof<L, R> right) {
        Formula a = lastAntecedent(left);
        Formula b = lastAntecedent(right);
        List<Formula> antecedent = replaceLast(left.conclusion().antecedent(), new Formula.Or(a, b));
        Sequent<L, R> conclusion = new Sequent<>(antecedent, left.conclusion().succedent());
        return new OrLeft<>(left, right, conclusion);
    }

    // ---------------------------------------------------------------------
    //  Implication
    // ---------------------------------------------------------------------

    /**
     * Right implication introduction: Γ, A ⊢ Δ, B implies Γ ⊢ Δ, A→B.
     *
     * The premise's final antecedent formula and final succedent formula are
     * combined into an implication on the right.
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> impliesRight(
            Proof<L, R> premise) {
        Formula a = lastAntecedent(premise);
        Formula b = lastSuccedent(premise);
        List<Formula> antecedent = dropLast(premise.conclusion().antecedent());
        List<Formula> succedent = replaceLast(premise.conclusion().succedent(), new Formula.Implies(a, b));
        Sequent<L, R> conclusion = new Sequent<>(antecedent, succedent);
        return new ImpliesRight<>(premise, conclusion);
    }

    /**
     * Left implication introduction: Γ ⊢ Δ, A and Γ, B ⊢ Δ imply Γ, A→B ⊢ Δ.
     *
     * Use an implication assumption by proving its antecedent (left premise)
     * and discharging its consequent (right premise). The conclusion inherits
     * the shared context.
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> impliesLeft(
            Proof<L, R> antecedentPremise, Proof<L, R> consequentPremise) {
        Formula a = lastSuccedent(antecedentPremise);
        Formula b = lastAntecedent(consequentPremise);
        List<Formula> antecedent = appendReplaceLast(
            antecedentPremise.conclusion().antecedent(),
            dropLast(consequentPremise.conclusion().antecedent()),
            new Formula.Implies(a, b));
        List<Formula> succedent = concat(
            dropLast(antecedentPremise.conclusion().succedent()),
            consequentPremise.conclusion().succedent());
        Sequent<L, R> conclusion = new Sequent<>(antecedent, succedent);
        return new ImpliesLeft<>(antecedentPremise, consequentPremise, conclusion);
    }

    // ---------------------------------------------------------------------
    //  Negation
    // ---------------------------------------------------------------------

    /**
     * Right negation introduction: Γ, A ⊢ Δ implies Γ ⊢ Δ, ¬A.
     *
     * The final antecedent formula is moved to the succedent as its negation.
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> notRight(Proof<L, R> premise) {
        Formula a = lastAntecedent(premise);
        List<Formula> antecedent = dropLast(premise.conclusion().antecedent());
        List<Formula> succedent = append(premise.conclusion().succedent(), new Formula.Not(a));
        Sequent<L, R> conclusion = new Sequent<>(antecedent, succedent);
        return new NotRight<>(premise, conclusion);
    }

    /**
     * Left negation introduction: Γ ⊢ Δ, A implies Γ, ¬A ⊢ Δ.
     *
     * The final succedent formula is moved to the antecedent as its negation.
     */
    public static <L extends Cardinality, R extends Cardinality> Proof<L, R> notLeft(Proof<L, R> premise) {
        Formula a = lastSuccedent(premise);
        List<Formula> antecedent = append(premise.conclusion().antecedent(), new Formula.Not(a));
        List<Formula> succedent = dropLast(premise.conclusion().succedent());
        Sequent<L, R> conclusion = new Sequent<>(antecedent, succedent);
        return new NotLeft<>(premise, conclusion);
    }

    // ---------------------------------------------------------------------
    //  Small sequent helpers — not public API.
    // ---------------------------------------------------------------------

    private static Formula onlySuccedent(Proof<?, ?> p) {
        List<Formula> s = p.conclusion().succedent();
        if (s.size() != 1) {
            throw new IllegalArgumentException("Expected exactly one succedent formula, got " + s.size());
        }
        return s.get(0);
    }

    private static Formula lastAntecedent(Proof<?, ?> p) {
        List<Formula> a = p.conclusion().antecedent();
        if (a.isEmpty()) {
            throw new IllegalArgumentException("Expected non-empty antecedent");
        }
        return a.get(a.size() - 1);
    }

    private static Formula lastSuccedent(Proof<?, ?> p) {
        List<Formula> s = p.conclusion().succedent();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Expected non-empty succedent");
        }
        return s.get(s.size() - 1);
    }

    private static List<Formula> concat(List<Formula> a, List<Formula> b) {
        List<Formula> out = new ArrayList<>(a.size() + b.size());
        out.addAll(a);
        out.addAll(b);
        return out;
    }

    private static List<Formula> append(List<Formula> list, Formula f) {
        List<Formula> out = new ArrayList<>(list.size() + 1);
        out.addAll(list);
        out.add(f);
        return out;
    }

    private static List<Formula> dropLast(List<Formula> list) {
        if (list.isEmpty()) {
            return list;
        }
        return new ArrayList<>(list.subList(0, list.size() - 1));
    }

    private static List<Formula> replaceLast(List<Formula> list, Formula replacement) {
        if (list.isEmpty()) {
            return List.of(replacement);
        }
        List<Formula> out = new ArrayList<>(list);
        out.set(out.size() - 1, replacement);
        return out;
    }

    private static List<Formula> withoutFirst(List<Formula> list, Formula f) {
        if (!list.isEmpty() && list.get(0).equals(f)) {
            return new ArrayList<>(list.subList(1, list.size()));
        }
        return list;
    }

    private static List<Formula> withoutLast(List<Formula> list, Formula f) {
        if (!list.isEmpty() && list.get(list.size() - 1).equals(f)) {
            return dropLast(list);
        }
        return list;
    }

    private static List<Formula> appendReplaceLast(
            List<Formula> first, List<Formula> prefix, Formula replacement) {
        List<Formula> out = new ArrayList<>(first.size() + prefix.size() + 1);
        out.addAll(first);
        out.addAll(prefix);
        out.add(replacement);
        return out;
    }
}
