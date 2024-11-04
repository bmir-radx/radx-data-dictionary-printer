package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.stanford.bmir.radx.datadictionary.lib.TermIdentifier;

import java.util.*;

public record AugmentedTermIdentifier(String identifier,
                                      String lookUpUrl,
                                      String label,
                                      List<String> synonyms) {

    @JsonIgnore
    public String getQuotedLabel() {
        return label.isBlank() ? "" : "'" + label + "'";
    }

    public static AugmentedTermIdentifier fromTermIdentifier(TermIdentifier termIdentifier, TermOracle termOracle) {

        var synonyms = termOracle.getTermSyonyms(termIdentifier.identifier());

        var termLabel = termOracle.getTermLabel(termIdentifier.identifier());

        var allSyns = new TreeSet<String>(String::compareToIgnoreCase);
        allSyns.add(termLabel);
        allSyns.addAll(synonyms);

        var url = termOracle.getLookUpUrl(termIdentifier.identifier());
        return new AugmentedTermIdentifier(termIdentifier.identifier(), url, termLabel, new ArrayList<>(allSyns));
    }
}
