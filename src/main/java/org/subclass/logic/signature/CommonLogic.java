package org.subclass.logic.signature;

import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.One;

import java.util.Set;

/**
 * The common logic of LJ and DualLJ: the bottom of the diamond. Sequents
 * are {@code A ⊢ A} in shape — one formula each side. Every theorem of
 * Common is a theorem of LJ, DualLJ, and LK by the inclusion morphisms.
 *
 * <p>The reflexive axiom schema is the minimal {@code A ⊢ A} form.
 */
public final class CommonLogic implements Logic {

    public static final CommonLogic INSTANCE = new CommonLogic();

    private CommonLogic() {}

    @Override
    public ReflexiveAxiomSchema axiomSchema() {
        return ReflexiveAxiomSchema.COMMON;
    }

    @Override
    public Set<String> admittedRules() {
        return Set.of(
            "Axiom", "Cut",
            "AndLeft", "AndRight",
            "OrLeft", "OrRight",
            "ImpliesLeft", "ImpliesRight",
            "NotLeft", "NotRight"
        );
    }

    @Override
    public Class<? extends Cardinality> antecedentCardinality() {
        return One.class;
    }

    @Override
    public Class<? extends Cardinality> succedentCardinality() {
        return One.class;
    }

    @Override
    public String label() {
        return "Common";
    }

    @Override
    public String toString() {
        return "CommonLogic[⟨One,One⟩, axiom=A⊢A]";
    }
}
