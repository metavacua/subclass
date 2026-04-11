# SubClass

## Generalized Undecidable Theories: Beyond the Classical Hierarchy

### Abstract

This monograph extends the foundational work of Tarski, Mostowski, and Robinson on undecidable theories to the full class of first-order theories formalizable in logics compatible with classical first-order predicate logic (CFOPL). While Tarski's framework exhausts the space of classical first-order theories within a Boolean metatheory, we demonstrate that the interpretability relation is fundamentally asymmetric: every classical theory admits faithful embedding into substructural extensions such as First-order Additive Linear Logic (FALL), yet there exist first-order theories in FALL that resist classical interpretation due to their paraconsistent and paracomplete features.

We introduce the **Hierarchy of Infinite Regrets**—the infinite regress of classical set theories (ZF, NBG, MK, etc.) that approach but never achieve totality—and prove an independence result establishing that certain first-order theories exist in non-Boolean semantic frameworks (complete lattices, phase spaces) that have no counterpart in CFOPL. These theories inhabit the gaps between classical consistency and inconsistency, admitting both constructive truth and constructive falsity (bi-constructive theories) while escaping Tarski's original classification of axiomatizability, decidability, and essential undecidability.

Our main theorem establishes that the set of first-order theories $\mathrm{Th}(\mathcal{L})$ for any logic $\mathcal{L}$ strictly weaker than CFOPL syntactically but semantically richer (non-Boolean) properly extends $\mathrm{Th}(\text{CFOPL})$, yielding new primitive classes of essentially undecidable theories that are neither recursively axiomatizable in the classical sense nor interpretable as classical theories. This generalization reveals that the "undecidable" is not merely a property of theories within a fixed logical framework, but a relational feature between metatheories, with the classical Boolean framework representing only the shadow of a larger lattice-theoretic universe of first-order theories.

---

## Overview

**SubClass** is the accompanying formalization and reference implementation for the monograph above. It provides:

- Formal definitions of the **Hierarchy of Infinite Regrets** and its limit structures
- Mechanized proofs of the main independence and non-interpretability results
- A library of bi-constructive first-order theories with non-Boolean semantics
- Decision procedures and undecidability witnesses for the new theory classes

## Key Concepts

| Concept | Description |
|---|---|
| CFOPL | Classical First-Order Predicate Logic — the standard Boolean metatheory of Tarski et al. |
| FALL | First-order Additive Linear Logic — a substructural extension with phase-space semantics |
| Bi-constructive theory | A theory admitting both constructive truth and constructive falsity; escapes classical decidability classification |
| Hierarchy of Infinite Regrets | The infinite regress ZF ⊂ NBG ⊂ MK ⊂ … of classical set theories that approach but never achieve totality |

## Main Theorem

For any logic $\mathcal{L}$ that is syntactically strictly weaker than CFOPL but semantically richer (non-Boolean):

$$\mathrm{Th}(\text{CFOPL}) \subsetneq \mathrm{Th}(\mathcal{L})$$

The gap $\mathrm{Th}(\mathcal{L}) \setminus \mathrm{Th}(\text{CFOPL})$ is non-empty and contains primitive classes of essentially undecidable theories with no classical counterpart.

## Structure

```
subclass/
├── theories/        # Formal theory definitions (FALL, bi-constructive, phase-space)
├── hierarchy/       # Hierarchy of Infinite Regrets construction and proofs
├── independence/    # Independence and non-interpretability results
└── decidability/    # Decision procedures and undecidability witnesses
```

## Background

This work builds on:

- Tarski, Mostowski & Robinson, *Undecidable Theories* (1953)
- Girard, *Linear Logic* (1987)
- Bell & Machover, *A Course in Mathematical Logic* (1977)

The central insight is that "undecidability" is not an intrinsic property of a theory but a **relational feature between metatheories**. The Boolean framework of classical logic represents only the shadow of a larger lattice-theoretic universe of first-order theories.
