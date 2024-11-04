package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.EnumerationChoice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EnumerationMappingOracle {

    private final List<EnumerationMappingRecord> records;

    public EnumerationMappingOracle(List<EnumerationMappingRecord> records) {
        this.records = new ArrayList<>(records);
    }


    public List<EnumerationChoiceMapping> getMapping(RadxProgram program,
                                                                String fromVariableIdRegex,
                                                                String toVariableId) {
        var fromVariablePattern = Pattern.compile(fromVariableIdRegex);
        return records.stream()
                .filter(r -> r.program().equals(program))
                .filter(r -> fromVariablePattern.matcher(r.fromVariable()).matches())
                .filter(r -> r.toVariable().equals(toVariableId))
                .map(r -> {
                    var from = new EnumerationChoice(r.fromValue(), r.fromLabel(), "");
                    var to = new EnumerationChoice(r.toValue(), r.toLabel(), "");
                    return new EnumerationChoiceMapping(from, to);
                })
                .toList();
    }

}
