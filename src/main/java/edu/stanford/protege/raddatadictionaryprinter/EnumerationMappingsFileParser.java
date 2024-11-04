package edu.stanford.protege.raddatadictionaryprinter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class EnumerationMappingsFileParser {

    public List<EnumerationMappingRecord> parse(String in) throws IOException {
        var parser = new CSVParser(new StringReader(in), CSVFormat.DEFAULT);
        return parser.stream()
                .skip(1)
                .map(record -> new EnumerationMappingRecord(
                        RadxProgram.parse(record.get(0)).orElseThrow(),
                        record.get(1),
                        record.get(2),
                        record.get(3),
                        record.get(4),
                        record.get(5),
                        record.get(6)
                ))
                .toList();
    }
}
