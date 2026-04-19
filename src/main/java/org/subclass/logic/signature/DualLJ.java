package org.subclass.logic.signature;

import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.One;

import java.util.Set;

/**
 * Dual intuitionistic logic (LDJ): the paraconsistent mirror of LJ.
 * Single-formula antecedent, multi-formula succedent. Axiom schema:
 * {@code A ⊢ A, Δ}.
 *
 * <p>Admits all connective rules plus right weakening, right contraction,
 * and the exchange primitives on the right. Left weakening and left
 * contraction are not admitted because the antecedent holds at most one
 * formula.
 */
public final class DualLJ implements Logic {

    public static final DualLJ INSTANCE = new DualLJ();

    private DualLJ() {}

    @Override
    public ReflexiveAxiomSchema axiomSchema() {
        return ReflexiveAxiomSchema.DUAL_INTUITIONISTIC;
    }

    @Override
    public Set<String> admittedRules() {
        return Set.of(
            "Axiom", "Cut",
            "AndLeft", "AndRight",
            "OrLeft", "OrRight",
            "ImpliesLeft", "ImpliesRight",
            "NotLeft", "NotRight",
            "WeakeningRight", "ContractionRight",
            "AdjacentExchangeRight", "ClassicalExchangeRight"
        );
    }

    @Override
    public Class<? extends Cardinality> antecedentCardinality() {
        return One.class;
    }

    @Override
    public Class<? extends Cardinality> succedentCardinality() {
        return Many.class;
    }

    @Override
    public String label() {
        return "LDJ";
    }

    @Override
    public String toString() {
        return "DualLJ[⟨One,Many⟩, axiom=A⊢A,Δ]";
    }
}
