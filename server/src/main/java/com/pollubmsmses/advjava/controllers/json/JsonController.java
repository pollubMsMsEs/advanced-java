package com.pollubmsmses.advjava.controllers.json;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pollubmsmses.advjava.services.JsonService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JsonController {

    private final JsonService jsonService;
    
    @GetMapping("/export/json")
    public ResponseEntity<Resource> exportData() throws IOException {
        Path file = jsonService.exportData();
        Resource fileResource = new FileSystemResource(file.toFile());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=data.json")
                .body(fileResource);
    }

    @PostMapping("/import/json")
    public ResponseEntity<?> importData(@RequestParam("data") MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().equals("application/json")) {
            return ResponseEntity.badRequest().body(Map.of("error", true, "msg", "Incorrect file"));
        }

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            jsonService.importData(content);
            return ResponseEntity.ok(Map.of("acknowledged", true));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", true, "msg", "Error reading file"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", true, "msg", e.getMessage()));
        }
    }
}