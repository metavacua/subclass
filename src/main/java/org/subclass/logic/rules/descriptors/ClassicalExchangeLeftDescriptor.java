package org.subclass.logic.rules.descriptors;

import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.rules.RuleDescriptor;
import org.subclass.logic.rules.structural.ClassicalExchangeLeft;

public final class ClassicalExchangeLeftDescriptor implements RuleDescriptor {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<? extends ProofNode<?, ?>> ruleClass() {
        return (Class) ClassicalExchangeLeft.class;
    }
}
