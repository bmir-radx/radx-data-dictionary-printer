package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionary;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public interface DataDictionaryRenderer {

    void render(String title, RADxDataDictionary dxDataDictionary, DataDictionaryMapping dataDictionaryMapping, OutputStream outputStream, TermOracle termOracle, EnumerationMappingOracle enumerationMappingOracle) throws IOException;
}
