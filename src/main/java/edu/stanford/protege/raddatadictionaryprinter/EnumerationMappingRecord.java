package edu.stanford.protege.raddatadictionaryprinter;

public record EnumerationMappingRecord(RadxProgram program,
                                      String fromVariable,
                                      String toVariable,
                                      String fromValue,
                                      String fromLabel,
                                      String toValue,
                                      String toLabel) {
}
