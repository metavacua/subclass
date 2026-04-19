package org.subclass.processor;

import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.signature.Logic;

/**
 * Framework for validating metalinguistic unprovability derivations.
 *
 * AntitheoremProcessor handles derivations of the form Γ ⊢ {} (empty consequent),
 * grounding them in the reflexive axiom schemas of specific logics.
 *
 * Core responsibilities:
 * 1. Verify unprovability derivations connect to the axiom schema of the target logic
 * 2. Distinguish unprovability (intuitionistic side) from refutability (classical side)
 * 3. Validate diamond graph constraints: ensure connectives (negation, implication, junctions)
 *    have correct behavior across all four nodes (classical, initial, left, right)
 * 4. Enforce the initial node property: intersection of intuitionistic AND dual-intuitionistic,
 *    where certain connectives must be non-functional
 *
 * Diamond Graph Requirements (validated by implementations):
 * - Terminal (classical): LEM provable, LNC provable, all connectives functional
 * - Initial (intuitionistic ∩ dual-intuitionistic): LEM unprovable, LNC unprovable,
 *   negation/implication/non-implication non-functional
 * - Right node: One of LEM/LNC provable, the other not
 * - Left node: One of LEM/LNC provable, the other not
 *
 * @see Antitheorem
 * @see org.subclass.examples.executable.LEMProofs (data model validator)
 * @see org.subclass.examples.executable.LNCProofs (data model validator)
 */
public abstract class AntitheoremProcessor {

    /**
     * Validates an unprovability derivation against the reflexive axiom schema of a logic.
     *
     * TODO: Implement for each logic:
     * - Classical logic: Validate refutability (¬Γ) using classical axiom schema
     * - Intuitionistic logic: Validate non-provability (Γ ⊬) using intuitionistic axiom schema
     * - Dual-intuitionistic logic: Validate non-refutability using dual axiom schema
     * - Initial node: Validate as intersection (both intuitionistic AND dual-intuitionistic must fail)
     *
     * @param antitheoremMethod method annotated with {@link Antitheorem}
     * @param logic target logic for validation
     * @return validator that can check if the unprovability derivation is valid for this logic
     */
    public abstract AntitheoremValidator validatorFor(java.lang.reflect.Method antitheoremMethod, Logic logic);

    /**
     * Validates diamond graph constraints for a theorem family.
     *
     * TODO: Implement to ensure:
     * 1. LEM provability status matches diamond graph node:
     *    - Terminal: provable
     *    - Initial: unprovable
     *    - Right/Left: one of {provable, non-provable}
     * 2. LNC provability status is dual to LEM across nodes
     * 3. Connective behavior (negation, implication, junctions) is consistent
     *    with each node's axiom schemas
     *
     * @param connective the connective to validate (e.g., AND, OR, NOT, IMPLIES)
     * @return constraint validator
     */
    public abstract ConnectiveValidator validateConnective(String connective);

    /**
     * Validates that structural rules maintain diamond graph invariants.
     *
     * TODO: Implement to ensure:
     * 1. Exchange rules are valid (formula order preservation depends on logic)
     * 2. Weakening rules are valid (depends on reflexive axiom schema)
     * 3. Contraction rules are valid (depends on units available in each node)
     * 4. Cut elimination (when applicable) preserves diamond graph structure
     *
     * @param ruleClass the structural rule to validate
     * @return true if rule is valid for the diamond graph
     */
    public abstract boolean validateStructuralRule(Class<?> ruleClass);
}

/**
 * Validator for an unprovability derivation in a specific logic.
 *
 * TODO: Concrete implementations should:
 * 1. Track the derivation from Γ ⊢ {} back to the reflexive axiom schema
 * 2. Verify no "provable" formulas appear in Γ (consistency check)
 * 3. Report which axiom schema grounds the unprovability
 */
interface AntitheoremValidator {
    /**
     * Check if a proof of type Proof<L,R> correctly represents
     * the unprovability claim grounded in its axiom schema.
     *
     * TODO: Implement for each axiom schema type.
     *
     * @return true if the proof correctly represents unprovability
     */
    boolean isValid(Proof<?, ?> candidate);

    /**
     * Explain the unprovability derivation in human-readable form.
     *
     * TODO: Generate explanation of the form "Γ ⊢ {} is unprovable in <logic>
     * because <axiom-schema> is not present in this logic's signature."
     *
     * @return explanation string
     */
    String explain();
}

/**
 * Validator for connective behavior across diamond graph nodes.
 *
 * TODO: Implement to check:
 * 1. Negation: must fail in initial node, functional elsewhere
 * 2. Implication: must fail in initial node, functional elsewhere
 * 3. Non-implication: must fail in initial node, functional elsewhere
 * 4. Junctions (AND, OR): behavior depends on units and reflexive axioms
 */
interface ConnectiveValidator {
    /**
     * Check if connective has correct status in the given node.
     *
     * TODO: Implement truth tables or proof obligation checks
     * per node's axiom schema.
     *
     * @param node one of "classical", "intuitionistic", "dual-intuitionistic", "initial"
     * @return true if connective has correct behavior in this node
     */
    boolean isValidInNode(String node);
}
