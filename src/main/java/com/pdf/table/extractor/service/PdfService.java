package com.pdf.table.extractor.service;

import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    public boolean isValidPdf(byte[] pdfBytes) {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            return true; // If load succeeds, it's a valid PDF
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasTables(byte[] pdfBytes) throws IOException {
        try (PDDocument pdf = PDDocument.load(pdfBytes);
                ObjectExtractor extractor = new ObjectExtractor(pdf)) {

            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            for (int i = 1; i <= pdf.getNumberOfPages(); i++) {
                Page page = extractor.extract(i);
                List<Table> tables = sea.extract(page);
                if (!tables.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public byte[] extractTablesToCsv(byte[] pdfBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        List<String[]> allRows = new ArrayList<>();

        try (PDDocument pdf = PDDocument.load(pdfBytes);
                ObjectExtractor extractor = new ObjectExtractor(pdf)) {

            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

            for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
                Page page = extractor.extract(pageNum);
                List<Table> tables = sea.extract(page);

                for (Table table : tables) {
                    // Add a separator for multiple tables/pages
                    if (!allRows.isEmpty()) {
                        allRows.add(new String[] { "--- Table from page " + pageNum + " ---" });
                    }

                    for (List<RectangularTextContainer> row : table.getRows()) {
                        String[] cells = row.stream()
                                .map(RectangularTextContainer::getText)
                                .toArray(String[]::new);
                        allRows.add(cells);
                    }
                }
            }
        }

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos))) {
            writer.writeAll(allRows);
        }

        return baos.toByteArray();
    }

}
