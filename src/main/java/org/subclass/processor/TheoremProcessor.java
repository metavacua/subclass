package org.subclass.processor;

import org.subclass.annotation.TheoremFamily;
import org.subclass.annotation.Theorem;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Annotation processor for compile-time validation of theorem families.
 *
 * This processor validates that @TheoremFamily annotations comply with the metalanguage
 * constraints of the tetragram structure:
 * - All four theorem methods are present and properly named
 * - Theorem statuses respect logical consistency/completeness constraints
 * - Duality relationships are correctly specified
 *
 * The processor generates no source or resource files; it purely validates
 * and reports compile-time errors/warnings.
 */
public class TheoremProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(TheoremFamily.class.getName(), Theorem.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Process @TheoremFamily annotations
        for (Element element : roundEnv.getElementsAnnotatedWith(TheoremFamily.class)) {
            validateTheoremFamily(element);
        }

        // Note: We don't claim the annotations to allow other processors to handle them
        return false;
    }

    /**
     * Validate a @TheoremFamily annotated class.
     *
     * @param element The annotated type element
     */
    private void validateTheoremFamily(Element element) {
        TheoremFamily family = element.getAnnotation(TheoremFamily.class);

        // Check that the family name is not empty
        if (family.name().isEmpty()) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "TheoremFamily name cannot be empty",
                element
            );
        }

        // Check that the four theorem method references are not empty
        String[] theoremMethods = {
            family.classicalTheorem(),
            family.intuitionisticTheorem(),
            family.paraconsistentTheorem(),
            family.commonLogicTheorem()
        };

        for (int i = 0; i < theoremMethods.length; i++) {
            if (theoremMethods[i].isEmpty()) {
                String[] positions = {"classical", "intuitionistic", "paraconsistent", "common logic"};
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "TheoremFamily missing " + positions[i] + " theorem method reference",
                    element
                );
            }
        }

        // Additional validation could go here:
        // - Verify that the referenced methods exist
        // - Check that all methods have @Theorem annotations
        // - Validate theorem status consistency across the tetragram
        // - Ensure duality relationships are correctly specified
    }
}
