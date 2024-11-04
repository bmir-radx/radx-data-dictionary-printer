package edu.stanford.protege.raddatadictionaryprinter;

import java.util.List;
import java.util.stream.Stream;

public record DataDictionarySection(String sectionName,
                                    List<AugmentedDataDictionaryRecord> records) {

    public Stream<String> getIds() {
        return records.stream().map(AugmentedDataDictionaryRecord::id);
    }
}
