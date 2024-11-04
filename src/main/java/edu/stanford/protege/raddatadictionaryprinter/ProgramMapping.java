package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProgramMapping(@JsonProperty("program") RadxProgram program,
                             @JsonProperty("dataElements") List<String> dataElements) {

    public ProgramMapping {
        boolean containsBlankIds = dataElements.stream().anyMatch(String::isBlank);
        if (containsBlankIds) {
            throw new IllegalArgumentException("ProgramMapping cannot contain blank ids");
        }
    }

    public static ProgramMapping of(RadxProgram radxProgram) {
        return new ProgramMapping(radxProgram, List.of());
    }

    @JsonIgnore
    public boolean isEmpty() {
        return dataElements.isEmpty();
    }
}
