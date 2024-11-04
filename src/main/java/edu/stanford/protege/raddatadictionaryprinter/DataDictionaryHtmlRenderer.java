package edu.stanford.protege.raddatadictionaryprinter;

import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionary;
import org.semanticweb.owlapi.model.OWLOntology;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class DataDictionaryHtmlRenderer implements DataDictionaryRenderer {

    private final SpringTemplateEngine templateEngine;

    public DataDictionaryHtmlRenderer(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void render(String title, RADxDataDictionary radDictiory, DataDictionaryMapping dataDictionaryMapping, OutputStream outputStream, TermOracle termOracle, EnumerationMappingOracle enumerationMappingOracle) throws IOException {
        var sectionedDict = SectionedDataDictionary.fromDataDictionary(radDictiory, dataDictionaryMapping, termOracle, enumerationMappingOracle);
        var markdownDict = new Renderer().render(sectionedDict);

        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("dict", markdownDict);
        context.setVariable("name", "Hello");
        context.setVariable("dictMapping", dataDictionaryMapping);
        var is = getClass().getResourceAsStream("/template.html").readAllBytes();

        var template = new String(is, StandardCharsets.UTF_8);
        var v = templateEngine.process(template, context);
        System.out.println(v);
        outputStream.write(v.getBytes(StandardCharsets.UTF_8));
    }
}
