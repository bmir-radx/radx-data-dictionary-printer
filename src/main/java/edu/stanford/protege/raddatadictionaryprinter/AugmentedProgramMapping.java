package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryRecord;

import java.util.List;

public record AugmentedProgramMapping(@JsonProperty("program") RadxProgram program,
                                      @JsonProperty("dataElements") List<MappedRADxDataDictionaryRecord> dataElements) {

    public static AugmentedProgramMapping of(RadxProgram radxProgram) {
        return new AugmentedProgramMapping(radxProgram, List.of());
    }

    @JsonIgnore
    public boolean isEmpty() {
        return dataElements.isEmpty();
    }
}
