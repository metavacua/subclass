package org.subclass.logic.rules;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.ProofNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Discovers {@link RuleDescriptor}s via {@link ServiceLoader} and indexes them
 * by rule name and by implementation class.
 *
 * The registry is the runtime counterpart to the {@code @RuleSpec} compile-time
 * metadata: the reflective proof walker and the (de)serialization code both
 * use it to locate a rule class by its {@link RuleSpec#name()}.
 */
public final class RuleRegistry {

    private static final RuleRegistry INSTANCE = new RuleRegistry();

    private final Map<String, RuleDescriptor> byName = new HashMap<>();
    private final Map<Class<? extends ProofNode<?, ?>>, RuleDescriptor> byClass = new HashMap<>();

    private RuleRegistry() {
        ServiceLoader<RuleDescriptor> loader = ServiceLoader.load(RuleDescriptor.class);
        for (RuleDescriptor descriptor : loader) {
            RuleSpec spec = descriptor.ruleSpec();
            if (byName.putIfAbsent(spec.name(), descriptor) != null) {
                throw new IllegalStateException("Duplicate rule registered: " + spec.name());
            }
            byClass.put(descriptor.ruleClass(), descriptor);
        }
    }

    public static RuleRegistry instance() {
        return INSTANCE;
    }

    /**
     * Look up a rule descriptor by its {@link RuleSpec#name()}.
     */
    public Optional<RuleDescriptor> find(String ruleName) {
        return Optional.ofNullable(byName.get(ruleName));
    }

    /**
     * Look up a rule descriptor by its implementing record class.
     */
    public Optional<RuleDescriptor> find(Class<? extends ProofNode<?, ?>> ruleClass) {
        return Optional.ofNullable(byClass.get(ruleClass));
    }

    /**
     * All registered rule names, in registration order.
     */
    public java.util.Set<String> ruleNames() {
        return java.util.Collections.unmodifiableSet(byName.keySet());
    }

    /**
     * All registered descriptors.
     */
    public java.util.Collection<RuleDescriptor> descriptors() {
        return java.util.Collections.unmodifiableCollection(byName.values());
    }
}
