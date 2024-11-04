package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionary;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public record AugmentedDataDictionary(List<AugmentedDataDictionaryRecord> records) {

    public static AugmentedDataDictionary fromDataDictionary(RADxDataDictionary dataDictionary,
                                                             DataDictionaryMapping dataDictionaryMapping,
                                                             TermOracle termOracle,
                                                             EnumerationMappingOracle enumerationMappingOracle) {
        var number = new AtomicInteger(1);
        var augmentedRecords = dataDictionary.records()
                .stream()
                .map(r -> AugmentedDataDictionaryRecord.fromDataDictionaryRecord(r, number.getAndIncrement(), dataDictionaryMapping, termOracle, enumerationMappingOracle))
                .toList();
        return new AugmentedDataDictionary(augmentedRecords);
    }
}
