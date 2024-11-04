package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;
import java.util.stream.Stream;

public enum RadxProgram {


    @JsonProperty("RADx-UP")
    RADX_UP("RADx-UP", "RADx Underserved Populations"),

    @JsonProperty("RADx-rad")
    RADX_RAD("RADx-rad", "RADx Radical"),

    @JsonProperty("RADx-Tech")
    RADX_TECH("RADx Tech", "RADx Tech"),

    @JsonProperty("RADx-DHT")
    RADX_DHT("RADx DHT", "RADx Digital Health Technologies"),;

    private final String label;

    private final String fullName;

    RadxProgram(String label, String fullName) {
        this.label = label;
        this.fullName = fullName;
    }

    public static Optional<RadxProgram> parse(String s) {
        return Stream.of(RadxProgram.values())
                .filter(p -> p.label.equals(s))
                .findFirst();
    }

    public String label() {
        return label;
    }

    public String fullName() {
        return fullName;
    }
}
