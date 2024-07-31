package com.example.abb.controller;

import com.example.abb.dto.axesEntities.Axes;
import com.example.abb.models.BkamEntity;
import com.example.abb.repositories.ModelRepo.BkamEntityRepository;
import com.example.abb.services.generale.BkamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class BkamController {
    @Autowired
    private BkamEntityRepository bkamEntityRepository;
    @Autowired
    private BkamService bkamService;

    @Operation(
            summary = "Get BKAM entities",
            description = "Returns a list of BKAM entities. Optionally, an ID can be provided to fetch a specific entity."
    )
    @GetMapping("/bkams")
    public Map<String, Object> getBkams(@RequestParam(required = false) String id) {
        Map<String, Object> response = new HashMap<>();
        List<BkamEntity> bkams;

        if (id != null) {
            Optional<BkamEntity> optionalBkam = bkamEntityRepository.findById(Long.valueOf(id));
            bkams = optionalBkam.map(Collections::singletonList).orElse(Collections.emptyList());
        } else {
            bkams = bkamEntityRepository.findAll();
        }
        response.put("response", bkams);
        response.put("success", true);
        response.put("message", "Success!");

        return response;
    }

    @Operation(
            summary = "Get Axes by BKAM entity ID",
            description = "Returns the axes associated with a specific BKAM entity ID."
    )
    @GetMapping("/bkam/{bkamId}/axes")
    public ResponseEntity<Map<String, Object>> getAxesByBkamEntityId(@PathVariable Long bkamId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> axes = bkamService.getAxesByBkamEntityId(bkamId);
        if (axes != null) {
            response.put("success", true);
            response.put("message", "Axes retrieved successfully");
            response.put("data", axes);
        } else {
            response.put("success", false);
            response.put("message", "No axes found for the given ID");
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get anomalies by BKAM entity ID",
            description = "Returns the anomalies associated with a specific BKAM entity ID."
    )
    @GetMapping("/bkam/{bkamId}/anomalies")
    public ResponseEntity<Map<String, Object>> getAnomalies(@PathVariable Long bkamId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> anomalies = bkamService.getAnomaliesByBkamEntityId(bkamId);
        if (anomalies != null) {
            response.put("success", true);
            response.put("message", "Anomalies retrieved successfully");
            response.put("data", anomalies);
        } else {
            response.put("success", false);
            response.put("message", "No Anomalies found for the given ID");
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete an axe",
            description = "Deletes a specific axe associated with a BKAM entity ID."
    )
    @DeleteMapping("/bkam/{bkamEntityId}/axes/{axetId}")
    public ResponseEntity<Map<String, Object>> deleteAxe(
            @PathVariable Long bkamEntityId,
            @PathVariable Long axetId,
            @RequestParam(name = "type", required = false) String type) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = bkamService.deleteAxeBkamId(bkamEntityId, axetId, type);
        if (result.containsKey("success")) {
            response.put("success", true);
            response.put("message", result.get("success"));
        } else {
            response.put("success", false);
            response.put("error", result.get("error"));
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add an axe",
            description = "Adds a new axe to a specific BKAM entity ID."
    )
    @PostMapping("/bkam/{bkamEntityId}/axes")
    public ResponseEntity<Map<String, Object>> addAxe(
            @PathVariable Long bkamEntityId,
            @RequestParam(name = "type", required = false) String type,
            @RequestBody Map<String, Object> axeData) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = bkamService.addAxe(bkamEntityId, axeData, type);
        if (result.containsKey("success")) {
            response.put("success", true);
            response.put("message", result.get("success"));
        } else {
            response.put("success", false);
            response.put("error", result.get("error"));
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Save data",
            description = "Saves data for a specific BKAM entity ID."
    )
    @GetMapping("/bkam/{bkamEntityId}/axes/save")
    public ResponseEntity<Map<String, Object>> saveData(
            @PathVariable Long bkamEntityId,
            @RequestParam(name = "type", required = false) String type) throws Exception {
        System.err.println("saveData");
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = bkamService.saveData(bkamEntityId, type);
        if (result.containsKey("success")) {
            response.put("success", true);
            response.put("message", result.get("success"));
        } else {
            response.put("success", false);
            response.put("message", result.get("error"));
        }
        return ResponseEntity.ok(response);
    }
}
