package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionary;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryParseException;
import edu.stanford.bmir.radx.datadictionary.lib.RADxDataDictionaryParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SpringBootApplication
@ComponentScan(basePackages = "edu.stanford.bmir.radx.datadictionary")
public class RadDataDictionaryPrinterApplication implements CommandLineRunner {

    @Autowired
    ApplicationContext context;

    public static void main(String[] args) throws IOException, RADxDataDictionaryParseException {
        SpringApplication.run(RadDataDictionaryPrinterApplication.class, args);
    }

    @Bean
    DataDictionaryParser dataDictionaryParser(RADxDataDictionaryParser p,
                                              ObjectMapper o) {
        return new DataDictionaryParser(p, o);
    }


    @Bean
    protected SpringTemplateEngine springTemplateEngine() {
        return new SpringTemplateEngine();
    }

    @Override
    public void run(String... args) throws Exception {
        Path path = Path.of(args[0]);

        var dd = parseDataDictionary(path);
        var ddm = getDataDictionaryMapping(args).orElse(null);
        var templateEngine = context.getBean(SpringTemplateEngine.class);

        var enumerationMappingParser = new EnumerationMappingsFileParser();
         var list = enumerationMappingParser.parse(Files.readString(Path.of("enumeration-mappings.csv")));

        var enumerationMappingOracle = new EnumerationMappingOracle(list);


//        render(dd, new DataDictionaryMarkdownRenderer(), Path.of("/tmp/dd.md"), ddm);
        Collection<OWLOntology> referenceOntologies = getReferenceOntologies();

        var termOracle = new TermOracle(referenceOntologies);

        var objectMapper = context.getBean(ObjectMapper.class);
        var inFileName = path.getFileName().toString();
        var inFileNameWithoutExtension = inFileName.substring(0, inFileName.length() - 4);

        render(dd, new DataDictionaryJsonRenderer(objectMapper), Path.of(inFileNameWithoutExtension + ".json"), ddm, termOracle, enumerationMappingOracle);
        render(dd, new DataDictionaryHtmlRenderer(templateEngine), Path.of(inFileNameWithoutExtension + ".html"), ddm, termOracle, enumerationMappingOracle);

//
//        var csvWriter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(Path.of("/tmp/descs.csv").toFile()), StandardCharsets.UTF_8), CSVFormat.DEFAULT);
//        var sdd = SectionedDataDictionary.fromDataDictionary(dd, ddm, termOracle);
//        sdd.sections()
//                .forEach(sec -> {
//                    sec.records().forEach(rec -> {
//                        var id = rec.id();
//                        var desc = new DescriptionGenerator()
//                                .generateDescription(rec, sdd, ddm);
//                        try {
//                            csvWriter.printRecord(id, desc);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//                });
//        csvWriter.close();

    }

    private static Set<OWLOntology> getReferenceOntologies() throws OWLOntologyCreationException {
        var gcboOnts = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File("gcbo.owl")).getImportsClosure();
        var gssoOnt = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File("gsso.owl")).getImportsClosure();
        var hpOnt = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File("hp.obo")).getImportsClosure();
        var mondoOnt = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File("mondo.obo")).getImportsClosure();
        var result = new LinkedHashSet<OWLOntology>();
        result.addAll(gcboOnts);
        result.addAll(gssoOnt);
        result.addAll(hpOnt);
        result.addAll(mondoOnt);
        return result;
    }

    private RADxDataDictionary parseDataDictionary(Path path) throws RADxDataDictionaryParseException, IOException {
        var p = context.getBean(DataDictionaryParser.class);
        return p.parseDataDictionary(new FileInputStream(path.toRealPath().toFile()));
    }

    @Bean
    public MappingFileParser mappingFileParser() {
        return new MappingFileParser();
    }

    private Optional<DataDictionaryMapping> getDataDictionaryMapping(String[] args) throws IOException, RADxDataDictionaryParseException {
        if(args.length < 2) {
            return Optional.empty();
        }
        var mappingRecs = (context.getBean(MappingFileParser.class).parse(new FileInputStream(Path.of(args[1]).toRealPath().toFile())));
        var referenceDataDictionaries = new ArrayList<RADxDataDictionary>();
        for(int i = 2; i < args.length; i++) {
            var refPath = Path.of(args[i]);
            try {
                var refFile = parseDataDictionary(refPath);
                referenceDataDictionaries.add(refFile);
            } catch (RADxDataDictionaryParseException e) {
                System.err.println(e.getMessage());
            }
        }
        var upDD = parseDataDictionary(Path.of("up.dd.csv"));
        var radDD = parseDataDictionary(Path.of("rad.dd.csv"));
        var techDD = parseDataDictionary(Path.of("tech.dd.csv"));
        var dhtDD = parseDataDictionary(Path.of("dht.dd.csv"));

        var ddm = DataDictionaryMapping.create(mappingRecs, List.of(new MappedDataDictionary(RadxProgram.RADX_UP, upDD),
                new MappedDataDictionary(RadxProgram.RADX_RAD, radDD),
                new MappedDataDictionary(RadxProgram.RADX_TECH, techDD),
                new MappedDataDictionary(RadxProgram.RADX_DHT, dhtDD)));
        return Optional.of(ddm);
    }

    private static void render(RADxDataDictionary dd,
                               DataDictionaryRenderer renderer,
                               Path out,
                               DataDictionaryMapping mapping,
                               TermOracle termOracle,
                               EnumerationMappingOracle enumerationMappingOracle) throws IOException {


        var os = new ByteArrayOutputStream();
        renderer.render("RADx Data Dictionary", dd, mapping, os, termOracle, enumerationMappingOracle);
        Files.write(out, os.toByteArray());
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .modules(new Jdk8Module());
    }
}
