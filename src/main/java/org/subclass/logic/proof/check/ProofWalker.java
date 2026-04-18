package org.subclass.logic.proof.check;

import org.subclass.annotation.RuleSpec;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.rules.RuleRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Reflective walker over {@link Proof} trees.
 *
 * Traverses a proof post-order, reading each node's {@link RuleSpec} metadata
 * and cross-checking:
 *
 * <ul>
 *   <li>every visited node implements {@link ProofNode};</li>
 *   <li>every {@code ProofNode} class carries a {@code @RuleSpec} annotation;</li>
 *   <li>the declared {@code @RuleSpec.name()} matches the name registered in
 *       the {@link RuleRegistry} ServiceLoader;</li>
 *   <li>the node's observed premise count matches
 *       {@code @RuleSpec.premiseCount()}.</li>
 * </ul>
 *
 * This is the runtime counterpart to {@code TheoremProcessor}'s compile-time
 * checks: the annotation processor validates that each rule class is
 * well-formed; the walker validates that each proof tree only refers to
 * well-formed rule classes and uses them with the advertised premise count.
 */
public final class ProofWalker {

    private ProofWalker() {}

    /**
     * Walk {@code proof} post-order, invoking {@code visitor} on each node
     * (leaves first). The visitor receives the underlying {@link ProofNode}.
     */
    public static void walk(Proof<?, ?> proof, Consumer<ProofNode<?, ?>> visitor) {
        ProofNode<?, ?> node = asNode(proof);
        for (Proof<?, ?> p : node.premises()) {
            walk(p, visitor);
        }
        visitor.accept(node);
    }

    /**
     * Validate {@code proof} against registered rule metadata. Returns a
     * report; callers can inspect {@link CheckReport#isOk()} or read the
     * accumulated {@link CheckReport#violations()}.
     */
    public static CheckReport check(Proof<?, ?> proof) {
        List<String> violations = new ArrayList<>();
        walk(proof, node -> validate(node, violations));
        return new CheckReport(List.copyOf(violations));
    }

    private static void validate(ProofNode<?, ?> node, List<String> violations) {
        Class<?> cls = node.getClass();
        RuleSpec spec = cls.getAnnotation(RuleSpec.class);
        if (spec == null) {
            violations.add(cls.getName() + ": missing @RuleSpec annotation");
            return;
        }
        if (!Objects.equals(spec.name(), node.ruleName())) {
            violations.add(cls.getName()
                + ": @RuleSpec.name=" + spec.name()
                + " but ruleName()=" + node.ruleName());
        }
        if (RuleRegistry.instance().find(spec.name()).isEmpty()) {
            violations.add(cls.getName()
                + ": rule '" + spec.name() + "' not registered in ServiceLoader registry");
        }
        int expected = spec.premiseCount();
        int actual = node.premises().size();
        if (expected != actual) {
            violations.add(cls.getName()
                + ": @RuleSpec.premiseCount=" + expected
                + " but observed " + actual + " premise(s)");
        }
    }

    private static ProofNode<?, ?> asNode(Proof<?, ?> p) {
        if (!(p instanceof ProofNode<?, ?> node)) {
            throw new IllegalArgumentException(
                "Proof node is not a ProofNode: " + p.getClass().getName());
        }
        return node;
    }

    /**
     * Outcome of a walk. Empty {@link #violations()} list means the proof is
     * well-formed with respect to registered rule metadata.
     */
    public record CheckReport(List<String> violations) {
        public boolean isOk() { return violations.isEmpty(); }
    }
}
