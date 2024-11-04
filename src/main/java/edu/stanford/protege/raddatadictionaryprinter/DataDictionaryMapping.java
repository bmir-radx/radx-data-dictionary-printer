package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionary;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryRecord;

import java.util.*;

public record DataDictionaryMapping(Map<String, MappingRecord> records, List<MappedDataDictionary> referenceDictionaries) {

    public static DataDictionaryMapping create(List<MappingRecord> records, List<MappedDataDictionary> referenceDictionaries) {
        var map = new HashMap<String, MappingRecord>();
        records.forEach(r -> map.put(r.id(), r));
        return new DataDictionaryMapping(map, referenceDictionaries);
    }

    public Optional<MappingRecord> get(String id) {
        return Optional.ofNullable(records.get(id));
    }

    public List<RADxDataDictionaryRecord> getMappedRecords(String id) {
        return get(id).stream()
                .map(MappingRecord::mappings)
                .flatMap(List::stream)
                .map(pm -> getMappedRecordsForProgram(id, pm.program()))
                .flatMap(List::stream)
                .toList();
    }

    public List<RADxDataDictionaryRecord> getMappedRecordsForProgram(String idRegex, RadxProgram program) {
        var rs = referenceDictionaries.stream()
                .filter(dd -> dd.program().equals(program))
                .map(MappedDataDictionary::dataDictionary)
                .map(RADxDataDictionary::records)
                .flatMap(Collection::stream)
                .filter(r -> r.id().matches(idRegex))
                .toList();
    return rs;
    }

}
