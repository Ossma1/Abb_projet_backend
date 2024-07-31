package com.example.abb.services;


import com.example.abb.dto.ExcelData;
import com.example.abb.exception.MissingHeaderException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelService<T extends ExcelData> {
    boolean isValidExcelFile(MultipartFile file);
    T readExcel(MultipartFile file, String etat, String exercice, String etablissement) throws IOException, MissingHeaderException;
}