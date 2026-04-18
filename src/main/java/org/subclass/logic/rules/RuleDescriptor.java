package org.subclass.logic.rules;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.ProofNode;

/**
 * Service-provider interface for inference-rule classes.
 *
 * Each reified rule (under {@code org.subclass.logic.rules.*}) ships an
 * implementation of this SPI that advertises the corresponding
 * {@link ProofNode} record class. Implementations are registered via
 * {@code META-INF/services/org.subclass.logic.rules.RuleDescriptor} and
 * discovered at runtime with {@link java.util.ServiceLoader}, so the
 * reflective proof walker, serialization, and metatheory validator can
 * enumerate admissible rules without hard-coding them.
 */
public interface RuleDescriptor {

    /**
     * The record class implementing this rule. Must be annotated with
     * {@link RuleSpec}.
     */
    Class<? extends ProofNode<?, ?>> ruleClass();

    /**
     * Convenience accessor for the {@link RuleSpec} attached to
     * {@link #ruleClass()}. Throws {@link IllegalStateException} if the
     * registered class is missing the annotation.
     */
    default RuleSpec ruleSpec() {
        RuleSpec spec = ruleClass().getAnnotation(RuleSpec.class);
        if (spec == null) {
            throw new IllegalStateException(
                "RuleDescriptor class " + ruleClass().getName() + " is not annotated with @RuleSpec");
        }
        return spec;
    }

    /**
     * The rule's canonical name (from its {@link RuleSpec#name()}).
     */
    default String name() {
        return ruleSpec().name();
    }
}
