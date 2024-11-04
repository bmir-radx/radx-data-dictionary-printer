package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.bmir.radx.datadictionary.lib.Enumeration;

import java.util.List;

public record AugmentedEnumeration(@JsonIgnore List<AugmentedEnumerationChoice> choices) {

    public static AugmentedEnumeration from(Enumeration enumeration) {
        if(enumeration == null) {
            return null;
        }
        return new AugmentedEnumeration(enumeration.choices()
                .stream()
                .map(AugmentedEnumerationChoice::from)
                .toList());
    }

    @JsonValue
    List<AugmentedEnumerationChoice> getChoices() {
        return choices;
    }


}
