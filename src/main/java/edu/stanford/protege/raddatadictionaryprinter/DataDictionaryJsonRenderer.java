package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionary;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;

@Component
public class DataDictionaryJsonRenderer implements DataDictionaryRenderer {

    private final ObjectMapper objectMapper;

    public DataDictionaryJsonRenderer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void render(String title, RADxDataDictionary dxDataDictionary, DataDictionaryMapping dataDictionaryMapping, OutputStream outputStream, TermOracle termOracle, EnumerationMappingOracle enumerationMappingOracle) throws IOException {
        var sectionedDataDictionary = AugmentedDataDictionary.fromDataDictionary(dxDataDictionary, dataDictionaryMapping, termOracle, enumerationMappingOracle);
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(outputStream, sectionedDataDictionary);
    }
}
