package edu.stanford.protege.raddatadictionaryprinter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
public class MappingFileParser {

    public static void main(String[] args) throws IOException {
        MappingFileParser p = new MappingFileParser();
        p.parse(new FileInputStream(Path.of(args[0]).toFile()));
    }

    public List<MappingRecord> parse(InputStream inputStream) throws IOException {
        CSVParser parser = new CSVParser(new InputStreamReader(inputStream, StandardCharsets.UTF_8), CSVFormat.DEFAULT);
        var recs = parser.stream()
                .map(record -> {
                    // 0 is GCB
                    var id = record.get(0);
                    // 1 is -UP
                    var radxUp = record.get(1);
                    // 2 is -rad
                    var radxRad = record.get(2);
                    // 3 is -Tech
                    var radxTech = record.get(3);
                    // 4 is -DHT
                    var radxDht = record.get(4);
                    return new MappingRecord(id,
                            List.of(
                                    parseProgramIdList(RadxProgram.RADX_UP, radxUp),
                                    parseProgramIdList(RadxProgram.RADX_RAD, radxRad),
                                    parseProgramIdList(RadxProgram.RADX_TECH, radxTech),
                                    parseProgramIdList(RadxProgram.RADX_DHT, radxDht))
                            );
                })
                .toList();
        return recs;
    }

    private ProgramMapping parseProgramIdList(RadxProgram program,
                                              String value) {
        var ids = parseIdList(value);
        return new ProgramMapping(program, ids);
    }

    private List<String> parseIdList(String value) {
        var values = value.trim().split("[;,\\s]+");
        return Arrays.stream(values)
                .map(String::trim)
                .filter(m -> !m.isEmpty())
                .toList();
    }
}

