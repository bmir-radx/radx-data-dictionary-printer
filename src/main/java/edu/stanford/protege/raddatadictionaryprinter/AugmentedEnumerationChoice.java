package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.EnumerationChoice;
import jakarta.annotation.Nonnull;

import java.util.regex.Pattern;

public record AugmentedEnumerationChoice(@Nonnull String value, @Nonnull String label, @Nonnull String iri) {

    public static final Pattern INTEGER_VALUE_PATTERN = Pattern.compile("\\d+");

    public static AugmentedEnumerationChoice from(EnumerationChoice choice) {
        return new AugmentedEnumerationChoice(choice.value(), choice.label(), choice.iri());
    }

    public boolean isMissingValueCode(AugmentedEnumeration parentEnumeration) {
        var count = parentEnumeration.choices().size();
        // This is a little arbitrary
        var cutOff = count * 2;
        return INTEGER_VALUE_PATTERN.matcher(value).matches()
                && Integer.parseInt(value) > cutOff;
    }
}
