package org.subclass.logic.signature;

import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.One;

import java.util.HashSet;
import java.util.Set;

/**
 * Gentzen's LJ: intuitionistic sequent calculus. Multi-formula antecedent,
 * single-formula succedent. Axiom schema: {@code Γ, A ⊢ A}.
 *
 * <p>Admits all connective rules plus left/right weakening (where valid
 * under the single-succedent constraint), left contraction, and the three
 * exchange primitives. Right weakening and right contraction are not
 * admitted because the succedent holds at most one formula.
 */
public final class IntuitionisticLJ implements Logic {

    public static final IntuitionisticLJ INSTANCE = new IntuitionisticLJ();

    private IntuitionisticLJ() {}

    @Override
    public ReflexiveAxiomSchema axiomSchema() {
        return ReflexiveAxiomSchema.INTUITIONISTIC;
    }

    @Override
    public Set<String> admittedRules() {
        Set<String> rules = new HashSet<>(Set.of(
            "Axiom", "Cut",
            "AndLeft", "AndRight",
            "OrLeft", "OrRight",
            "ImpliesLeft", "ImpliesRight",
            "NotLeft", "NotRight",
            "WeakeningLeft", "ContractionLeft",
            "AdjacentExchangeLeft", "ClassicalExchangeLeft"
        ));
        return Set.copyOf(rules);
    }

    @Override
    public Class<? extends Cardinality> antecedentCardinality() {
        return Many.class;
    }

    @Override
    public Class<? extends Cardinality> succedentCardinality() {
        return One.class;
    }

    @Override
    public String label() {
        return "LJ";
    }

    @Override
    public String toString() {
        return "IntuitionisticLJ[⟨Many,One⟩, axiom=Γ,A⊢A]";
    }
}
