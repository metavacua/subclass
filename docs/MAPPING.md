# XML Specification ↔ Java Implementation Mapping

This document maps the formal specification (XML DocBook monograph in `docs/`) to the Java implementation (`src/`).

The SubClass project uses a **dual-model architecture**: the XML monograph is the authoritative specification, and the Java code is the formal implementation that witnesses the claims made in the specification.

## Foundational Layer

| XML Source | Java Implementation | Purpose |
|---|---|---|
| docs/part1/ProofTheory.xml § Introduction | src/main/java/org/subclass/logic/proof/typed/ | Core proof representation (phantom types, cardinality constraints) |
| docs/part1/ProofTheory.xml § Proof Structures | org.subclass.logic.proof.typed.Proof&lt;L,R&gt; | Typed proof representation with left/right cardinality |
| docs/part1/ProofTheory.xml § Sequent Calculus | org.subclass.logic.proof.typed.Sequent | Sequent structure (antecedent, succedent) |
| docs/part1/ProofTheory.xml § Cardinality Constraints | org.subclass.logic.proof.typed.{Zero, One, Many} | Phantom type cardinality markers |

## Rule Specifications

| XML Source | Java Implementation | Count |
|---|---|---|
| docs/part1/SequentCalculus.xml § Axioms | src/main/java/org/subclass/logic/rules/axiom/ | 1 rule (Identity Axiom) |
| docs/part1/SequentCalculus.xml § Logical Connectives | src/main/java/org/subclass/logic/rules/{conjunction,disjunction,implication,negation}/ | 12 rules |
| docs/part1/SequentCalculus.xml § Structural Rules | src/main/java/org/subclass/logic/rules/structural/ | 4+ rules |
| docs/part1/SequentCalculus.xml § Cut Rule | src/main/java/org/subclass/logic/rules/cut/ | 1 rule |

Each rule file contains:
- `@RuleSpec` annotation documenting the rule name, connective, and structural properties
- Javadoc explaining the rule's role in the calculus
- Implementation as a `ProofNode` record

## Theorem Validation

| XML Source | Java Implementation | Purpose |
|---|---|---|
| docs/part1/TheoremValidation.xml § Compile-Time Validation | org.subclass.processor.TheoremProcessor | @Theorem/@TheoremFamily annotation processor |
| docs/part1/TheoremValidation.xml § Unprovability Derivations | org.subclass.processor.AntitheoremProcessor | Framework for metalinguistic unprovability proofs (Γ ⊢ {}) |
| docs/part1/TheoremValidation.xml § Annotation System | org.subclass.annotation.{@Theorem, @TheoremFamily, @RuleSpec, @Antitheorem} | Annotations for proof declaration |

## Diamond Graph Validation

| XML Source | Java Implementation | Purpose |
|---|---|---|
| docs/part1/DiamondGraph.xml (or relevant section) | org.subclass.logic.tetragram.Tetragram | Diamond graph data structure |
| " | org.subclass.logic.tetragram.TheoremStatus | Provability status enum (PROVABLE, NON_PROVABLE, etc.) |
| " | org.subclass.examples.executable.LEMProofs | Law of Excluded Middle validator across 4 logical matrices |
| " | org.subclass.examples.executable.LNCProofs | Law of Non-Contradiction validator across 4 logical matrices |

These classes are **data model references**, not pedagogical examples. They validate that the Java type system correctly enforces the diamond graph's logical constraints.

## Serialization & I/O

| XML Source | Java Implementation | Purpose |
|---|---|---|
| docs/part1/Serialization.xml (or relevant) | org.subclass.logic.proof.io.ProofIO | Bidirectional mapping: Java Proof ↔ XML representation |
| " | org.subclass.logic.proof.io.* | JSON/XML/S-expression serialization drivers |
| " | org.subclass.logic.proof.check.ProofWalker | Runtime proof validation via reflection |

The ProofIO classes are the **bridge** between the XML specification notation and Java's object model.

## Logical Signatures

| XML Source | Java Implementation | Purpose |
|---|---|---|
| docs/part1/LogicalSignatures.xml (or relevant) | org.subclass.logic.signature.Logic | Logic interface defining connectives, axioms, units |
| " | org.subclass.logic.signature.Connective | Connective (AND, OR, NOT, IMPLIES, etc.) |
| " | org.subclass.logic.signature.* | Specific logic implementations (Classical, Intuitionistic, etc.) |

## Testing & Validation

| XML Source | Java Implementation | Purpose |
|---|---|---|
| docs/part1/ProofTheory.xml § Examples | src/test/java/org/subclass/examples/executable/ | Executable proof examples |
| " | src/test/java/org/subclass/logic/ | Rule and logic hierarchy tests |

## Service-Provider Interfaces (SPI)

| XML Source | Java Implementation | Purpose |
|---|---|---|
| docs/part1/Extensibility.xml (or relevant) | org.subclass.logic.rules.RuleDescriptor | SPI for runtime rule discovery |
| " | org.subclass.logic.signature.LogicRegistry | SPI for runtime logic discovery |

Service implementations are registered in `META-INF/services/`.

## How to Use This Mapping

### For Implementers
1. Find the specification section in docs/part1/
2. Locate the corresponding Java implementation using this table
3. Read the Java javadoc for implementation details
4. Return to the XML spec for the "why" and formal justification

### For Spec Writers
When adding new features to the monograph:
1. Add specification to docs/part1/
2. Reference the Java package where it's implemented using the pattern shown here
3. Update this MAPPING.md with the new row(s)

### For Readers
- **XML-first**: Start in the monograph (docs/), use this table to find implementations
- **Code-first**: Start in the Java source, find the corresponding spec section here, then read the monograph

## Future Sections

As the project expands (M2-M5 roadmap), this table will grow to include:
- Theory modules (docs/theories/) → Theory implementations
- Hierarchy encodings (docs/hierarchy/) → Encoding implementations
- Proof witnesses → Corresponding proof term generation
- Semantic web export → RDF/OWL generator implementations
