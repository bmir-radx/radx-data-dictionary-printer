package edu.stanford.protege.raddatadictionaryprinter;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.bmir.radx.datadictionary.lib.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DataDictionaryParser {

    private final RADxDataDictionaryParser dataDictionaryParser;

    private final ObjectMapper objectMapper;

    public DataDictionaryParser(RADxDataDictionaryParser dataDictionaryParser, ObjectMapper objectMapper) {
        this.dataDictionaryParser = dataDictionaryParser;
        this.objectMapper = objectMapper;
    }

    public RADxDataDictionary parseDataDictionary(InputStream inputStream) throws RADxDataDictionaryParseException, IOException {
        var dict = dataDictionaryParser.parse(inputStream, ParseMode.STRICT);
        return dict;
    }
}
