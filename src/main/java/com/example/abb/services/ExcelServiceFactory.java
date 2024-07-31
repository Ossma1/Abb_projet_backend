package com.example.abb.services;

import com.example.abb.dto.ExcelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExcelServiceFactory {

    private final Map<String, ExcelService<? extends ExcelData>> services = new HashMap<>();

    @Autowired
    public ExcelServiceFactory(E700Service e700Service,PPPService ppService, FfService bfService, CBBService cBService) {
        services.put("700", e700Service);
        services.put("701", ppService);
        services.put("702", ppService);
        services.put("703", bfService);
        services.put("704", bfService);
        services.put("705", cBService);
        services.put("707", bfService);
        services.put("708", bfService);
        services.put("709", bfService);
        services.put("710", bfService);
        services.put("711", bfService);
        services.put("712", bfService);
        services.put("713", bfService);
    }
    public ExcelService<? extends ExcelData> getService(String sheetName) {
        return services.get(sheetName);
    }
}