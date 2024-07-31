package com.example.abb.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodeLigne704 {
    public static final Map<String, String> codeMapping = new LinkedHashMap<>();

    static {
        codeMapping.put("CL-01", "CL01"); // CL-01
        codeMapping.put("CL-01-1", "CL02"); // CL-01-1
        codeMapping.put("CL-01-2", "CL03"); // CL-01-2
        codeMapping.put("CL-01-3", "CL04"); // CL-01-3
        codeMapping.put("CL-01-4", "CL05"); // CL-01-4
        codeMapping.put("CL-01-4-1", "CL06"); // CL06
        codeMapping.put("CL-01-4-2", "CL07"); // CL07


        codeMapping.put("PDT-01", "PP01"); // PP01
        codeMapping.put("PDT-01-1", "PP02"); // PP02
        codeMapping.put("PDT-02", "PP03"); // PP03
        codeMapping.put("PDT-02-1", "PP04"); // PP04

        codeMapping.put("TR-01", "TR01"); // TR01
        codeMapping.put("TR-02", "TR02"); // TR-02
        codeMapping.put("TR-03", "TR03"); // TR-03
        codeMapping.put("TR-04", "TR04"); // TR-04
        codeMapping.put("TR-05", "TR05"); // TR-05
        codeMapping.put("TR-06", "TR06"); // TR-06
        codeMapping.put("TR-07", "TR07"); // TR-07

        codeMapping.put("CD-01", "CD01"); // CD-01
        codeMapping.put("CD-02", "CD02"); // CD-02
        codeMapping.put("GEO-01", "GE01"); // GEO-01
        codeMapping.put("GEO-02", "GE02"); // GE01
        codeMapping.put("Autres-01", "AT01"); // GE01

    }

}