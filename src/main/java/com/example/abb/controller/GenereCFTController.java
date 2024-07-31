package com.example.abb.controller;

import com.example.abb.dto.FileResponse;
import com.example.abb.dto.GenerateFilesRequest;
import com.example.abb.exception.*;
import com.example.abb.models.BkamEntity;
import com.example.abb.repositories.ModelRepo.BkamEntityRepository;
import com.example.abb.services.GenereCFT.GenereCFTService;
import com.example.abb.services.convertion.Convertion703Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class GenereCFTController {
    @Autowired
    private GenereCFTService genereCFTService;

    @Operation(summary = "Download a file",description = "Returns a file as a downloadable attachment.")
    @GetMapping(value = "/generateFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponse> generateFile(@RequestParam("id") String id) {
        try {
            FileResponse fileResponse= genereCFTService.genereCFT(id);
            return ResponseEntity.ok().body(fileResponse);
        } catch (BkamNotFoundException e) {
            FileResponse fileResponse = new FileResponse("", "", "BKAM with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(fileResponse);
        } catch (IOException e) {
            FileResponse fileResponse = new FileResponse("", "", "An error occurred while generating the file.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fileResponse);
        }
    }
    @Operation(summary = "Download a file from multiple files Excel",description = "Returns a file as a downloadable attachment.")
    @PostMapping(value = "/generateFiles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponse> generateFiles(@RequestBody GenerateFilesRequest request) {
        try {
            List<String> ids = request.getBkamIds();
            FileResponse fileResponse = genereCFTService.generateFilesForListEtat(ids);
            return ResponseEntity.ok().body(fileResponse);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            HttpStatus status;

            if (e instanceof EmptyIdListException) {
                status = HttpStatus.BAD_REQUEST;
            } else if (e instanceof BkamNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            } else if (e instanceof DifferentExerciceDateException || e instanceof DifferentCodeDocumentException || e instanceof DifferentCodeEtablissementException) {
                status = HttpStatus.CONFLICT;
                System.err.println(e.getMessage());
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                errorMessage = "Une erreur est survenue lors de la génération des fichiers.";
            }

            FileResponse fileResponse = new FileResponse("", "", errorMessage);
            return ResponseEntity.status(status).body(fileResponse);
        }
    }
}
