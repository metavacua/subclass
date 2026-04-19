package org.subclass.logic.signature;

import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Many;

import java.util.Set;

/**
 * Gentzen's LK: classical sequent calculus. Multi-formula antecedent and
 * succedent. Axiom schema: {@code Γ, A ⊢ A, Δ} — the strictly most
 * permissive reflexive axiom of the four primary nodes.
 *
 * <p>Admits every structural rule (weakening, contraction, and both
 * exchange primitives) on both sides, plus every connective rule. Every
 * admitted rule of LJ, LDJ, and Common is also admitted by LK (this is
 * the claim the inclusion morphisms witness).
 */
public final class ClassicalLK implements Logic {

    public static final ClassicalLK INSTANCE = new ClassicalLK();

    private ClassicalLK() {}

    @Override
    public ReflexiveAxiomSchema axiomSchema() {
        return ReflexiveAxiomSchema.CLASSICAL;
    }

    @Override
    public Set<String> admittedRules() {
        return Set.of(
            "Axiom", "Cut",
            "AndLeft", "AndRight",
            "OrLeft", "OrRight",
            "ImpliesLeft", "ImpliesRight",
            "NotLeft", "NotRight",
            "WeakeningLeft", "WeakeningRight",
            "ContractionLeft", "ContractionRight",
            "AdjacentExchangeLeft", "AdjacentExchangeRight",
            "ClassicalExchangeLeft", "ClassicalExchangeRight"
        );
    }

    @Override
    public Class<? extends Cardinality> antecedentCardinality() {
        return Many.class;
    }

    @Override
    public Class<? extends Cardinality> succedentCardinality() {
        return Many.class;
    }

    @Override
    public String label() {
        return "LK";
    }

    @Override
    public String toString() {
        return "ClassicalLK[⟨Many,Many⟩, axiom=Γ,A⊢A,Δ]";
    }
}
