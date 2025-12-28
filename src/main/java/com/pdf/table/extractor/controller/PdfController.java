package com.pdf.table.extractor.controller;

import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.pdf.table.extractor.service.PdfService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatePdf(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"valid\": false, \"message\": \"No file uploaded\"}");
        }

        boolean isValidPdf = pdfService.isValidPdf(file.getBytes());
        boolean hasTables = pdfService.hasTables(file.getBytes());

        if (!isValidPdf) {
            return ResponseEntity.badRequest().body("{\"valid\": false, \"message\": \"Not a valid PDF file\"}");
        }

        return ResponseEntity.ok()
                .body("{\"valid\": true, \"hasTables\": " + hasTables +
                        ", \"message\": \"" + (hasTables ? "PDF contains tables" : "No tables detected") + "\"}");
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractTables(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        if (!pdfService.isValidPdf(file.getBytes())) {
            return ResponseEntity.badRequest().body("Invalid PDF file");
        }

        byte[] csvBytes = pdfService.extractTablesToCsv(file.getBytes());

        if (csvBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No tables found in the PDF");
        }

        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"extracted_tables.csv\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(csvBytes.length)
                .body(resource);
    }

}
