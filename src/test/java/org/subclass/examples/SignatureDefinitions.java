package org.subclass.examples;

import org.subclass.logic.signature.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines canonical logical signatures for the tetragram examples.
 * Each signature is in minimal canonical form emphasizing functional
 * completeness/incompleteness.
 */
public class SignatureDefinitions {
    /**
     * LK: Classical Sequent Calculus
     * Functionally complete: has {∧, ∨, ¬, →} with full set of structural rules
     * Node: <Consistent=true, Complete=true>
     *
     * LK is the foundation of classical logic. It includes:
     * - Negation (¬) allowing classical truth value assignment
     * - Conjunction (∧) and Disjunction (∨) for compound formulas
     * - Implication (→) for conditionals
     * - All three structural rules: Weakening, Contraction, Exchange
     *
     * LEM (law of excluded middle) is a theorem in LK.
     * Proof: The sequent (⊢ A ∨ ¬A) is derivable.
     */
    public static LogicalSignature createLKSignature() {
        Connective negation = new Connective(
            "¬", 1, "propositional",
            Arrays.asList(
                new InferenceRule("neg_left", "¬", "left",
                    Arrays.asList("Γ ⊢ Δ, A"),
                    "Γ, ¬A ⊢ Δ",
                    "Negation left introduction: assume ¬A on left, conclude from A on right")
            ),
            Arrays.asList(
                new InferenceRule("neg_right", "¬", "right",
                    Arrays.asList("Γ, A ⊢ Δ"),
                    "Γ ⊢ Δ, ¬A",
                    "Negation right introduction: to prove ¬A, assume A and derive contradiction")
            ),
            "Logical negation: classical negation with full classical rules"
        );

        Connective conjunction = new Connective(
            "∧", 2, "propositional",
            Arrays.asList(
                new InferenceRule("and_left_1", "∧", "left",
                    Arrays.asList("Γ, A ⊢ Δ"),
                    "Γ, A ∧ B ⊢ Δ",
                    "Left intro 1: from A ∧ B, we have A"),
                new InferenceRule("and_left_2", "∧", "left",
                    Arrays.asList("Γ, B ⊢ Δ"),
                    "Γ, A ∧ B ⊢ Δ",
                    "Left intro 2: from A ∧ B, we have B")
            ),
            Arrays.asList(
                new InferenceRule("and_right", "∧", "right",
                    Arrays.asList("Γ ⊢ Δ, A", "Γ ⊢ Δ, B"),
                    "Γ ⊢ Δ, A ∧ B",
                    "Right intro: to prove A ∧ B, prove both A and B")
            ),
            "Logical conjunction: classical intersection of truth"
        );

        Connective disjunction = new Connective(
            "∨", 2, "propositional",
            Arrays.asList(
                new InferenceRule("or_left", "∨", "left",
                    Arrays.asList("Γ, A ⊢ Δ", "Γ, B ⊢ Δ"),
                    "Γ, A ∨ B ⊢ Δ",
                    "Left intro: from A ∨ B, handle both cases")
            ),
            Arrays.asList(
                new InferenceRule("or_right_1", "∨", "right",
                    Arrays.asList("Γ ⊢ Δ, A"),
                    "Γ ⊢ Δ, A ∨ B",
                    "Right intro 1: A ∨ B follows from A"),
                new InferenceRule("or_right_2", "∨", "right",
                    Arrays.asList("Γ ⊢ Δ, B"),
                    "Γ ⊢ Δ, A ∨ B",
                    "Right intro 2: A ∨ B follows from B")
            ),
            "Logical disjunction: classical union of truth"
        );

        Connective implication = new Connective(
            "→", 2, "propositional",
            Arrays.asList(
                new InferenceRule("impl_left", "→", "left",
                    Arrays.asList("Γ ⊢ Δ, A", "Γ, B ⊢ Δ"),
                    "Γ, A → B ⊢ Δ",
                    "Left intro: from A → B, either A is false or B is true")
            ),
            Arrays.asList(
                new InferenceRule("impl_right", "→", "right",
                    Arrays.asList("Γ, A ⊢ Δ, B"),
                    "Γ ⊢ Δ, A → B",
                    "Right intro: to prove A → B, assume A and prove B")
            ),
            "Logical implication: classical material conditional"
        );

        Set<Connective> connectives = new HashSet<>(Arrays.asList(
            negation, conjunction, disjunction, implication
        ));

        Set<StructuralRule> structuralRules = new HashSet<>(Arrays.asList(
            StructuralRule.WEAKENING,
            StructuralRule.CONTRACTION,
            StructuralRule.EXCHANGE
        ));

        return new LogicalSignature.Builder("LK", "Classical Sequent Calculus (Gentzen)")
            .addConnectives(connectives)
            .addStructuralRules(structuralRules)
            .functionallyComplete(true)
            .description("Gentzen's LK: complete classical sequent calculus with all " +
                "structural rules. Functionally complete and proves LEM.")
            .build();
    }

    /**
     * LJ: Intuitionistic Sequent Calculus
     * Functionally incomplete: same connectives as LK but restricted right side
     * Node: <Consistent=true, Complete=false>
     *
     * LJ restricts sequents to at most one formula on the right (Γ ⊢ Δ where |Δ| ≤ 1).
     * This restriction makes the logic paracomplete: LEM is not a theorem.
     * The negation and other connectives are redefined to respect this restriction.
     *
     * LEM is NOT a theorem in LJ.
     * Proof: We cannot prove (⊢ A ∨ ¬A) in the intuitionistic setting because:
     *  - The disjunction rule requires proving one disjunct
     *  - Neither A nor ¬A can be proven from empty assumptions in general
     */
    public static LogicalSignature createLJSignature() {
        // LJ uses the same symbolic connectives as LK, but with restricted right side
        // For simplicity in this representation, we show the same rules but note the restriction
        Connective negation = new Connective(
            "¬", 1, "propositional",
            Arrays.asList(
                new InferenceRule("neg_left_lj", "¬", "left",
                    Arrays.asList("Γ ⊢ A"),  // Note: single consequent only
                    "Γ, ¬A ⊢",
                    "Intuitionistic negation: ¬A on left eliminates consequent")
            ),
            Arrays.asList(
                new InferenceRule("neg_right_lj", "¬", "right",
                    Arrays.asList("Γ, A ⊢"),  // Derive contradiction
                    "Γ ⊢ ¬A",
                    "Intuitionistic negation: to prove ¬A, assume A and derive contradiction")
            ),
            "Intuitionistic negation: respects right restriction"
        );

        Connective conjunction = new Connective(
            "∧", 2, "propositional",
            Arrays.asList(
                new InferenceRule("and_left_1_lj", "∧", "left",
                    Arrays.asList("Γ, A ⊢ C"),
                    "Γ, A ∧ B ⊢ C",
                    "Intuitionistic left intro 1: handle A from A ∧ B"),
                new InferenceRule("and_left_2_lj", "∧", "left",
                    Arrays.asList("Γ, B ⊢ C"),
                    "Γ, A ∧ B ⊢ C",
                    "Intuitionistic left intro 2: handle B from A ∧ B")
            ),
            Arrays.asList(
                new InferenceRule("and_right_lj", "∧", "right",
                    Arrays.asList("Γ ⊢ A", "Γ ⊢ B"),
                    "Γ ⊢ A ∧ B",
                    "Intuitionistic right intro: prove A ∧ B from both components")
            ),
            "Intuitionistic conjunction"
        );

        Connective disjunction = new Connective(
            "∨", 2, "propositional",
            Arrays.asList(
                new InferenceRule("or_left_lj", "∨", "left",
                    Arrays.asList("Γ, A ⊢ C", "Γ, B ⊢ C"),
                    "Γ, A ∨ B ⊢ C",
                    "Intuitionistic left intro: case analysis on disjunction")
            ),
            Arrays.asList(
                new InferenceRule("or_right_1_lj", "∨", "right",
                    Arrays.asList("Γ ⊢ A"),
                    "Γ ⊢ A ∨ B",
                    "Intuitionistic right intro 1: left disjunct"),
                new InferenceRule("or_right_2_lj", "∨", "right",
                    Arrays.asList("Γ ⊢ B"),
                    "Γ ⊢ A ∨ B",
                    "Intuitionistic right intro 2: right disjunct")
            ),
            "Intuitionistic disjunction"
        );

        Connective implication = new Connective(
            "→", 2, "propositional",
            Arrays.asList(
                new InferenceRule("impl_left_lj", "→", "left",
                    Arrays.asList("Γ ⊢ A", "Γ, B ⊢ C"),
                    "Γ, A → B ⊢ C",
                    "Intuitionistic left intro: use implication by providing antecedent")
            ),
            Arrays.asList(
                new InferenceRule("impl_right_lj", "→", "right",
                    Arrays.asList("Γ, A ⊢ B"),
                    "Γ ⊢ A → B",
                    "Intuitionistic right intro: assume A to prove B")
            ),
            "Intuitionistic implication"
        );

        Set<Connective> connectives = new HashSet<>(Arrays.asList(
            negation, conjunction, disjunction, implication
        ));

        Set<StructuralRule> structuralRules = new HashSet<>(Arrays.asList(
            StructuralRule.WEAKENING,
            StructuralRule.CONTRACTION,
            StructuralRule.EXCHANGE
        ));

        return new LogicalSignature.Builder("LJ", "Intuitionistic Sequent Calculus")
            .addConnectives(connectives)
            .addStructuralRules(structuralRules)
            .functionallyComplete(false)
            .description("Heyting's LJ: intuitionistic sequent calculus with right-side " +
                "restriction (|Δ| ≤ 1). Functionally incomplete, does NOT prove LEM.")
            .build();
    }

    /**
     * LDJ: Paraconsistent dual of LJ (Urbas-Rauszer)
     * Functionally incomplete: same connectives as LJ but with left-side restriction
     * Node: <Consistent=false, Complete=true>
     *
     * This signature is the dual of LJ under the exchange of left and right (Urbas-Rauszer).
     * It restricts sequents to at most one formula on the LEFT (Γ where |Γ| ≤ 1).
     * This makes the logic paraconsistent: LNC (law of non-contradiction) is NOT a theorem.
     *
     * CRITICAL CORRECTION: LEM IS PROVABLE in LDJ.
     * This is the exact dual of LJ: where LJ fails to prove LEM, LDJ proves it.
     * Where LJ proves LNC, LDJ fails to prove it.
     *
     * The negation in LDJ is defined by:
     * - Double negation elimination (A ⊢ ¬¬A): present in LDJ
     * - Double negation introduction (¬¬A ⊢ A): absent in LDJ
     * This is symmetric to LJ which has introduction but not elimination.
     */
    public static LogicalSignature createLDJSignature() {
        // LDJ (Urbas-Rauszer dual of LJ): symmetric dual with left-side restriction
        Connective negation = new Connective(
            "¬", 1, "propositional",
            Arrays.asList(
                new InferenceRule("neg_left_ldj", "¬", "left",
                    Arrays.asList("⊢ A, Δ"),
                    "¬A ⊢ Δ",
                    "LDJ negation left intro: symmetric to LJ right intro")
            ),
            Arrays.asList(
                new InferenceRule("neg_right_ldj", "¬", "right",
                    Arrays.asList("A ⊢ Δ"),
                    "⊢ ¬A, Δ",
                    "LDJ negation right intro: symmetric to LJ left intro")
            ),
            "Paraconsistent negation: dual to intuitionistic, lacks LNC"
        );

        Connective conjunction = new Connective(
            "∧", 2, "propositional",
            Arrays.asList(
                new InferenceRule("and_left_ldj", "∧", "left",
                    Arrays.asList("A ⊢ Δ", "B ⊢ Δ"),
                    "A ∧ B ⊢ Δ",
                    "LDJ conjunction left intro: symmetric to LJ right")
            ),
            Arrays.asList(
                new InferenceRule("and_right_1_ldj", "∧", "right",
                    Arrays.asList("⊢ A, Δ"),
                    "⊢ A ∧ B, Δ",
                    "LDJ conjunction right intro 1"),
                new InferenceRule("and_right_2_ldj", "∧", "right",
                    Arrays.asList("⊢ B, Δ"),
                    "⊢ A ∧ B, Δ",
                    "LDJ conjunction right intro 2")
            ),
            "LDJ conjunction: symmetric to intuitionistic"
        );

        Connective disjunction = new Connective(
            "∨", 2, "propositional",
            Arrays.asList(
                new InferenceRule("or_left_1_ldj", "∨", "left",
                    Arrays.asList("A ⊢ Δ"),
                    "A ∨ B ⊢ Δ",
                    "LDJ disjunction left intro 1"),
                new InferenceRule("or_left_2_ldj", "∨", "left",
                    Arrays.asList("B ⊢ Δ"),
                    "A ∨ B ⊢ Δ",
                    "LDJ disjunction left intro 2")
            ),
            Arrays.asList(
                new InferenceRule("or_right_ldj", "∨", "right",
                    Arrays.asList("⊢ A, Δ", "⊢ B, Δ"),
                    "⊢ A ∨ B, Δ",
                    "LDJ disjunction right intro: symmetric to LJ left")
            ),
            "LDJ disjunction: symmetric to intuitionistic"
        );

        Connective implication = new Connective(
            "→", 2, "propositional",
            Arrays.asList(
                new InferenceRule("impl_left_ldj", "→", "left",
                    Arrays.asList("⊢ A, Δ", "B ⊢ Δ"),
                    "A → B ⊢ Δ",
                    "LDJ implication left intro")
            ),
            Arrays.asList(
                new InferenceRule("impl_right_ldj", "→", "right",
                    Arrays.asList("A ⊢ B, Δ"),
                    "⊢ A → B, Δ",
                    "LDJ implication right intro")
            ),
            "LDJ implication: symmetric to intuitionistic"
        );

        Set<Connective> connectives = new HashSet<>(Arrays.asList(
            negation, conjunction, disjunction, implication
        ));

        Set<StructuralRule> structuralRules = new HashSet<>(Arrays.asList(
            StructuralRule.WEAKENING,
            StructuralRule.CONTRACTION,
            StructuralRule.EXCHANGE
        ));

        return new LogicalSignature.Builder("LDJ", "Paraconsistent Dual of Intuitionistic (Urbas-Rauszer)")
            .addConnectives(connectives)
            .addStructuralRules(structuralRules)
            .functionallyComplete(false)
            .description("Urbas-Rauszer dual of LJ obtained by exchanging left and right. " +
                "Functionally incomplete, paraconsistent. DOES prove LEM but NOT LNC. " +
                "Lacks double negation introduction but has double negation elimination.")
            .build();
    }

    /**
     * Common Logic: Intersection of all four logics
     * Functionally incomplete: most restrictive logic
     * Node: <Consistent=false, Complete=false>
     *
     * The common logic is the intersection of LJ and its dual.
     * It has no structural rules (or very restricted rules).
     * This is the most conservative logic that can be embedded in all four.
     *
     * LEM is NOT a theorem (and also NOT refutable).
     * In the common logic, neither A nor ¬A can be proven for arbitrary A.
     */
    public static LogicalSignature createCommonLogicSignature() {
        // Minimalist connectives with no structural rules
        Connective negation = new Connective(
            "¬", 1, "propositional",
            Arrays.asList(
                new InferenceRule("neg_left_common", "¬", "left",
                    Arrays.asList(),  // No premise - minimal
                    "¬A ⊢",
                    "Common negation: no rules (maximal restriction)")
            ),
            Arrays.asList(
                new InferenceRule("neg_right_common", "¬", "right",
                    Arrays.asList(),
                    "⊢ ¬A",
                    "Common negation: no rules (maximal restriction)")
            ),
            "Common negation: empty signature (no derivations)"
        );

        Set<Connective> connectives = new HashSet<>(Arrays.asList(negation));
        Set<StructuralRule> structuralRules = new HashSet<>();  // No structural rules

        return new LogicalSignature.Builder("Common", "Common Logic (Intersection)")
            .addConnectives(connectives)
            .addStructuralRules(structuralRules)
            .functionallyComplete(false)
            .description("Common logic: intersection of LJ and its dual. " +
                "Paraconsistent and paracomplete. Neither LEM nor LNC is provable.")
            .build();
    }
}
