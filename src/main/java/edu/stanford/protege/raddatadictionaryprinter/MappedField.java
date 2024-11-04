package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryRecord;
import jakarta.annotation.Nullable;

public record MappedField(String id, @Nullable RADxDataDictionaryRecord mappedRecord) {


}
