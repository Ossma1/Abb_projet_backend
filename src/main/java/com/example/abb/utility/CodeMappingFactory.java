package com.example.abb.utility;

import java.util.HashMap;
import java.util.Map;

public class CodeMappingFactory {
    public static final Map<String, Map<String, String>> codeMappings = new HashMap<>();

    static {
        codeMappings.put("700", CodeLigne700.codeMapping);
        codeMappings.put("701", CodeLigne701.codeMapping);
        codeMappings.put("702", CodeLigne702.codeMapping);
        codeMappings.put("703", CodeLigne703.codeMapping);
        codeMappings.put("704", CodeLigne704.codeMapping);
        codeMappings.put("707", CodeLigne707.codeMapping);
        codeMappings.put("708", CodeLigne708.codeMapping);
        codeMappings.put("709", CodeLigne709.codeMapping);
        codeMappings.put("710", CodeLigne710.codeMapping);
        codeMappings.put("711", CodeLigne711.codeMapping);
        codeMappings.put("712", CodeLigne712.codeMapping);
        codeMappings.put("713", CodeLigne713.codeMapping);
    }

    public static Map<String, String> getCodeMapping(String etat) {
        return codeMappings.getOrDefault(etat, null);
    }
}