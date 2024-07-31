package com.example.abb.dto;

import lombok.Data;

@Data
public class FileResponse {
    private String fileName;
    private String fileData;
    private String message;

    public FileResponse(String fileName, String fileData,String message) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.message = message;
    }

    // Getters and setters
}
