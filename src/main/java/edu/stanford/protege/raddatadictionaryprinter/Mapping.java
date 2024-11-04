package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryRecord;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Mapping(@JsonProperty("radxUp") AugmentedProgramMapping radxUpMapping,
                      @JsonProperty("radxRad") AugmentedProgramMapping radxRadMapping,
                      @JsonProperty("radxTech") AugmentedProgramMapping radxTechMapping,
                      @JsonProperty("radxDht") AugmentedProgramMapping radxDhtMapping) {

    public Mapping(@JsonProperty("radxUp") AugmentedProgramMapping radxUpMapping, @JsonProperty("radxRad") AugmentedProgramMapping radxRadMapping, @JsonProperty("radxTech") AugmentedProgramMapping radxTechMapping, @JsonProperty("radxDht") AugmentedProgramMapping radxDhtMapping) {
        this.radxUpMapping = checkMapping(radxUpMapping, RadxProgram.RADX_UP);
        this.radxRadMapping = checkMapping(radxRadMapping, RadxProgram.RADX_RAD);
        this.radxTechMapping = checkMapping(radxTechMapping, RadxProgram.RADX_TECH);
        this.radxDhtMapping = checkMapping(radxDhtMapping, RadxProgram.RADX_DHT);
    }

    private static AugmentedProgramMapping checkMapping(AugmentedProgramMapping programMapping,
                                RadxProgram expectedProgram) {
        if(!programMapping.program().equals(expectedProgram)) {
            throw new IllegalArgumentException("Expected " + expectedProgram + " but got " + programMapping.program());
        }
        return programMapping;
    }

    public static Mapping get(AugmentedProgramMapping radxUpMapping,
                              AugmentedProgramMapping radxRadMapping,
                              AugmentedProgramMapping radxTechMapping,
                              AugmentedProgramMapping radxDhtMapping) {
        return new Mapping(radxUpMapping, radxRadMapping, radxTechMapping, radxDhtMapping);

    }

    public static Mapping empty() {
        return get(AugmentedProgramMapping.of(RadxProgram.RADX_UP),
                AugmentedProgramMapping.of(RadxProgram.RADX_RAD),
                AugmentedProgramMapping.of(RadxProgram.RADX_TECH),
                AugmentedProgramMapping.of(RadxProgram.RADX_DHT)
                );
    }

    @JsonIgnore
    public boolean isMultiProgramMapping() {
        return getNonEmptyMappings().count() > 1;
    }

    @JsonIgnore
    public int getNumberOfHarmonizedPrograms() {
        return (int) getProgramMappings().stream().filter(pm -> !pm.isEmpty()).count();
    }

    @JsonIgnore
    public List<AugmentedProgramMapping> getProgramMappings() {
        return List.of(radxUpMapping, radxRadMapping, radxTechMapping, radxDhtMapping);
    }

    @JsonIgnore
    public Stream<AugmentedProgramMapping> getNonEmptyMappings() {
        return getProgramMappings().stream().filter(pm -> !pm.isEmpty());
    }

    @JsonIgnore
    public boolean isEmpty() {
        return getProgramMappings().stream().allMatch(AugmentedProgramMapping::isEmpty);
    }

    @JsonIgnore
    public Set<String> getHarmonizedIds() {
        return getProgramMappings().stream().flatMap(m -> m.dataElements().stream().map(MappedRADxDataDictionaryRecord::id)).collect(Collectors.toSet());
    }

    public static Mapping getMappingForRecord(DataDictionaryMapping dataDictionaryMapping, RADxDataDictionaryRecord r, EnumerationMappingOracle enumerationMappingOracle) {
        if(dataDictionaryMapping == null) {
            return Mapping.empty();
        }
        var mappingRecord = dataDictionaryMapping.get(r.id());
        if(mappingRecord.isEmpty()) {
            return Mapping.empty();
        }
        var theMappingRecord = mappingRecord.get();
        var radxUp = toAugmentedProgramMapping(r.id(), theMappingRecord.getMapping(RadxProgram.RADX_UP), dataDictionaryMapping, enumerationMappingOracle);
        var radxRad = toAugmentedProgramMapping(r.id(), theMappingRecord.getMapping(RadxProgram.RADX_RAD), dataDictionaryMapping, enumerationMappingOracle);
        var radxTech = toAugmentedProgramMapping(r.id(), theMappingRecord.getMapping(RadxProgram.RADX_TECH), dataDictionaryMapping, enumerationMappingOracle);
        var radxDht = toAugmentedProgramMapping(r.id(), theMappingRecord.getMapping(RadxProgram.RADX_DHT), dataDictionaryMapping, enumerationMappingOracle);
        return Mapping.get(radxUp, radxRad, radxTech, radxDht);
    }

    private static AugmentedProgramMapping toAugmentedProgramMapping(String toId, ProgramMapping programMapping, DataDictionaryMapping dataDictionaryMapping, EnumerationMappingOracle enumerationMappingOracle) {
        var recs = programMapping.dataElements()
                .stream()
                .flatMap(id -> dataDictionaryMapping.getMappedRecordsForProgram(id + "(_\\d+)?", programMapping.program()).stream())
                .map(record -> {
                    var mapping = enumerationMappingOracle.getMapping(programMapping.program(), record.id(), toId);
                    return MappedRADxDataDictionaryRecord.fromRADxDataDictionaryRecord(record, mapping);
                })
                .collect(Collectors.toList());
        return new AugmentedProgramMapping(programMapping.program(),
                recs);
    }
}
