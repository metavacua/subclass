package org.subclass.examples;

import org.subclass.annotation.DiamondGraph;
import org.subclass.annotation.DiamondGraphNode;
import org.subclass.annotation.TheoremStatusDerivation;

/**
 * Declarative specification of the Law of Excluded Middle (LEM) tetragram
 * using the @DiamondGraph annotation framework.
 *
 * This demonstrates the auto-derivation system:
 * - The @DiamondGraph annotation specifies all four logic nodes declaratively
 * - The DiamondGraphProcessor auto-validates the graph structure
 * - The TheoremStatusDeriver automatically derives LEM's status in each logic
 * - Compile-time warnings if derived status ≠ expected status
 *
 * Comparison to LEMTetragram.java (hand-coded):
 * - LEMTetragram: Hand-wrote 4 @Theorem methods + 1 @TheoremFamily
 * - LEMDiamondGraphExample: Declarative @DiamondGraph + auto-derivation
 * - Eliminates redundancy; derive statuses algorithmically from graph structure
 */
@DiamondGraph(
    name = "LEM",
    displayName = "Law of Excluded Middle",
    description = "Complete diamond graph specification for LEM (⊢ A∨¬A). " +
                  "Shows how LEM is provable in classical and paraconsistent logics " +
                  "but not in intuitionistic logic.",

    // Classical Logic Node: (Many, Many)
    // Most permissive: allows context on both sides
    // LEM provable via axiom schema Γ,A⊢A,Δ and right disjunction rule
    classicalNode = @DiamondGraphNode(
        position = "classical",
        antecedentCardinality = "Many",
        succedentCardinality = "Many",
        axiomSchema = "Γ,A⊢A,Δ",
        structuralRules = {"W", "C", "E"},
        connectives = {"∧", "∨", "¬", "→"},
        functionallyComplete = true
    ),

    // Intuitionistic Logic Node: (Many, One)
    // Restricted right: succedent limited to single formula
    // LEM not provable: cannot close ⊢A∨¬A from empty antecedent
    intuitionisticNode = @DiamondGraphNode(
        position = "intuitionistic",
        antecedentCardinality = "Many",
        succedentCardinality = "One",
        axiomSchema = "Γ,A⊢A",
        structuralRules = {"W", "C", "E"},
        connectives = {"∧", "∨", "¬", "→"},
        functionallyComplete = false
    ),

    // Paraconsistent Logic Node: (One, Many)
    // Restricted left (dual to intuitionistic): antecedent limited to single formula
    // LEM provable: dual of intuitionistic restriction
    // Can derive A∨¬A⊢ from axiom schema, thus ⊢A∨¬A is dual-provable
    paraconsistentNode = @DiamondGraphNode(
        position = "paraconsistent",
        antecedentCardinality = "One",
        succedentCardinality = "Many",
        axiomSchema = "A⊢A,Δ",
        structuralRules = {"W", "C", "E"},
        connectives = {"∧", "∨", "¬", "→"},
        functionallyComplete = false
    ),

    // Common Logic Node: (One, One)
    // Most restrictive: both sides limited to single formula
    // LEM not provable: intersection of intuitionistic and paraconsistent restrictions
    commonLogicNode = @DiamondGraphNode(
        position = "common",
        antecedentCardinality = "One",
        succedentCardinality = "One",
        axiomSchema = "A⊢A",
        structuralRules = {},
        connectives = {"∧", "∨", "¬", "→"},
        functionallyComplete = false
    ),

    // Theorem status derivations: specify expected statuses for validation
    // The processor will auto-derive these and warn if mismatches occur
    theoremDerivations = {
        @TheoremStatusDerivation(
            theoremName = "LEM",
            classicalStatus = "PROVABLE",
            classicalReasoning = "Axiom schema Γ,A⊢A,Δ with right disjunction enables closure",

            intuitionisticStatus = "NON_PROVABLE",
            intuitionisticReasoning = "Succedent restricted to one; cannot close ⊢A∨¬A",

            paraconsistentStatus = "PROVABLE",
            paraconsistentReasoning = "Dual of intuitionistic; A∨¬A⊢ derivable",

            commonLogicStatus = "NON_PROVABLE",
            commonLogicReasoning = "Intersection too restrictive; axiom schema A⊢A only"
        ),
        @TheoremStatusDerivation(
            theoremName = "LNC",
            classicalStatus = "PROVABLE",
            intuitionisticStatus = "PROVABLE",
            paraconsistentStatus = "NON_PROVABLE",
            commonLogicStatus = "NON_PROVABLE"
        )
    }
)
public class LEMDiamondGraphExample {

    // Marker class: no actual methods needed.
    // The @DiamondGraph annotation is sufficient for the processor to:
    // 1. Parse the graph specification
    // 2. Validate the cardinality hierarchy
    // 3. Auto-derive theorem statuses
    // 4. Compare against expected values (warnings for mismatches)

    // In a full system, developers would write @Theorem and @Antitheorem methods here,
    // and the type system (Proof<L,R>) would enforce cardinality constraints.
}
