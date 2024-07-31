package com.example.abb.controller;

import com.example.abb.dto.ExcelData;
import com.example.abb.exception.MissingHeaderException;
import com.example.abb.services.ExcelService;
import com.example.abb.services.ExcelServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ImportFileController {
    @Autowired
    private ExcelServiceFactory excelServiceFactory;

    @Operation(summary = "Upload an Excel file", description = "Endpoint for uploading an Excel file")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadExcelFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("etat") String etat,
            @RequestParam("exercice") String exercice,
            @RequestParam("etablissement") String etablissement) {
        Map<String, String> response = new HashMap<>();
        if (!etat.contains("7")) {
            response.put("message", "État incorrect");
            return ResponseEntity.badRequest().body(response);
        }
        String etatExtraire = etat.substring(etat.indexOf('7'));
        ExcelService<? extends ExcelData> excelService = excelServiceFactory.getService(etatExtraire);
        if (excelService == null) {
            response.put("message", "Excel service not available");
            return ResponseEntity.badRequest().body(response);
        }
        if (!excelService.isValidExcelFile(file)) {
            response.put("message", "Invalid file type. Please upload a valid Excel file.");
            return ResponseEntity.badRequest().body(response);
        }
        try (InputStream fis = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(fis)) {
            ExcelData PP = excelService.readExcel(file, etatExtraire, exercice, etablissement);
            if (PP != null) {
                response.put("message", "File processed successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to process the file.");
                return ResponseEntity.status(500).body(response);
            }
        } catch (MissingHeaderException e) {
             response.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while processing the file: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

