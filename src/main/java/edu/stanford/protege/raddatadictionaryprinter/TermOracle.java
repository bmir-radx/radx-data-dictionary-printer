package edu.stanford.protege.raddatadictionaryprinter;

import com.google.common.base.Optional;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

import java.util.*;

public class TermOracle {

    public static final String OBO_PREFIX = "http://purl.obolibrary.org/obo/";
    private final Collection<OWLOntology> referenceOntologies;

    public TermOracle(Collection<OWLOntology> ontologies) {
        this.referenceOntologies = new LinkedHashSet<>(ontologies);
    }

    public String getLookUpUrl(String termId) {
        var colonIndex = termId.indexOf(":");
        if(colonIndex == -1) {
            return termId;
        }
        String acronym = termId.substring(0, colonIndex);
        return  "https://bioportal.bioontology.org/ontologies/" + acronym + "?p=classes&conceptid=" + termId;
    }

    private String getTermIri(String termId) {
        if(termId.startsWith("http")) {
            return termId;
        }
        var colonIndex = termId.indexOf(':');
        if(colonIndex == -1) {
            return termId;
        }
        else {
            var acronym = termId.substring(colonIndex+1);
            return OBO_PREFIX + termId.replace(":", "_");
        }
    }

    public String getTermLabel(String termId) {
        return referenceOntologies.stream()
                .flatMap(ont -> {
                    var termIri = getTermIri(termId);
                    return ont.getEntitiesInSignature(IRI.create(termIri))
                            .stream()
                            .flatMap(entity -> {
                                return ont.getAnnotationAssertionAxioms(entity.getIRI())
                                        .stream().filter(TermOracle::isLabellingAxiom)
                                        .map(ax -> ax.getValue().asLiteral())
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .map(OWLLiteral::getLiteral);
                            });
                }).findFirst()
                .orElse("");
    }

    private static boolean isLabellingAxiom(OWLAnnotationAssertionAxiom ax) {
        return ax.getProperty().isLabel() || SKOSVocabulary.PREFLABEL.getIRI().equals(ax.getProperty().getIRI());
    }

    public List<String> getTermSyonyms(String termId) {
        var termIri = getTermIri(termId);
        var collected = new TreeSet<String>(String::compareToIgnoreCase);
        return referenceOntologies.stream()
                .map(ont -> {
                    var entities = ont.getEntitiesInSignature(IRI.create(termIri));
                    return entities.stream()
                            .map(entity -> {
                                return ont.getAnnotationAssertionAxioms(entity.getIRI())
                                        .stream()
                                        .filter(TermOracle::isSynonymAxiom)
                                        .filter(ax -> ax.getValue().isLiteral())
                                        .map(ax -> (OWLLiteral) ax.getValue())
                                        .map(OWLLiteral::getLiteral)
                                        .toList();
                            })
                            .flatMap(List::stream)
                            .distinct()
                            .toList();
                })
                .flatMap(List::stream)
                .filter(collected::add)
                .sorted(String::compareToIgnoreCase)
                .toList();

    }

    private static boolean isSynonymAxiom(OWLAnnotationAssertionAxiom ax) {
        var propertyIri = ax.getProperty().getIRI();
        return propertyIri.toString().equals("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym") ||
                propertyIri.equals(SKOSVocabulary.ALTLABEL.getIRI());
    }
}
