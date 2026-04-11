# SubClass

> A Java formalization library and DocBook XML monograph extending Tarski's undecidability results beyond classical Boolean logic into the lattice-theoretic universe of first-order theories.

![Status](https://img.shields.io/badge/status-pre--alpha%20%E2%80%93%20no%20source%20yet-red)
![License (code)](https://img.shields.io/badge/code%20license-AGPL%20v3-blue)
![License (content)](https://img.shields.io/badge/content%20license-CC--SA%204.0%20International-lightgrey)

---

## Table of Contents

1. [What is SubClass?](#what-is-subclass)
2. [Mathematical Background](#mathematical-background)
3. [Main Theorem](#main-theorem)
4. [Key Concepts](#key-concepts)
5. [Project Status and Roadmap](#project-status-and-roadmap)
6. [Repository Structure](#repository-structure)
7. [Documentation Architecture](#documentation-architecture)
8. [Prerequisites](#prerequisites)
9. [Building from Source](#building-from-source)
10. [Contributing](#contributing)
11. [License](#license)
12. [References](#references)

---

## What is SubClass?

SubClass is simultaneously a scholarly monograph and a Java software library. The monograph develops new theory in mathematical logic; the library mechanizes and operationalizes that theory as executable Java code. Both are developed together in this repository.

The monograph is authored in **DocBook XML** using an atomized, token-limited modular structure: each paragraph or section lives in its own file, keeping individual documents small enough for LLM-assisted incremental drafting and enabling automated translation into LaTeX, XHTML, semantic web formats, LISP, Java, Python, and other targets.

The library provides:

- Formal definitions of the **Hierarchy of Infinite Regrets** and its limit structures
- Mechanized proofs of the main independence and non-interpretability results
- A library of bi-constructive first-order theories with non-Boolean semantics
- Decision procedures and undecidability witnesses for the new theory classes
- An executable test suite demonstrating undecidability witnesses on concrete theory instances

---

## Mathematical Background

### The Classical Picture (Tarski, 1953)

Tarski, Mostowski, and Robinson established that many classical first-order theories are undecidable, and classified theories by axiomatizability and mutual interpretability within a fixed Boolean metatheory. Their framework is exhaustive within the Boolean setting: every classical first-order theory fits somewhere in their hierarchy.

### The Gap This Work Addresses

Classical logic is Boolean; there exist non-Boolean logics — linear, paraconsistent, phase-space — where the landscape of first-order theories is strictly larger.

### Abstract

This monograph extends the foundational work of Tarski, Mostowski, and Robinson on undecidable theories to the full class of first-order theories formalizable in logics compatible with classical first-order predicate logic (CFOPL). While Tarski's framework exhausts the space of classical first-order theories within a Boolean metatheory, we demonstrate that the interpretability relation is fundamentally asymmetric: every classical first order theory is a theoretical extension of a substructural or non-structural logic such as First-order Additive Linear Logic (FALL), yet there exist first-order theories formalizable in FALL that resist classical interpretation due to their paraconsistent or paracomplete features.

We introduce the **Hierarchy of Infinite Regrets**—the infinite regress of classical set theories (ZF, NBG, MK, etc.) that approach but never achieve totality—and prove an independence result establishing that certain first-order theories exist in non-Boolean semantic frameworks (complete lattices, phase spaces) that have no counterpart in CFOPL. These theories inhabit the gaps between classical consistency and inconsistency or triviality and non-triviality, admitting both constructive truth and constructive falsity (bi-constructive theories) while escaping Tarski's original classification of axiomatizability, decidability, and essential undecidability.

Our main theorem establishes that the set of first-order theories $\mathrm{Th}(\mathcal{L})$ for any logic $\mathcal{L}$ strictly weaker than CFOPL syntactically but semantically richer (non-Boolean) properly extends $\mathrm{Th}(\text{CFOPL})$.

---

## Main Theorem

Informally: the universe of first-order theories is strictly larger when you drop the Boolean requirement on the metatheory.

For any logic $\mathcal{L}$ that is syntactically strictly weaker than CFOPL but semantically richer (non-Boolean):

$$\mathrm{Th}(\text{CFOPL}) \subsetneq \mathrm{Th}(\mathcal{L})$$

The gap $\mathrm{Th}(\mathcal{L}) \setminus \mathrm{Th}(\text{CFOPL})$ is non-empty and contains primitive classes of essentially undecidable theories with no classical counterpart.

---

## Key Concepts

The following concepts appear throughout the codebase and the accompanying monograph. Each maps to a module in the library.

| Concept | Description |
|---|---|
| CFOPL | Classical First-Order Predicate Logic — the standard Boolean metatheory of Tarski et al. |
| FALL | First-order Additive Linear Logic — a substructural extension with phase-space semantics |
| Bi-constructive theory | A theory admitting both constructive truth and constructive falsity; escapes classical decidability classification |
| Hierarchy of Infinite Regrets | The infinite regress ZF ⊂ NBG ⊂ MK ⊂ … of classical set theories that approach but never achieve totality |
| Phase space | The Girard semantics structure (a closure system on a commutative monoid) providing the truth values for FALL |

---

## Project Status and Roadmap

> [!NOTE]
> This repository is in the monograph-drafting and architecture phase. No Java source files or DocBook XML modules exist yet. The directory structure described below is planned, not present.

- [x] Repository initialized
- [x] Mathematical foundations documented in README
- [ ] `pom.xml` with initial dependencies
- [ ] DocBook XML schema and atomized module skeleton
- [ ] Core Java interfaces: `LogicSignature`, `Theory`, `Interpretation`

**Milestone 1 — DocBook XML Monograph Skeleton**
- [ ] `docs/` directory with exc-c14n XML schema configuration
- [ ] Atomized module files for abstract and introduction (one paragraph per file)
- [ ] XSLT stylesheet for LaTeX output
- [ ] XSLT stylesheet for XHTML output

**Milestone 2 — Core Theory Library**
- [ ] `theories/` module: FALL theory encoder
- [ ] `theories/` module: bi-constructive theory class
- [ ] `theories/` module: phase-space semantics evaluator
- [ ] Unit tests for theory inclusion/exclusion

**Milestone 3 — Hierarchy Module**
- [ ] `hierarchy/` module: ZF, NBG, MK encodings
- [ ] Limit structure construction
- [ ] Independence proof witnesses (computational)

**Milestone 4 — Decidability Module**
- [ ] `decidability/` module: decision procedures for classical fragment
- [ ] Undecidability witnesses for new theory classes
- [ ] Benchmarks

**Milestone 5 — Multi-Format Publication**
- [ ] Semantic web export (RDF/OWL) from DocBook XML
- [ ] LISP and Python bindings generated from the data model
- [ ] GitHub Pages rendering of full monograph

---

## Repository Structure

The following directory layout is planned. Only the repository root and `.gitignore` currently exist.

```
subclass/
├── docs/                          # Monograph — atomized DocBook XML modules
│   ├── schema/                    # exc-c14n XML schema and catalog
│   ├── abstract/                  # One .xml file per paragraph of the abstract
│   ├── introduction/
│   ├── hierarchy-of-regrets/
│   ├── main-theorem/
│   └── xslt/                      # Output stylesheets (LaTeX, XHTML, RDF)
├── src/
│   └── main/
│       └── java/
│           └── io/github/metavacua/subclass/
│               ├── theories/      # FALL, bi-constructive, phase-space
│               ├── hierarchy/     # Hierarchy of Infinite Regrets
│               ├── independence/  # Non-interpretability results
│               └── decidability/  # Decision procedures and witnesses
├── pom.xml
└── README.md
```

---

## Documentation Architecture

The monograph is authored in **DocBook 5** XML using **Exclusive Canonicalization (exc-c14n)**, making each document fragment canonically serializable and diff-friendly.

### Atomized Modular Structure

Each conceptual unit — paragraph, definition, theorem, proof step — lives in its own small XML file. Files are sized to stay within a conservative token budget (roughly 500–1000 tokens each), so that an LLM can load, draft, revise, or translate a single module without needing the full document in context.

```
docs/abstract/
├── 001-tarski-extension.xml       # ≈ paragraph 1 of the abstract
├── 002-interpretability.xml       # ≈ paragraph 2
└── 003-main-theorem-statement.xml # ≈ paragraph 3
```

### Why XML?

XML separates content from presentation. A single corpus of DocBook XML modules can be automatically transformed into:

| Target | Toolchain |
|---|---|
| LaTeX / PDF | XSLT → `dblatex` or `xelatex` |
| XHTML web pages | XSLT → DocBook XHTML stylesheets |
| Semantic web (RDF/OWL) | Custom XSLT or JAXB binding |
| Java interfaces | JAXB schema compilation from DocBook subset |
| Python data classes | `xsdata` or `generateDS` from schema |
| LISP s-expressions | Custom XSLT |

### Exclusive Canonicalization

exc-c14n (defined in [W3C Exclusive XML Canonicalization](https://www.w3.org/TR/xml-exc-c14n/)) ensures that XML fragments have a stable, byte-for-byte canonical form independent of namespace declarations in enclosing documents. This makes individual module files safe for cryptographic signing, content-addressable storage, and reproducible builds.

---

## Prerequisites

- **Java 17 or later** — tested on OpenJDK 17 and 21
- **Apache Maven 3.9+** — or use the Maven Wrapper (`./mvnw`) bundled in the repository
- **Git**
- **DocBook/XSLT toolchain** (for monograph rendering) — Saxon-HE 12+ recommended; `dblatex` for PDF output
- **IDE** (optional) — IntelliJ IDEA or Eclipse with m2e plugin

---

## Building from Source

Clone the repository:

```bash
git clone https://github.com/metavacua/subclass.git
cd subclass
```

Build with the Maven Wrapper:

```bash
./mvnw clean verify
```

Run the test suite:

```bash
./mvnw test
```

> [!NOTE]
> As of the current pre-alpha stage no source files exist and the `pom.xml` has not been created. The build instructions above describe the intended workflow and will be updated as development progresses.

---

## Contributing

Contributions are closed at this time; any contributions require an explicit written contract to protect contributor and repository intellectual property rights.

Formal contribution guidelines (`CONTRIBUTING.md`) and a code of conduct will be added before Milestone 2. In the meantime, use the [Issues tracker](https://github.com/metavacua/subclass/issues) for all contributions and discussion.

---

## License

**Software** (all Java source code, build scripts, and XSLT stylesheets in this repository):
Licensed under the **GNU Affero General Public License v3.0 (AGPL v3)**. See `LICENSE` (to be added).

**Monograph content** (all DocBook XML files in `docs/` and the text of this README):
Licensed under **Creative Commons Attribution-ShareAlike 4.0 International (CC-SA 4.0)**. See `LICENSE-content` (to be added).

---

## References

- Tarski, A., Mostowski, A., & Robinson, R.M., *Undecidable Theories*. North-Holland, 1953.
- Girard, J.-Y., "Linear Logic." *Theoretical Computer Science* 50(1):1–101, 1987.
- Bell, J.L. & Machover, M., *A Course in Mathematical Logic*. North-Holland, 1977.
- Avron, A., "The semantics and proof theory of linear logic." *Theoretical Computer Science* 57(2–3):161–184, 1988.
- Troelstra, A.S., *Lectures on Linear Logic*. CSLI Lecture Notes 29, Stanford, 1992.
- Visser, A., "Categories of theories and interpretations." In *Logic in Tehran*, Lecture Notes in Logic 26, ASL, 2006.
- Restall, G., *An Introduction to Substructural Logics*. Routledge, 2000.
