package org.subclass.logic.rules.descriptors;

import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.rules.RuleDescriptor;
import org.subclass.logic.rules.implication.ImpliesRight;

public final class ImpliesRightDescriptor implements RuleDescriptor {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<? extends ProofNode<?, ?>> ruleClass() {
        return (Class) ImpliesRight.class;
    }
}
