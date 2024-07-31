package com.example.abb;


import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.example.abb.controller.ImportFileController;
import com.example.abb.services.PPPService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImportFileController.class)
public class ImportFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PPPService ppService;

    @Test
    public void testUploadExcelFile_ValidFile() throws Exception {
        Path path = Paths.get("C:/Users/ousss/Downloads/Al Barid Bank_701_test.xlsx");
        byte[] content = Files.readAllBytes(path);

        // Création d'un MockMultipartFile à partir du fichier local
        MockMultipartFile file = new MockMultipartFile("file", "Al Barid Bank_701_test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

        when(ppService.isValidExcelFile(file)).thenReturn(true);
        when(ppService.readExcel(file,"Risque inhérent \" Correspondance bancaire\"","2021","")).thenReturn(new BanqueParticuliersProfessionnels());

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File processed successfully."));
    }

    @Test
    public void testUploadExcelFile_InvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "Invalid file content".getBytes());

        when(ppService.isValidExcelFile(file)).thenReturn(false);

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid file type. Please upload an Excel file in .xlsx format."));
    }

    @Test
    public void testUploadExcelFile_ProcessingError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[]{});

        when(ppService.isValidExcelFile(file)).thenReturn(true);
        when(ppService.readExcel(file,"Risque inhérent \" Correspondance bancaire\"","2021","")).thenThrow(new RuntimeException("Processing error"));

        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while processing the file: Processing error"));
    }
}
