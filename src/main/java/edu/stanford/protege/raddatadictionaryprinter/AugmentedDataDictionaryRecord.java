package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.bmir.radx.datadictionary.lib.*;
import edu.stanford.bmir.radx.datadictionary.lib.Enumeration;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

public record AugmentedDataDictionaryRecord(@JsonIgnore int number, @Nonnull String id, List<String> aliases, @Nonnull String label, @Nonnull String description, @Nonnull List<String> examples, @Nonnull String section, @Nonnull List<AugmentedTermIdentifier> terms, @Nonnull Cardinality cardinality, @Nonnull Datatype datatype, @Nullable Pattern pattern, @Nullable Unit unit, @Nullable AugmentedEnumeration enumeration, @Nullable Enumeration missingValueCodes, @Nonnull String notes, @Nonnull String provenance, @Nullable URI seeAlso, @Nonnull Mapping mapping) {

    public AugmentedDataDictionaryRecord(int number, @Nonnull String id, @Nonnull List<String> aliases, @Nonnull String label, @Nonnull String description, @Nonnull List<String> examples, @Nonnull String section, @Nonnull List<AugmentedTermIdentifier> terms, @Nonnull Cardinality cardinality, @Nonnull Datatype datatype, @Nullable Pattern pattern, @Nullable Unit unit, @Nullable AugmentedEnumeration enumeration, @Nullable Enumeration missingValueCodes, @Nonnull String notes, @Nonnull String provenance, @Nullable URI seeAlso, @Nonnull Mapping mapping) {
        this.number = number;
        this.id = id;
        this.aliases = Objects.requireNonNull(aliases);
        this.label = Objects.requireNonNull(label);
        this.description = Objects.requireNonNull(description);
        this.examples = Objects.requireNonNull(examples);
        this.section = Objects.requireNonNull(section);
        this.terms = Objects.requireNonNull(terms);
        this.cardinality = Objects.requireNonNull(cardinality);
        this.datatype = Objects.requireNonNull(datatype);
        this.pattern = pattern;
        this.unit = unit;
        this.enumeration = enumeration;
        this.missingValueCodes = missingValueCodes;
        this.notes = notes;
        this.provenance = provenance;
        this.seeAlso = seeAlso;
        this.mapping = Objects.requireNonNull(mapping);
    }

    @JsonIgnore
    public List<String> synonyms() {
        return terms.stream().flatMap(t -> t.synonyms().stream())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();
    }

    @JsonProperty("enumeration")
    public Optional<AugmentedEnumeration> getEnumeration() {
        return Optional.ofNullable(enumeration);
    }

    @JsonIgnore
    public List<String> getEnumerationValues() {
        return this.getEnumeration()
                .stream()
                .map(AugmentedEnumeration::choices)
                .flatMap(List::stream)
                .map(AugmentedEnumerationChoice::value)
                .toList();
    }

    @Nonnull
    @JsonProperty("mapping")
    public Optional<Mapping> getMapping() {
        return mapping.isEmpty() ? Optional.empty() : Optional.of(mapping);
    }


    public static AugmentedDataDictionaryRecord fromDataDictionaryRecord(RADxDataDictionaryRecord r, int recordNumber,
                                                                         DataDictionaryMapping dataDictionaryMapping,
                                                                         TermOracle termOracle,
                                                                         EnumerationMappingOracle enumerationMappingOracle) {

                    var mapping = Mapping.getMappingForRecord(dataDictionaryMapping, r, enumerationMappingOracle);
                    return new AugmentedDataDictionaryRecord(
                            recordNumber,
                            r.id(),
                            List.of(),
                            r.label(),
                            r.description(),
                            List.of(),
                            r.section(),
                            r.terms().stream().map(t -> AugmentedTermIdentifier.fromTermIdentifier(t, termOracle)).toList(),
                            r.cardinality(),
                            r.datatype(),
                            r.pattern(),
                            r.unit(),
                            AugmentedEnumeration.from(r.enumeration()),
                            r.missingValueCodes(),
                            r.notes(),
                            r.provenance(),
                            r.seeAlso(),
                            mapping
                    );

    }
}
