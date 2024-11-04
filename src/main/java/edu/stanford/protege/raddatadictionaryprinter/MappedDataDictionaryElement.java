package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryRecord;

import javax.annotation.Nullable;

public record MappedDataDictionaryElement(String id, @Nullable RADxDataDictionaryRecord dataDictionaryRecord) {
}
