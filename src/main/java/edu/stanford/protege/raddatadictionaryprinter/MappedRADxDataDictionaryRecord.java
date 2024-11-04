package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import edu.stanford.bmir.radx.datadictionary.lib.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@JsonPropertyOrder({"id", "label", "description", "section", "terms", "cardinality", "datatype", "pattern", "unit", "enumeration", "enumerationMapping", "missingValueCodes", "notes", "provenance", "seeAlso"})
public record MappedRADxDataDictionaryRecord(@Nonnull String id, @Nonnull String label, @Nonnull String description, @Nonnull String section, @Nonnull List<TermIdentifier> terms, @Nonnull Cardinality cardinality, @Nonnull Datatype datatype, @Nullable Pattern pattern, @Nullable Unit unit, @JsonIgnore @Nullable Enumeration enumeration, @Nullable Enumeration missingValueCodes, @Nonnull String notes, @Nonnull String provenance, @Nullable URI seeAlso, List<EnumerationChoiceMapping> enumerationMapping) {

    public static MappedRADxDataDictionaryRecord fromRADxDataDictionaryRecord(RADxDataDictionaryRecord record, List<EnumerationChoiceMapping> choicesMapping) {
        return new MappedRADxDataDictionaryRecord(record.id(),
                record.label(),
                record.description(),
                record.section(),
                record.terms(),
                record.cardinality(),
                record.datatype(),
                record.pattern(),
                record.unit(),
                record.enumeration(),
                record.missingValueCodes(),
                record.notes(),
                record.provenance(),
                record.seeAlso(),
                choicesMapping);
    }

    @JsonProperty("enumeration")
    public List<EnumerationChoice> getEnumerationChoices() {
        return enumeration != null ? enumeration.choices() : List.of();
    }

    public Optional<EnumerationChoice> getMappedTo(EnumerationChoice choice) {
        return this.enumerationMapping.stream()
                .filter(em -> em.from().value().equals(choice.value()))
                .map(ch -> ch.to())
                .findFirst();
    }

    @JsonIgnore
    public String getMappedToValue(EnumerationChoice choice) {
        return getMappedTo(choice).map(EnumerationChoice::value).orElse("");
    }

    @JsonIgnore
    public String getMappedToLabel(EnumerationChoice choice) {
        return getMappedTo(choice).map(EnumerationChoice::label).orElse("");
    }

    @JsonIgnore
    public List<EnumerationChoice> enumerationChoices() {
        if (enumeration != null) {
            Comparator<EnumerationChoice> comparator = Comparator.comparing((EnumerationChoice v) -> v.value().length()).thenComparing(EnumerationChoice::value);
            return enumeration.choices().stream().sorted(comparator).toList();
        } else {
            return List.of();
        }
    }

    @JsonIgnore
    public List<EnumerationChoiceMapping> getSortedEnumerationMapping() {
        return this.enumerationMapping.stream()
                .sorted(Comparator.comparing(v -> v.from().value()))
                .toList();
    }
}
