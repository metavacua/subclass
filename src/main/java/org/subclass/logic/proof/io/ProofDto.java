package org.subclass.logic.proof.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Serialization-friendly mirror of {@link org.subclass.logic.proof.typed.Proof}.
 *
 * A {@code ProofDto} is a uniform node: it records the rule name (as registered
 * in {@link org.subclass.logic.rules.RuleRegistry}), a list of premise DTOs,
 * the conclusion sequent, and an optional active formula used by rules that
 * mention a distinguished formula in their conclusion (currently only
 * {@code Axiom} and {@code Cut}).
 *
 * No per-rule polymorphism is required on the DTO side: the rule-name string
 * and the uniform premise list suffice, and the typed record is reconstructed
 * at deserialization time by looking up the rule name in the registry.
 */
public final class ProofDto {

    /** Name of the inference rule (as returned by {@code ProofNode.ruleName()}). */
    public String rule;

    /**
     * Active formula, when the rule carries one directly (Axiom's identity
     * formula, Cut's cut-formula). {@code null} for every other rule.
     */
    public FormulaDto formula;

    public List<ProofDto> premises = new ArrayList<>();

    public SequentDto conclusion;

    public ProofDto() {}

    public ProofDto(String rule,
                    FormulaDto formula,
                    List<ProofDto> premises,
                    SequentDto conclusion) {
        this.rule = rule;
        this.formula = formula;
        this.premises = new ArrayList<>(premises);
        this.conclusion = conclusion;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ProofDto p
            && Objects.equals(rule, p.rule)
            && Objects.equals(formula, p.formula)
            && Objects.equals(premises, p.premises)
            && Objects.equals(conclusion, p.conclusion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, formula, premises, conclusion);
    }
}
