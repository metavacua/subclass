package org.subclass.logic.signature;

import org.junit.jupiter.api.Test;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.One;
import org.subclass.logic.proof.typed.Sequent;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the four primary nodes of the diamond: each logic's axiom schema,
 * admitted rules, cardinality signature, and the inclusion relations
 * {@code Common ⊂ LJ ⊂ LK} and {@code Common ⊂ LDJ ⊂ LK}.
 */
class LogicHierarchyTest {

    private static final Formula A = new Formula.Atom("A");
    private static final Formula B = new Formula.Atom("B");

    @Test
    void commonLogicIsOneOneWithCommonAxiomSchema() {
        Logic common = CommonLogic.INSTANCE;
        assertEquals(ReflexiveAxiomSchema.COMMON, common.axiomSchema());
        assertEquals(One.class, common.antecedentCardinality());
        assertEquals(One.class, common.succedentCardinality());
        assertEquals("Common", common.label());
    }

    @Test
    void intuitionisticLJIsManyOneWithIntuitionisticAxiomSchema() {
        Logic lj = IntuitionisticLJ.INSTANCE;
        assertEquals(ReflexiveAxiomSchema.INTUITIONISTIC, lj.axiomSchema());
        assertEquals(Many.class, lj.antecedentCardinality());
        assertEquals(One.class, lj.succedentCardinality());
        assertEquals("LJ", lj.label());
    }

    @Test
    void dualLJIsOneManyWithDualAxiomSchema() {
        Logic ldj = DualLJ.INSTANCE;
        assertEquals(ReflexiveAxiomSchema.DUAL_INTUITIONISTIC, ldj.axiomSchema());
        assertEquals(One.class, ldj.antecedentCardinality());
        assertEquals(Many.class, ldj.succedentCardinality());
        assertEquals("LDJ", ldj.label());
    }

    @Test
    void classicalLKIsManyManyWithClassicalAxiomSchema() {
        Logic lk = ClassicalLK.INSTANCE;
        assertEquals(ReflexiveAxiomSchema.CLASSICAL, lk.axiomSchema());
        assertEquals(Many.class, lk.antecedentCardinality());
        assertEquals(Many.class, lk.succedentCardinality());
        assertEquals("LK", lk.label());
    }

    @Test
    void commonRuleSetIsSubsetOfIntuitionisticAndDual() {
        Set<String> common = CommonLogic.INSTANCE.admittedRules();
        assertTrue(IntuitionisticLJ.INSTANCE.admittedRules().containsAll(common),
            "LJ must admit every rule admitted by Common");
        assertTrue(DualLJ.INSTANCE.admittedRules().containsAll(common),
            "LDJ must admit every rule admitted by Common");
    }

    @Test
    void intuitionisticAndDualRuleSetsAreSubsetsOfClassical() {
        Set<String> lk = ClassicalLK.INSTANCE.admittedRules();
        assertTrue(lk.containsAll(IntuitionisticLJ.INSTANCE.admittedRules()),
            "LK must admit every rule admitted by LJ");
        assertTrue(lk.containsAll(DualLJ.INSTANCE.admittedRules()),
            "LK must admit every rule admitted by LDJ");
    }

    @Test
    void ljAdmitsLeftStructuralButNotRight() {
        Set<String> lj = IntuitionisticLJ.INSTANCE.admittedRules();
        assertTrue(lj.contains("WeakeningLeft"));
        assertTrue(lj.contains("ContractionLeft"));
        assertFalse(lj.contains("WeakeningRight"),
            "LJ's single succedent forbids right weakening");
        assertFalse(lj.contains("ContractionRight"),
            "LJ's single succedent forbids right contraction");
    }

    @Test
    void ldjAdmitsRightStructuralButNotLeft() {
        Set<String> ldj = DualLJ.INSTANCE.admittedRules();
        assertTrue(ldj.contains("WeakeningRight"));
        assertTrue(ldj.contains("ContractionRight"));
        assertFalse(ldj.contains("WeakeningLeft"),
            "LDJ's single antecedent forbids left weakening");
        assertFalse(ldj.contains("ContractionLeft"),
            "LDJ's single antecedent forbids left contraction");
    }

    @Test
    void commonAdmitsNoStructuralRules() {
        Set<String> common = CommonLogic.INSTANCE.admittedRules();
        assertFalse(common.contains("WeakeningLeft"));
        assertFalse(common.contains("WeakeningRight"));
        assertFalse(common.contains("ContractionLeft"));
        assertFalse(common.contains("ContractionRight"));
        assertFalse(common.contains("AdjacentExchangeLeft"));
        assertFalse(common.contains("ClassicalExchangeLeft"));
    }

    @Test
    void lkAdmitsAllStructuralRulesOnBothSides() {
        Set<String> lk = ClassicalLK.INSTANCE.admittedRules();
        for (String r : List.of(
                "WeakeningLeft", "WeakeningRight",
                "ContractionLeft", "ContractionRight",
                "AdjacentExchangeLeft", "AdjacentExchangeRight",
                "ClassicalExchangeLeft", "ClassicalExchangeRight")) {
            assertTrue(lk.contains(r), "LK should admit " + r);
        }
    }

    @Test
    void fourLogicsAreDistinctInstances() {
        Logic c = CommonLogic.INSTANCE;
        Logic lj = IntuitionisticLJ.INSTANCE;
        Logic ldj = DualLJ.INSTANCE;
        Logic lk = ClassicalLK.INSTANCE;
        assertNotEquals(c, lj);
        assertNotEquals(c, ldj);
        assertNotEquals(lj, ldj);
        assertNotEquals(lj, lk);
        assertNotEquals(ldj, lk);
    }

    // --- Reflexive axiom schema admissibility ---

    @Test
    void commonSchemaAdmitsOnlyOneOneIdentity() {
        assertTrue(ReflexiveAxiomSchema.COMMON.admits(
            Sequent.common(A, A)));
        assertFalse(ReflexiveAxiomSchema.COMMON.admits(
            Sequent.common(A, B)));
        assertFalse(ReflexiveAxiomSchema.COMMON.admits(
            new Sequent<Many, Many>(List.of(B, A), List.of(A))));
        assertFalse(ReflexiveAxiomSchema.COMMON.admits(
            new Sequent<Many, Many>(List.of(A), List.of(A, B))));
    }

    @Test
    void intuitionisticSchemaAdmitsGammaAEntailsA() {
        assertTrue(ReflexiveAxiomSchema.INTUITIONISTIC.admits(
            Sequent.common(A, A)));
        assertTrue(ReflexiveAxiomSchema.INTUITIONISTIC.admits(
            new Sequent<Many, One>(List.of(B, A), List.of(A))));
        assertFalse(ReflexiveAxiomSchema.INTUITIONISTIC.admits(
            new Sequent<Many, Many>(List.of(A), List.of(A, B))),
            "LJ schema forbids multi-succedent");
    }

    @Test
    void dualSchemaAdmitsAEntailsAdelta() {
        assertTrue(ReflexiveAxiomSchema.DUAL_INTUITIONISTIC.admits(
            Sequent.common(A, A)));
        assertTrue(ReflexiveAxiomSchema.DUAL_INTUITIONISTIC.admits(
            new Sequent<One, Many>(List.of(A), List.of(A, B))));
        assertFalse(ReflexiveAxiomSchema.DUAL_INTUITIONISTIC.admits(
            new Sequent<Many, Many>(List.of(B, A), List.of(A))),
            "LDJ schema forbids multi-antecedent");
    }

    @Test
    void classicalSchemaAdmitsAnyMatchingPair() {
        assertTrue(ReflexiveAxiomSchema.CLASSICAL.admits(
            new Sequent<Many, Many>(List.of(B, A), List.of(A, B))));
        assertFalse(ReflexiveAxiomSchema.CLASSICAL.admits(
            new Sequent<Many, Many>(List.of(A), List.of(B))),
            "LK schema requires some formula appearing on both sides");
    }

    @Test
    void sealedInterfacePermitsExactlyTheFourPrimaryNodes() {
        Class<?>[] permitted = Logic.class.getPermittedSubclasses();
        assertNotNull(permitted);
        assertEquals(4, permitted.length,
            "the diamond has exactly four primary nodes");
    }
}
