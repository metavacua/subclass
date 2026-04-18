package org.subclass.logic.proof.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Serialization-friendly mirror of {@link org.subclass.logic.proof.typed.Sequent}.
 *
 * The cardinality type parameters (L, R) of the typed {@code Sequent<L,R>} are
 * not preserved here: on the wire a sequent is simply two lists of formulas.
 * Reconstruction of the correct cardinality phantom types happens at
 * deserialization time, where the caller supplies the expected L, R by
 * selecting the correct {@link ProofIO} read-method overload.
 */
public final class SequentDto {

    public List<FormulaDto> antecedent = new ArrayList<>();
    public List<FormulaDto> succedent = new ArrayList<>();

    public SequentDto() {}

    public SequentDto(List<FormulaDto> antecedent, List<FormulaDto> succedent) {
        this.antecedent = new ArrayList<>(antecedent);
        this.succedent = new ArrayList<>(succedent);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SequentDto s
            && Objects.equals(antecedent, s.antecedent)
            && Objects.equals(succedent, s.succedent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(antecedent, succedent);
    }
}
