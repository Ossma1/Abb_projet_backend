package com.example.abb.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodeLigne707 {
    public static final Map<String, String> codeMapping = new LinkedHashMap<>();

    static {
        codeMapping.put("PDT-01", "PP01"); // CL-01
        codeMapping.put("PDT-02", "PP02"); // CL-01-1
        codeMapping.put("PDT-02-1", "PP03"); // CL-01-2
        codeMapping.put("PDT-02-2", "PP04"); // CL-01-3
        codeMapping.put("PDT-02-3", "PP05"); // CL-01-4
        codeMapping.put("PDT-03", "PP06"); // CL06
        codeMapping.put("PDT-03-1", "PP07"); // CL07
        codeMapping.put("PDT-03-2", "PP08"); // CL07
        codeMapping.put("PDT-03-3", "PP09"); // CL07

    }
}
