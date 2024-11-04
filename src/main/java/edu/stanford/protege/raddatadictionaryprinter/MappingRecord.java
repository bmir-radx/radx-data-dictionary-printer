package edu.stanford.protege.raddatadictionaryprinter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record MappingRecord(String id,
                            List<ProgramMapping> mappings) {

    public MappingRecord {
        if(mappings.size() != 4) {
            throw new IllegalArgumentException("mappings must contain exactly four elements");
        }
        if(!mappings.get(0).program().equals(RadxProgram.RADX_UP)) {
            throw new IllegalArgumentException("first program must be UP");
        }
        if(!mappings.get(1).program().equals(RadxProgram.RADX_RAD)) {
            throw new IllegalArgumentException("first program must be RAD");
        }
        if(!mappings.get(2).program().equals(RadxProgram.RADX_TECH)) {
            throw new IllegalArgumentException("first program must be UP");
        }
        if(!mappings.get(3).program().equals(RadxProgram.RADX_DHT)) {
            throw new IllegalArgumentException("first program must be UP");
        }
    }

    public ProgramMapping getMapping(RadxProgram program) {
        return switch (program) {
            case RADX_UP -> mappings.get(0);
            case RADX_RAD -> mappings.get(1);
            case RADX_TECH -> mappings.get(2);
            case RADX_DHT -> mappings.get(3);
        };
    }

    public Set<String> getMappedIds() {
        return mappings.stream()
                .flatMap(m -> m.dataElements().stream())
                .collect(Collectors.toSet());
    }
}
