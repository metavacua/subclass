package org.subclass.processor;

import org.subclass.annotation.DiamondGraph;
import org.subclass.annotation.DiamondGraphNode;
import org.subclass.annotation.TheoremStatusDerivation;
import org.subclass.processor.metadata.DiamondGraphInfo;
import org.subclass.processor.metadata.DiamondGraphNodeInfo;
import org.subclass.processor.metadata.TheoremStatusExpectation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Annotation processor for @DiamondGraph specifications.
 *
 * Multi-phase processing:
 * 1. Parse @DiamondGraph annotations into DiamondGraphInfo structures
 * 2. Validate cardinality hierarchy and axiom schema consistency
 * 3. Auto-derive theorem statuses using TheoremStatusDeriver
 * 4. Compare derived statuses against @TheoremStatusDerivation expectations
 * 5. Report errors/warnings for mismatches
 *
 * Non-exclusive: allows other processors (e.g., TheoremProcessor) to also validate.
 */
@SupportedAnnotationTypes("org.subclass.annotation.DiamondGraph")
public class DiamondGraphProcessor extends AbstractProcessor {

    private final TheoremStatusDeriver deriver = new TheoremStatusDeriver();
    private boolean hasProcessedGraphs = false;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        // Only process in first round to avoid duplicate work
        if (hasProcessedGraphs) {
            return false;
        }
        hasProcessedGraphs = true;

        Set<? extends Element> graphElements = roundEnv.getElementsAnnotatedWith(DiamondGraph.class);
        if (graphElements.isEmpty()) {
            return false;
        }

        for (Element element : graphElements) {
            if (element instanceof TypeElement typeElement) {
                processDiamondGraph(typeElement);
            }
        }

        return false; // Don't claim annotations; let other processors work too
    }

    private void processDiamondGraph(TypeElement typeElement) {
        DiamondGraph annotation = typeElement.getAnnotation(DiamondGraph.class);

        try {
            // Phase 1: Parse annotation into structured form
            ParsedGraph parsed = parseAnnotation(annotation);
            DiamondGraphInfo graphInfo = parsed.graphInfo();
            Map<String, TheoremStatusExpectation> expectations = parsed.expectations();

            // Phase 2: Validate graph structure (cardinality hierarchy, axiom schemas)
            // This is done in DiamondGraphInfo constructor via validate()
            // If validation fails, an exception is thrown

            // Phase 3: Auto-derive theorem statuses
            Map<String, Map<String, String>> derivedStatuses = new HashMap<>();
            for (TheoremStatusDerivation expectedDerivation : annotation.theoremDerivations()) {
                String theoremName = expectedDerivation.theoremName();
                Map<String, String> derived = deriveTheoremStatuses(theoremName, graphInfo);
                derivedStatuses.put(theoremName, derived);

                // Phase 4: Compare derived against expected
                TheoremStatusExpectation expectation = expectations.get(theoremName);
                if (expectation != null) {
                    validateTheoremStatusesMatch(theoremName, expectation, derived, typeElement);
                }
            }

        } catch (IllegalArgumentException e) {
            // Cardinality/axiom schema validation failed
            processingEnv.getMessager().printMessage(
                javax.tools.Diagnostic.Kind.ERROR,
                "Diamond graph '" + annotation.name() + "' validation failed: " + e.getMessage(),
                typeElement
            );
        }
    }

    private ParsedGraph parseAnnotation(DiamondGraph annotation) {
        DiamondGraphNodeInfo classicalNode = parseNode(annotation.classicalNode());
        DiamondGraphNodeInfo intuitionisticNode = parseNode(annotation.intuitionisticNode());
        DiamondGraphNodeInfo paraconsistentNode = parseNode(annotation.paraconsistentNode());
        DiamondGraphNodeInfo commonLogicNode = parseNode(annotation.commonLogicNode());

        Map<String, TheoremStatusExpectation> expectations = new HashMap<>();
        for (TheoremStatusDerivation derivation : annotation.theoremDerivations()) {
            expectations.put(
                derivation.theoremName(),
                new TheoremStatusExpectation(
                    derivation.theoremName(),
                    derivation.classicalStatus(),
                    derivation.intuitionisticStatus(),
                    derivation.paraconsistentStatus(),
                    derivation.commonLogicStatus(),
                    derivation.classicalReasoning(),
                    derivation.intuitionisticReasoning(),
                    derivation.paraconsistentReasoning(),
                    derivation.commonLogicReasoning()
                )
            );
        }

        DiamondGraphInfo graphInfo = new DiamondGraphInfo(
            annotation.name(),
            annotation.displayName(),
            annotation.description(),
            classicalNode,
            intuitionisticNode,
            paraconsistentNode,
            commonLogicNode,
            expectations
        );

        return new ParsedGraph(graphInfo, expectations);
    }

    private record ParsedGraph(DiamondGraphInfo graphInfo, Map<String, TheoremStatusExpectation> expectations) {}

    private DiamondGraphNodeInfo parseNode(DiamondGraphNode nodeAnnotation) {
        Set<String> rules = new HashSet<>();
        for (String rule : nodeAnnotation.structuralRules()) {
            rules.add(rule);
        }

        Set<String> connectives = new HashSet<>();
        for (String connective : nodeAnnotation.connectives()) {
            connectives.add(connective);
        }

        return new DiamondGraphNodeInfo(
            nodeAnnotation.position(),
            nodeAnnotation.antecedentCardinality(),
            nodeAnnotation.succedentCardinality(),
            nodeAnnotation.axiomSchema(),
            rules,
            connectives,
            nodeAnnotation.functionallyComplete()
        );
    }

    private Map<String, String> deriveTheoremStatuses(String theoremName, DiamondGraphInfo graphInfo) {
        Map<String, String> statuses = new HashMap<>();

        // Derive status for each node
        for (String position : new String[]{"classical", "intuitionistic", "paraconsistent", "common"}) {
            DiamondGraphNodeInfo node = graphInfo.getNodeByPosition(position);
            String status = deriver.deriveStatus(theoremName, node, graphInfo);
            statuses.put(position, status);
        }

        return statuses;
    }

    private void validateTheoremStatusesMatch(
        String theoremName,
        TheoremStatusExpectation expected,
        Map<String, String> derived,
        TypeElement typeElement
    ) {
        for (String position : new String[]{"classical", "intuitionistic", "paraconsistent", "common"}) {
            String expectedStatus = expected.getStatusForPosition(position);
            String derivedStatus = derived.get(position);

            if (!expectedStatus.equals(derivedStatus)) {
                processingEnv.getMessager().printMessage(
                    javax.tools.Diagnostic.Kind.WARNING,
                    "Theorem '" + theoremName + "' in " + position + " logic: " +
                    "expected status " + expectedStatus + " but derivation produces " + derivedStatus,
                    typeElement
                );
            }
        }
    }
}
