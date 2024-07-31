package com.example.abb.utility;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CodeLigne701 {
    public static final Map<String, String> codeMapping = new LinkedHashMap<>();

    static {
        codeMapping.put("CL1", "CL01");
        codeMapping.put("CL-01-1", "CL02");
        codeMapping.put("CL-01-2", "CL03");
        codeMapping.put("CL-01-3", "CL04");
        codeMapping.put("CL-01-4", "CL05");
        codeMapping.put("CL-01-4-1", "CL06");
        codeMapping.put("CL-01-4-2", "CL07");
        codeMapping.put("CL-05", "CL08");
        codeMapping.put("PDT-01", "PP01");
        codeMapping.put("PDT-01-01", "PP02");
        codeMapping.put("PDT-01-02", "PP03");
        codeMapping.put("PDT-02", "PP04");
        codeMapping.put("PDT-03", "PP05");
        codeMapping.put("PDT- 04", "PP06");
        codeMapping.put("PDT- 04-1", "PP07");
        codeMapping.put("TR-01", "TR01");
        codeMapping.put("TR-02", "TR02");
        codeMapping.put("TR-03", "TR03");
        codeMapping.put("TR-04", "TR04");
        codeMapping.put("TR-05", "TR05");
        codeMapping.put("TR-06", "TR06");
        codeMapping.put("TR-07", "TR07");
        codeMapping.put("TR-08", "TR08");
        codeMapping.put("TR-09", "TR09");
        codeMapping.put("TR-10", "TR10");
        codeMapping.put("TR-11", "TR11");
        codeMapping.put("TR-12", "TR12");
        codeMapping.put("CD-01", "CD01");
        codeMapping.put("CD-02", "CD02");
        codeMapping.put("CD-03", "CD03");
        codeMapping.put("GEO-01", "GE01");
        codeMapping.put("GEO-02", "GE02");
        codeMapping.put("GEO-03", "GE03");
        codeMapping.put("GEO-04", "GE04");
        codeMapping.put("Autres-01", "AT01");
    }

}
