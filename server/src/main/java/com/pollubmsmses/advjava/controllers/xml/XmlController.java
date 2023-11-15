package com.pollubmsmses.advjava.controllers.xml;

import com.pollubmsmses.advjava.services.files.ExportService;
import com.pollubmsmses.advjava.services.files.JsonService;
import com.pollubmsmses.advjava.services.files.XmlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class XmlController {
    private final XmlService xmlService;
    private final ExportService exportService;

    @Secured("ADMIN")
    @GetMapping("/export/xml")
    public ResponseEntity<Resource> exportData(@RequestParam() LocalDate begin_date, @RequestParam() LocalDate end_date, @RequestParam() List<Long> countries){
        String file = xmlService.exportData(exportService.collectData(begin_date,end_date,countries));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(inputStream.available())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("data.xml")
                                .build()
                                .toString())
                .body(new ByteArrayResource(inputStream.readAllBytes()));

    }

    @Secured("ADMIN")
    @PostMapping("/import/xml")
    public ResponseEntity<?> importData(@RequestParam("data") MultipartFile file) {
        if (file.isEmpty() || (!Objects.equals(file.getContentType(), "application/xml") && !Objects.equals(file.getContentType(), "text/xml"))) {
            return ResponseEntity.badRequest().body(Map.of("error", true, "msg", "Incorrect file"));
        }

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            xmlService.importData(content);
            return ResponseEntity.ok(Map.of("acknowledged", true));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", true, "msg", "Error reading file"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", true, "msg", e.getMessage()));
        }
    }

}
