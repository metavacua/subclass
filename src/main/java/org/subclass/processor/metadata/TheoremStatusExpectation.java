package org.subclass.processor.metadata;

import java.util.Objects;

/**
 * Expected theorem status derivation for a specific theorem.
 * Used to validate that auto-derived statuses match expectations.
 */
public final class TheoremStatusExpectation {
    private final String theoremName;
    private final String classicalStatus;
    private final String intuitionisticStatus;
    private final String paraconsistentStatus;
    private final String commonLogicStatus;
    private final String classicalReasoning;
    private final String intuitionisticReasoning;
    private final String paraconsistentReasoning;
    private final String commonLogicReasoning;

    public TheoremStatusExpectation(
        String theoremName,
        String classicalStatus,
        String intuitionisticStatus,
        String paraconsistentStatus,
        String commonLogicStatus,
        String classicalReasoning,
        String intuitionisticReasoning,
        String paraconsistentReasoning,
        String commonLogicReasoning
    ) {
        this.theoremName = Objects.requireNonNull(theoremName, "theoremName");
        this.classicalStatus = Objects.requireNonNull(classicalStatus, "classicalStatus");
        this.intuitionisticStatus = Objects.requireNonNull(intuitionisticStatus, "intuitionisticStatus");
        this.paraconsistentStatus = Objects.requireNonNull(paraconsistentStatus, "paraconsistentStatus");
        this.commonLogicStatus = Objects.requireNonNull(commonLogicStatus, "commonLogicStatus");
        this.classicalReasoning = classicalReasoning;
        this.intuitionisticReasoning = intuitionisticReasoning;
        this.paraconsistentReasoning = paraconsistentReasoning;
        this.commonLogicReasoning = commonLogicReasoning;

        validateStatuses();
    }

    public String theoremName() {
        return theoremName;
    }

    public String classicalStatus() {
        return classicalStatus;
    }

    public String intuitionisticStatus() {
        return intuitionisticStatus;
    }

    public String paraconsistentStatus() {
        return paraconsistentStatus;
    }

    public String commonLogicStatus() {
        return commonLogicStatus;
    }

    public String getStatusForPosition(String position) {
        return switch (position) {
            case "classical" -> classicalStatus;
            case "intuitionistic" -> intuitionisticStatus;
            case "paraconsistent" -> paraconsistentStatus;
            case "common" -> commonLogicStatus;
            default -> throw new IllegalArgumentException("Unknown position: " + position);
        };
    }

    private void validateStatuses() {
        String[] allStatuses = {classicalStatus, intuitionisticStatus, paraconsistentStatus, commonLogicStatus};
        for (String status : allStatuses) {
            if (!isValidStatus(status)) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }
    }

    private boolean isValidStatus(String status) {
        return status.equals("PROVABLE_UNREFUTABLE") ||
               status.equals("PROVABLE_REFUTABLE") ||
               status.equals("NON_PROVABLE_REFUTABLE") ||
               status.equals("NON_PROVABLE_UNREFUTABLE");
    }

    @Override
    public String toString() {
        return "TheoremStatusExpectation{" +
               "theoremName='" + theoremName + '\'' +
               ", classicalStatus='" + classicalStatus + '\'' +
               ", intuitionisticStatus='" + intuitionisticStatus + '\'' +
               ", paraconsistentStatus='" + paraconsistentStatus + '\'' +
               ", commonLogicStatus='" + commonLogicStatus + '\'' +
               '}';
    }
}
