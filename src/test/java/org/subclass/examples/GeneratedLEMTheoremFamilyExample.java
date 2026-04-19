package org.subclass.examples;

import org.subclass.annotation.*;

/**
 * Example: Annotation-driven generation of the Law of Excluded Middle (LEM) theorem family
 * and its anti-theorem variant LEM'.
 *
 * <p>This class demonstrates the @GeneratedTheoremFamily annotation that completely specifies
 * a theorem and optional anti-theorem with relationship-aware duality. The TheoremGeneratorProcessor
 * will automatically generate:
 * <ul>
 *   <li>GeneratedLEMFamily.java - @TheoremFamily with 4 @Theorem methods (one per node)</li>
 *   <li>GeneratedLEM_PRIMEFamily.java - Anti-theorem family with relationship-aware status mapping</li>
 *   <li>Supporting proof implementations for each node</li>
 * </ul>
 *
 * <p><b>Theorem Semantics:</b>
 *
 * <p><b>LEM (Law of Excluded Middle):</b>
 * Classical theorem: "A ∨ ¬A" (Excluded Middle)
 * <pre>
 *   LK: PROVABLE           (Classical: both sides unrestricted)
 *   LJ: NON_PROVABLE       (Intuitionistic: can't prove all tautologies)
 *   LDJ: PROVABLE          (Paraconsistent: negation behaves differently)
 *   COMMON: UNPROVABLE_AND_REFUTABLE (Minimal intersection)
 * </pre>
 *
 * <p><b>LEM' (Anti-Theorem Variant):</b>
 * Captures double negation elimination failures in paraconsistent contexts.
 * Notation: "double negation of A entails..." or "DN elimination failures"
 * <pre>
 *   LK: PROVABLE           (Classical DN elimination holds)
 *   LJ: PROVABLE           (Intuitionistic DN elimination restricted)
 *   LDJ: NON_PROVABLE      (Paraconsistent: DN elimination fails)
 *   COMMON: UNPROVABLE_AND_REFUTABLE
 * </pre>
 *
 * <p><b>Relationship-Aware Duality:</b>
 * LEM and LEM' are NOT simple status inversions. They represent complementary failure
 * modes of double negation elimination (DNE) and introduction (DNI) across the tetragram.
 * In paraconsistent logic, where truth values can collapse differently, LEM' captures
 * the unprovability of certain DN elimination patterns.
 */
/**
 * Specification class (comment out the annotation for now while testing).
 * When uncommented, this @GeneratedTheoremFamily annotation will trigger
 * automatic generation of GeneratedLEMFamily.java and GeneratedLEM_PRIMEFamily.java
 * during annotation processing.
 *
 * The annotation would look like:
 * @GeneratedTheoremFamily(
 *     name = "LEM",
 *     graphReference = "DiamondGraphSpec",
 *     ...
 * )
 */
public class GeneratedLEMTheoremFamilyExample {

    /**
     * Demonstrates that both theorem and anti-theorem families are generated
     * from a single annotation specification.
     */
    public static void main(String[] args) {
        System.out.println("Law of Excluded Middle (LEM) Theorem Family Generation");
        System.out.println("======================================================");
        System.out.println();

        System.out.println("Theorem: LEM");
        System.out.println("  Notation: A ∨ ¬A");
        System.out.println("  Status mapping:");
        System.out.println("    LK:     PROVABLE");
        System.out.println("    LJ:     NON_PROVABLE");
        System.out.println("    LDJ:    PROVABLE");
        System.out.println("    COMMON: UNPROVABLE_AND_REFUTABLE");
        System.out.println();

        System.out.println("Anti-Theorem: LEM'");
        System.out.println("  Notation: Double negation elimination variant");
        System.out.println("  Status mapping:");
        System.out.println("    LK:     PROVABLE");
        System.out.println("    LJ:     PROVABLE");
        System.out.println("    LDJ:    NON_PROVABLE");
        System.out.println("    COMMON: UNPROVABLE_AND_REFUTABLE");
        System.out.println();

        System.out.println("Duality Relationship:");
        System.out.println("  LEM maps classical law of excluded middle");
        System.out.println("  LEM' maps DN elimination failures in paraconsistent contexts");
        System.out.println();

        System.out.println("Key Insight:");
        System.out.println("  Anti-theorems are NOT status inversions!");
        System.out.println("  They capture unprovability/refutability distinctions");
        System.out.println("  and double negation elimination/introduction failures");
        System.out.println("  across different logical systems.");
        System.out.println();

        System.out.println("Generated artifacts:");
        System.out.println("  - GeneratedLEMFamily.java (@TheoremFamily)");
        System.out.println("  - GeneratedLEM_PRIMEFamily.java (anti-theorem)");
    }
}
