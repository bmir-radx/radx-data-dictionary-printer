package edu.stanford.protege.raddatadictionaryprinter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

public class DescriptionGenerator {

    private static String getCommaSeparatedListOfHarmonizedPrompts(DataDictionaryMapping dataDictionaryMapping, AugmentedProgramMapping pm, MappedRADxDataDictionaryRecord mappedId) {
        var mappedRecs = pm.dataElements();
        if (!mappedRecs.isEmpty()) {
            return mappedRecs.stream()
                    .map(MappedRADxDataDictionaryRecord::label)
                    .map(p -> p.replaceAll("\\s\\s\\s*", " "))
                    .map("- \"%s\""::formatted)
                    .collect(Collectors.joining(", ")) + " (" + pm.program().label() + ")";
        } else {
            return "";
        }
    }

    private static boolean isMappingForAllPrograms(List<ProgramMapping> harmonizationMapping) {
        return harmonizationMapping.stream().map(ProgramMapping::program).collect(Collectors.toSet()).size() == 4;
    }

    public String generateDescription(AugmentedDataDictionaryRecord record,
                                      SectionedDataDictionary dataDictionary,
                                      DataDictionaryMapping dataDictionaryMapping) {
        var id = record.id();
        var label = record.label();

        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        var equivPromptsMsg = record.mapping().isMultiProgramMapping() ? ", or a congruently meaning prompt," : "";
        pw.format("The `%s` data element records responses to the prompt" + equivPromptsMsg + " \"%s\".", id, label);
        if(record.mapping().getNumberOfHarmonizedPrograms() > 1) {

            var harmonizedPrompts =
                    record.mapping().getNonEmptyMappings()
                            .map(pm -> pm.dataElements()
                                    .stream()
                                    .map(mappedRec -> getCommaSeparatedListOfHarmonizedPrompts(dataDictionaryMapping, pm, mappedRec))
                                    .filter(p -> !p.isBlank())
                                    .collect(Collectors.joining(", ")))
                            .filter(v -> !v.isBlank())
                            .collect(Collectors.joining("\n"));

            pw.format("  Equivalent prompts are: \n\n%s\n\n", harmonizedPrompts);
        }

        var enumeration = record.enumeration();
        var datatypeSingular = record.datatype().value();
        var datatypePlural = record.datatype().value() + "s";
        if (enumeration != null) {
            pw.format("The values for this data element are %s that come from a list of %d permissible %s values.\n\n", datatypePlural,
                    enumeration.choices().size(),
                    datatypeSingular);
        } else {
            pw.format("The values for this data element are %s.\n\n", datatypePlural);
        }


        var harmonizationMapping = record.mapping();
        if (!harmonizationMapping.isEmpty()) {
            var hm = harmonizationMapping
                    .getNonEmptyMappings()
                    .map(this::generateHarmonizedDataElementsDescription)
                    .filter(text -> !text.isBlank())
                    .collect(Collectors.joining("; "));
            pw.format("""
                    The `%s` data element is the result of the harmonization of other RADx program data elements: %s
                    """, id, hm);

            var negChoices = record.getEnumeration()
                    .stream()
                    .flatMap(e -> e.choices().stream())
                    .filter(e -> e.label().equalsIgnoreCase("other")
                            || e.label().toLowerCase().contains("none of the above")
                            || e.label().toLowerCase().contains("none of these"))
                    .toList();
            // Need to add a caution if there are choices that correspond to the negation of other choices
            if (harmonizationMapping.isMultiProgramMapping()) {
                if (negChoices.size() == 1) {
                    var negChoice = negChoices.getFirst();
                    pw.format("""
                            
                            Note that the meaning of the value `%s` is context specific and depends on the subset of permissible \
                            value used by a specific study (not all studies presented the full set of permissible values documented here \
                            for this data element). \
                            Therefore, the meaning of the value `%s` in an individual data record MUST be \
                            determined by referring to the original data dictionary for the non-harmonized data in that study.
                            """, negChoice.value(), negChoice.value());
                } else if (negChoices.size() > 1) {
                    var negChoicesRendering = negChoices.stream().map(ch -> "`" + ch.value() + "`").collect(Collectors.joining(", "));
                    var negChoicesValuesRendering = negChoices.stream().map(ch -> "`" + ch.value() + "`").collect(Collectors.joining(", "));
                    pw.format("""
                            
                            Note that the meaning of the values %s is context specific and depends on the subset of enumerated values \
                            used by a specific study (not all studies presented the full set of permissible values documented here \
                            for this data element). \
                            The meaning of any of the values %s in an individual data record MUST therefore be \
                            determined by referring to the original data dictionary for the non-harmonized data in that study.
                            """, negChoicesRendering, negChoicesValuesRendering);
                }
            }

        }


        pw.flush();
        return sw.toString();
    }

    private String generateHarmonizedDataElementsDescription(AugmentedProgramMapping programMapping) {
        if (programMapping.isEmpty()) {
            return "";
        }
        return programMapping.dataElements().stream()
                .map("`%s`"::formatted)
                .collect(Collectors.joining(", ")) + " (%s)".formatted(programMapping.program().label());
    }
}
