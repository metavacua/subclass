package org.subclass.logic.signature;

import org.subclass.logic.proof.typed.Sequent;

/**
 * The reflexive axiom schema of a logic: the shape of admissible identity
 * axioms {@code Γ, A ⊢ A, Δ}, where the permissible Γ and Δ depend on the
 * logic.
 *
 * <p>Four distinguished shapes correspond to the four primary nodes of the
 * diamond:
 * <ul>
 *   <li>{@link #COMMON}: {@code A ⊢ A} — both contexts empty</li>
 *   <li>{@link #INTUITIONISTIC}: {@code Γ, A ⊢ A} — antecedent only</li>
 *   <li>{@link #DUAL_INTUITIONISTIC}: {@code A ⊢ A, Δ} — succedent only</li>
 *   <li>{@link #CLASSICAL}: {@code Γ, A ⊢ A, Δ} — full context on both</li>
 * </ul>
 *
 * Inclusions are strict: {@code COMMON ⊂ INTUITIONISTIC ∩ DUAL_INTUITIONISTIC},
 * and {@code INTUITIONISTIC ∪ DUAL_INTUITIONISTIC ⊂ CLASSICAL}.
 *
 * <p>A schema admits a sequent if the sequent matches its shape: some formula
 * {@code A} appears on both sides, with permissible remainders on each side.
 */
public interface ReflexiveAxiomSchema {

    /**
     * Does {@code sequent} fit this schema (i.e. is it a valid identity
     * axiom under this logic)?
     */
    boolean admits(Sequent<?, ?> sequent);

    /** Short name (e.g. "common", "intuitionistic", "dual", "classical"). */
    String name();

    ReflexiveAxiomSchema COMMON = new ReflexiveAxiomSchema() {
        @Override public boolean admits(Sequent<?, ?> s) {
            return s.antecedent().size() == 1
                && s.succedent().size() == 1
                && s.antecedent().get(0).equals(s.succedent().get(0));
        }
        @Override public String name() { return "common"; }
    };

    ReflexiveAxiomSchema INTUITIONISTIC = new ReflexiveAxiomSchema() {
        @Override public boolean admits(Sequent<?, ?> s) {
            if (s.succedent().size() != 1 || s.antecedent().isEmpty()) return false;
            return s.antecedent().contains(s.succedent().get(0));
        }
        @Override public String name() { return "intuitionistic"; }
    };

    ReflexiveAxiomSchema DUAL_INTUITIONISTIC = new ReflexiveAxiomSchema() {
        @Override public boolean admits(Sequent<?, ?> s) {
            if (s.antecedent().size() != 1 || s.succedent().isEmpty()) return false;
            return s.succedent().contains(s.antecedent().get(0));
        }
        @Override public String name() { return "dual"; }
    };

    ReflexiveAxiomSchema CLASSICAL = new ReflexiveAxiomSchema() {
        @Override public boolean admits(Sequent<?, ?> s) {
            if (s.antecedent().isEmpty() || s.succedent().isEmpty()) return false;
            for (var a : s.antecedent()) {
                if (s.succedent().contains(a)) return true;
            }
            return false;
        }
        @Override public String name() { return "classical"; }
    };
}
