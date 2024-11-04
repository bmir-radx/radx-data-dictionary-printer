package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionary;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryRecord;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public record SectionedDataDictionary(List<DataDictionarySection> sections) {

    public static SectionedDataDictionary fromDataDictionary(RADxDataDictionary dataDictionary,
                                                             @Nullable DataDictionaryMapping dataDictionaryMapping,
                                                             @Nonnull TermOracle termOracle,
                                                             @Nonnull EnumerationMappingOracle enumerationMappingOracle) {
        var bySection = dataDictionary.records()
                .stream()
                .collect(Collectors.groupingBy(RADxDataDictionaryRecord::section, LinkedHashMap::new, Collectors.toList()));

        var sections = new ArrayList<DataDictionarySection>();
        var number = new AtomicInteger(1);
        bySection.forEach((section, records) -> {
            var mappedRecords = records.stream()
                            .map(r -> AugmentedDataDictionaryRecord.fromDataDictionaryRecord(r,
                                    number.getAndIncrement(),
                                    dataDictionaryMapping,
                                    termOracle,
                                    enumerationMappingOracle))
                    .toList();
            sections.add(new DataDictionarySection(section, mappedRecords));
        });
        return new SectionedDataDictionary(sections);
    }

    @JsonIgnore
    public Set<String> getIds() {
        return sections.stream()
                .flatMap(DataDictionarySection::getIds)
                .collect(Collectors.toSet());
    }
}
