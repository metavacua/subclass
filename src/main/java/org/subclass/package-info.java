/**
 * SubClass: Formal proof library implementing Tarski's undecidability results beyond Boolean logic.
 *
 * <h2>Architecture Overview</h2>
 *
 * SubClass is a <strong>dual-model system</strong> where the XML monograph in {@code docs/} is the
 * authoritative mathematical specification, and this Java codebase is its formal implementation.
 * Both models are logically interdependent: neither stands alone.
 *
 * <h3>Layer 1: Core Proof Model ({@link org.subclass.logic.proof.typed})</h3>
 * <ul>
 *   <li>{@link org.subclass.logic.proof.typed.Proof Proof&lt;L,R&gt;}: Typed proof with phantom-type
 *       cardinality constraints (Many, One, or Zero formulas on left/right)</li>
 *   <li>{@link org.subclass.logic.proof.typed.ProofNode}: Record interface for reified sequent-calculus rules</li>
 *   <li>{@link org.subclass.logic.proof.typed.Sequent}: Antecedent/succedent pair with formula lists</li>
 *   <li>Implements: docs/part1/ProofTheory.xml</li>
 * </ul>
 *
 * <h3>Layer 2: Sequent Calculus Rules ({@link org.subclass.logic.rules})</h3>
 * <ul>
 *   <li>18+ reified rule implementations (Axiom, AndLeft, AndRight, ...)</li>
 *   <li>Each marked with {@link org.subclass.annotation.RuleSpec @RuleSpec} metadata</li>
 *   <li>Discovered at runtime via {@link java.util.ServiceLoader} (META-INF/services/RuleDescriptor)</li>
 *   <li>Implements: docs/part1/SequentCalculus.xml</li>
 * </ul>
 *
 * <h3>Layer 3: Logical Signatures ({@link org.subclass.logic.signature})</h3>
 * <ul>
 *   <li>{@link org.subclass.logic.signature.Logic}: Interface for logical matrices (classical, intuitionistic, etc.)</li>
 *   <li>{@link org.subclass.logic.signature.Connective}: Interface for logical connectives (AND, OR, NOT, ...)</li>
 *   <li>Specifies axiom schemas, units, and operational properties for each logic</li>
 *   <li>Implements: docs/part1/LogicalSignatures.xml</li>
 * </ul>
 *
 * <h3>Layer 4: Compile-Time Validation ({@link org.subclass.processor})</h3>
 * <ul>
 *   <li>{@link org.subclass.processor.TheoremProcessor}: Validates {@code @Theorem} and {@code @TheoremFamily}
 *       annotations at compile time</li>
 *   <li>{@link org.subclass.processor.AntitheoremProcessor}: Framework for metalinguistic unprovability derivations
 *       (Γ ⊢ {})</li>
 *   <li>Ensures type system enforces proof validity before runtime</li>
 *   <li>Implements: docs/part1/TheoremValidation.xml</li>
 * </ul>
 *
 * <h3>Layer 5: Diamond Graph Validation ({@link org.subclass.logic.tetragram})</h3>
 * <ul>
 *   <li>{@link org.subclass.logic.tetragram.Tetragram}: Diamond structure with 4 nodes
 *       (classical, intuitionistic, paraconsistent, common)</li>
 *   <li>{@link org.subclass.examples.executable.LEMProofs}: Data model validator for Law of Excluded Middle</li>
 *   <li>{@link org.subclass.examples.executable.LNCProofs}: Data model validator for Law of Non-Contradiction</li>
 *   <li>Implements: docs/part1/DiamondGraph.xml</li>
 * </ul>
 *
 * <h3>Layer 6: Serialization & I/O ({@link org.subclass.logic.proof.io})</h3>
 * <ul>
 *   <li>{@link org.subclass.logic.proof.io.ProofIO}: Bidirectional Proof ↔ XML/JSON/S-expression mapping</li>
 *   <li>{@link org.subclass.logic.proof.check.ProofWalker}: Runtime validation via reflection</li>
 *   <li>Bridges the XML specification notation and Java object model</li>
 *   <li>Implements: docs/part1/Serialization.xml</li>
 * </ul>
 *
 * <h2>How Specification and Implementation Relate</h2>
 *
 * <strong>docs/ (XML Monograph)</strong>: Authoritative specification of logical theory
 * <ul>
 *   <li>Defines sequent calculus formalism</li>
 *   <li>Describes diamond graph structure and constraints</li>
 *   <li>States what theorems are provable in which logics</li>
 * </ul>
 *
 * <strong>src/ (Java Implementation)</strong>: Formal proof that specification is constructible
 * <ul>
 *   <li>Reifies all sequent-calculus rules as ProofNode records</li>
 *   <li>Implements diamond graph as data structure with validators (LEMProofs, LNCProofs)</li>
 *   <li>Demonstrates that claimed theorems can be proven using the rules</li>
 * </ul>
 *
 * <strong>Cross-references</strong>: Every Java class should reference the corresponding spec section
 * <ul>
 *   <li>See {@link org.subclass.annotation.RuleSpec @RuleSpec} for rule metadata linking to specification</li>
 *   <li>See docs/MAPPING.md for complete spec ↔ implementation correspondence</li>
 * </ul>
 *
 * <h2>Key Invariants</h2>
 *
 * The system maintains these invariants at compile time:
 * <ol>
 *   <li><strong>Type Safety</strong>: Method return types serve as proof witnesses.
 *       A method claiming to prove a theorem must return a compatible Proof type.</li>
 *   <li><strong>Diamond Graph Consistency</strong>: LEM/LNC must have specific provability status in each node.
 *       LEMProofs/LNCProofs validate this structure.</li>
 *   <li><strong>Cardinality Constraints</strong>: Phantom types (Many, One, Zero) enforce logical matrix properties.
 *       Each logic supports specific cardinality combinations.</li>
 *   <li><strong>Rule Closure</strong>: All applicable rules must be discoverable and executable.
 *       ServiceLoader registration ensures complete enumeration.</li>
 * </ol>
 *
 * <h2>Usage Pattern</h2>
 *
 * <h3>For Theorem Writers</h3>
 * <pre>{@code
 * @TheoremFamily(name = "MyTheorem", ...)
 * public class MyProofs {
 *   @Theorem(name = "MyTheorem_in_LK", signature = "LK", status = "PROVABLE")
 *   public static Proof<Many, Many> classical() { ... }
 *   // ... other logics ...
 * }
 * }</pre>
 *
 * <h3>For Proof Implementers</h3>
 * <pre>{@code
 * Proof<Many, Many> proof = andLeft(
 *   axiom(a),
 *   orRight(axiom(b))
 * );
 * }</pre>
 *
 * <h3>For Serialization</h3>
 * <pre>{@code
 * String xml = ProofIO.toXML(proof);           // Proof → XML
 * String json = ProofIO.toJSON(proof);         // Proof → JSON
 * Proof<Many, Many> restored = ProofIO.fromXML(xml);
 * }</pre>
 *
 * <h2>Future Extensions (M2-M5 Roadmap)</h2>
 *
 * <ul>
 *   <li><strong>M1</strong>: DocBook XML export, LaTeX stylesheets</li>
 *   <li><strong>M2</strong>: Theory modules (FALL encoder, bi-constructive logic)</li>
 *   <li><strong>M3</strong>: Hierarchy encodings (ZF/NBG/MK set theories)</li>
 *   <li><strong>M4</strong>: Decidability procedures and proof search</li>
 *   <li><strong>M5</strong>: Semantic web export (RDF/OWL), language bindings</li>
 * </ul>
 *
 * <h2>Documentation Navigation</h2>
 *
 * <ul>
 *   <li>{@linkplain docs.MAPPING docs/MAPPING.md}: Specification ↔ Implementation correspondence</li>
 *   <li>{@linkplain docs.README docs/README.md}: Project overview and roadmap</li>
 *   <li>{@linkplain docs.part1 docs/part1/}: XML monograph (formal specification)</li>
 *   <li>{@linkplain docs.design docs/design/}: Architectural decisions and design docs</li>
 * </ul>
 */
package org.subclass;
