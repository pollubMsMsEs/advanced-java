package com.pollubmsmses.advjava.controllers.json;

import com.pollubmsmses.advjava.services.files.ExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pollubmsmses.advjava.services.files.JsonService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JsonController {

    private final JsonService jsonService;
    private final ExportService exportService;
    @GetMapping("/export/json")
    public ResponseEntity<Resource> exportData(){
        String file = jsonService.exportData(exportService.collectData(null,null,null,null));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(inputStream.available())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("data.json")
                                .build()
                                .toString())
                .body(new ByteArrayResource(inputStream.readAllBytes()));

    }

    @PostMapping("/import/json")
    public ResponseEntity<?> importData(@RequestParam("data") MultipartFile file) {
        if (file.isEmpty() || !Objects.equals(file.getContentType(), "application/json")) {
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