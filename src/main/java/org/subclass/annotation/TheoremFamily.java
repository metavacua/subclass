package org.subclass.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for grouping four related theorems into a tetragram family.
 * A theorem family formalizes how a single mathematical assertion appears
 * across four related logical systems forming a diamond/tetragram structure.
 *
 * The four nodes correspond to:
 * 1. classicalTheorem: <Consistent=true, Complete=true> (e.g., LK)
 * 2. intuitionisticTheorem: <Consistent=true, Complete=false> (e.g., LJ)
 * 3. paraconsistentTheorem: <Consistent=false, Complete=true> (e.g., dual)
 * 4. commonLogicTheorem: <Consistent=false, Complete=false> (e.g., intersection)
 *
 * These four must be members (methods) of the annotated class, each bearing
 * a @Theorem annotation with the appropriate signature.
 *
 * Example:
 * @TheoremFamily(
 *     name = "LEM",
 *     displayName = "Law of Excluded Middle",
 *     classicalTheorem = "lemInLK",
 *     intuitionisticTheorem = "lemInLJ",
 *     paraconsistentTheorem = "lemInDual",
 *     commonLogicTheorem = "lemInCommon",
 *     description = "The tetragram of LEM across four logical systems"
 * )
 * public class LEMTetragram { ... }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TheoremFamily {
    /**
     * Internal identifier for this theorem family.
     * Should be unique across the codebase.
     * Example: "LEM", "LNC", "DoubleNegation"
     */
    String name();

    /**
     * Human-readable display name for this theorem.
     * Example: "Law of Excluded Middle"
     */
    String displayName() default "";

    /**
     * Name of the method annotated with @Theorem for the classical node.
     * This logic is consistent and complete (e.g., LK).
     * Example: "lemInLK"
     */
    String classicalTheorem() default "";

    /**
     * Name of the method annotated with @Theorem for the intuitionistic node.
     * This logic is consistent but not complete (e.g., LJ).
     * Example: "lemInLJ"
     */
    String intuitionisticTheorem() default "";

    /**
     * Name of the method annotated with @Theorem for the paraconsistent-complete node.
     * This logic is paraconsistent but complete (e.g., dual of LJ).
     * Example: "lemInDual"
     */
    String paraconsistentTheorem() default "";

    /**
     * Name of the method annotated with @Theorem for the common logic node.
     * This logic is paraconsistent and paracomplete (e.g., intersection).
     * Example: "lemInCommon"
     */
    String commonLogicTheorem() default "";

    /**
     * Optional description of this theorem family.
     * Should explain why these four theorems form a meaningful tetragram.
     * Example: "LEM is provable in LK, not provable in LJ, and neither provable nor refutable in the common logic"
     */
    String description() default "";

    /**
     * Optional reference to the meta-theorem relating these four theorems.
     * Can be a citation, paper, or URL.
     */
    String metaTheoremReference() default "";
}
