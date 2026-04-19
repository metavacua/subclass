# AntitheoremProcessor Framework Design

## Overview

The AntitheoremProcessor framework handles **unprovability derivations** — metalinguistic proofs of the form Γ ⊢ {} (empty consequent) — that ground in the reflexive axiom schemas of specific logics.

## Motivation

The current implementation of LEMProofs/LNCProofs contains stub methods that return `null` or throw `UnsupportedOperationException`. These are not intentional design choices, but rather **placeholders for incomplete architecture**.

These classes serve as **diamond graph validators**: they verify that the type system correctly enforces the provability constraints of the four logical matrices:

- **Terminal node (classical logic, LK)**: LEM provable, LNC provable
- **Initial node (intuitionistic ∩ dual-intuitionistic)**: LEM unprovable, LNC unprovable
- **Right node (paraconsistent, LDJ)**: One provable, the other not
- **Left node (intuitionistic, LJ)**: One provable, the other not

The framework needs to be completed so that these validators can prove their claims by grounding unprovability derivations in axiom schemas.

## Core Concepts

### Unprovability Derivations

An unprovability derivation is a metalinguistic proof of the form:

```
Γ ⊢ {}
```

This reads: "From hypotheses Γ, we derive an empty consequent", which means "Γ cannot prove anything" or more precisely "no formula can be derived from Γ in this logic".

### Grounding in Axiom Schemas

A derivation Γ ⊢ {} is valid in a logic L if and only if it grounds in L's **reflexive axiom schema**. Different logics have different axiom schemas:

- **Classical logic (LK)**: Law of Excluded Middle (LEM), Law of Non-Contradiction (LNC), etc.
- **Intuitionistic logic (LJ)**: Intuitionistic axioms (no LEM, no LNC)
- **Dual-intuitionistic logic (LDJ)**: Dual axioms (no LNC, but has weaker forms)
- **Initial node (intersection)**: Only axioms present in BOTH intuitionistic AND dual-intuitionistic

### Diamond Graph Constraints

The diamond graph constrains how connectives (negation, implication, junctions) behave across the four nodes:

| Connective | Classical | Intuitionistic | Paraconsistent | Common |
|------------|-----------|---|---|---|
| Negation   | ✓ | ✓ | ✓ | ✗ |
| Implication| ✓ | ✓ | ✗ | ✗ |
| Conjunction| ✓ | ✓ | ✓ | ✓ |
| Disjunction| ✓ | ✓ | ✓ | ✓ |

Where:
- ✓ = connective is functional (behaves as expected)
- ✗ = connective is non-functional (must fail or have limited behavior)

The initial node (intersection) requires that certain connectives fail because they are not available in both intuitionistic AND dual-intuitionistic logic simultaneously.

## Implementation Requirements

### 1. AntitheoremValidator Interface

**Purpose**: Validates unprovability derivations in a specific logic.

```java
interface AntitheoremValidator {
    /**
     * Check if a proof correctly represents unprovability grounded in this logic's axiom schema.
     */
    boolean isValid(Proof<?, ?> candidate);

    /**
     * Explain the unprovability derivation, referencing the axiom schema.
     */
    String explain();
}
```

**Concrete implementations needed**:
1. **ClassicalAntitheoremValidator**: Validates refutability (¬Γ) using classical axiom schema
   - Check: Does ¬Γ contain all classical axioms? (LEM, LNC, etc.)
   - Grounding: Trace derivation to reflexive axiom schema
   
2. **IntuitionisticAntitheoremValidator**: Validates non-provability (Γ ⊬) using intuitionistic axiom schema
   - Check: Does Γ avoid classical axioms? (No LEM, no LNC)
   - Grounding: Trace derivation to intuitionistic axiom schema
   
3. **DualIntuitionisticAntitheoremValidator**: Validates non-refutability using dual axioms
   - Check: Does ¬Γ avoid dual axioms?
   - Grounding: Trace derivation to dual axiom schema
   
4. **InitialNodeAntitheoremValidator**: Validates the intersection property
   - Check: Both intuitionistic AND dual-intuitionistic constraints must hold
   - This ensures LEM and LNC fail in the initial node

### 2. ConnectiveValidator Interface

**Purpose**: Validates that connectives have correct behavior across nodes.

```java
interface ConnectiveValidator {
    /**
     * Check if connective has correct status in a given node.
     */
    boolean isValidInNode(String node);
}
```

**For each connective** (Negation, Implication, Non-implication, And, Or):
- Verify truth table matches node's axiom schema
- Verify proof obligations are satisfiable or unsatisfiable as expected
- Check that structural rules (exchange, weakening, contraction) apply correctly

### 3. Structural Rule Validation

**Purpose**: Ensure structural rules maintain diamond graph invariants.

**Rules to validate**:
1. **Exchange (permutation)**: Valid in all nodes (formula order doesn't matter)
2. **Weakening (thinning)**: Valid in all nodes (adding hypotheses preserves provability)
3. **Contraction**: Valid in classical and intuitionistic; restricted in paraconsistent
4. **Cut elimination**: Preserves diamond graph structure when applicable

## Implementation Plan

### Phase A: Design (Complete)
- Define AntitheoremValidator, ConnectiveValidator interfaces ✓
- Specify diamond graph constraints ✓
- Document axiom schemas for each logic ✓

### Phase B: Annotation Support (TODO)
1. Implement @Antitheorem annotation processor in TheoremProcessor
2. Scan for methods annotated with @Antitheorem
3. Route to appropriate AntitheoremValidator based on logic

### Phase C: Core Validators (TODO)
1. Implement ClassicalAntitheoremValidator
   - Validate refutability derivations
   - Check classical axioms are grounded
2. Implement IntuitionisticAntitheoremValidator
   - Validate non-provability
   - Check no classical axioms used
3. Implement DualIntuitionisticAntitheoremValidator
   - Validate non-refutability
   - Check dual axiom schema

### Phase D: Diamond Graph Validation (TODO)
1. Implement ConnectiveValidator for each connective
2. Implement InitialNodeAntitheoremValidator (intersection)
3. Validate structural rules

### Phase E: Integration with LEMProofs/LNCProofs (TODO)
1. Add AntitheoremProcessor as dependency in TheoremProcessor
2. When processing LEMProofs/LNCProofs, route unprovable cases to AntitheoremValidator
3. Validate that null returns / UnsupportedOperationException matches validator claims

## Axiom Schemas Reference

### Classical Logic (LK)

**Axioms**: {LEM, LNC, Double Negation Elimination (DNE), ...}

- `LEM`: Γ ⊢ Δ ∪ {A ∨ ¬A}
- `LNC`: Γ ⊢ Δ ∪ {¬(A ∧ ¬A)}`
- `DNE`: ¬¬A ⊢ A

All connectives functional. Unrestricted sequent structure.

### Intuitionistic Logic (LJ)

**Axioms**: {Intuitionistic axioms only, no LEM, no LNC, ...}

- No LEM
- No LNC  
- Successors restricted to single formula (Γ ⊢ A)
- No DNE in general

Negation and implication functional. Disjunction more limited.

### Dual-Intuitionistic Logic (LDJ)

**Axioms**: {Dual axioms, no LNC, weaker forms, ...}

- Has LEM
- No LNC
- Antecedents restricted (A ⊢ Δ)

Negation functional (in limited form). Implication non-functional.

### Initial Node (Intuitionistic ∩ Dual-Intuitionistic)

**Axioms**: {Axioms in BOTH LJ AND LDJ}

- No LEM (intuitionistic forbids it)
- No LNC (dual-intuitionistic forbids it)
- Most connectives non-functional
- Maximally restrictive

This is the "intersection" node that validates the diamond graph's minimal element.

## Testing Strategy

1. **LEMProofs validator**: LEM should be provable in {LK, LDJ}, unprovable in {LJ, initial}
2. **LNCProofs validator**: LNC should be provable in {LK, LJ}, unprovable in {LDJ, initial}
3. **Duality check**: Where LEM provable ↔ LNC unprovable (in left/right nodes)
4. **Connective matrix**: Verify all connectives have correct status per node

## References

- **Diamond graph**: Part 1, Section X (Tetragram structure)
- **Axiom schemas**: Part 1, Section Y (Reflexive axiom schemas)
- **Sequent calculus**: Part 1, Section Z (Rule formulation)
- **LEMProofs/LNCProofs**: src/test/java/org/subclass/examples/executable/

## Future Work

- [ ] Auto-generation of AntitheoremValidator stubs per logic
- [ ] Proof visualization for unprovability derivations
- [ ] Constraint solver for determining connective status
- [ ] Automatic diamond graph validation in CI/CD
