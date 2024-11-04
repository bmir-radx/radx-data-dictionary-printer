package edu.stanford.protege.raddatadictionaryprinter;

import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Renderer {



    public SectionedDataDictionary render(SectionedDataDictionary in) {
        var renderedSections = in.sections().stream()
                .map(section -> {
                    var renderedRecords = section.records()
                            .stream()
                            .map(r -> render(r, in))
                            .toList();
                    return new DataDictionarySection(section.sectionName(), renderedRecords);
                })
                .toList();
        return new SectionedDataDictionary(renderedSections);
    }

    private AugmentedDataDictionaryRecord render(AugmentedDataDictionaryRecord record, SectionedDataDictionary sectionedDataDictionary) {
        return new AugmentedDataDictionaryRecord(
                record.number(),
                record.id(),
                List.of(),
                record.label(),
                render(record.description(), record, sectionedDataDictionary),
                List.of(),
                record.section(),
                record.terms(),
                record.cardinality(),
                record.datatype(),
                record.pattern(),
                record.unit(),
                record.enumeration(),
                record.missingValueCodes(),
                render(record.notes(), record, sectionedDataDictionary),
                record.provenance(),
                record.seeAlso(),
                record.mapping()
        );
    }

    private static String render(String markdown, AugmentedDataDictionaryRecord record, SectionedDataDictionary sectionedDataDictionary) {
        if(markdown == null) {
            return "";
        }


        var harmonizedIds = record.mapping().getHarmonizedIds();
        var versionSuffixPattern = Pattern.compile("(_\\d+)$");
        for(var harmonizedId : harmonizedIds) {
            var strippedId = harmonizedId;
            var matcher = versionSuffixPattern.matcher(harmonizedId);
            if(matcher.find()) {
                strippedId = harmonizedId.substring(0, matcher.start());
            }
            markdown = markdown.replaceAll("`" + strippedId + "`", "<span class=\"harmonized-id badge\">" + harmonizedId + "</span>");
        }

        for(var id : sectionedDataDictionary.getIds()) {
            markdown = markdown.replace("`" + id + "`", "<a href=\"#" + id + "\" class=\"record__id badge\">" + id + "</a>");

        }


        for(var program : RadxProgram.values()) {
            markdown = renderRadxProgram(program, markdown);
        }

        markdown = renderChoices(markdown, record);


        try {
            Parser parser = Parser.builder().extensions(List.of(AutolinkExtension.create())).build();
            Node document = parser.parse(markdown);
            HtmlRenderer renderer = HtmlRenderer.
                    builder()
                    .build();
            return renderer.render(document);
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    private static String renderRadxProgram(RadxProgram program, String markdown) {
        String programName = program.label();
        String programFullName = program.fullName();
        return markdown.replaceAll("\\(" + programName + "\\)", "<span title=\"" + programName + " (" + programFullName + ")\" class=\"radx-program badge badge-pill\">" + programName + "</span>");
    }

    private static String renderChoices(String markdown, AugmentedDataDictionaryRecord record) {
        var choices = record.getEnumeration().map(AugmentedEnumeration::choices).orElse(List.of());
        for(var choice : choices) {
            var extraClass = choice.isMissingValueCode(record.enumeration()) ?  "choice__value--missing-value-code" : "";
            markdown = markdown.replace("`" + choice.value() + "`", "<span class=\"badge choice__value " + extraClass + "\" title=\"" + choice.value() + "\">" + choice.value() + "</span>");
        }
        return markdown;

    }
}

