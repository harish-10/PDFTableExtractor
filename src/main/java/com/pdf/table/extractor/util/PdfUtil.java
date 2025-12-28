package com.pdf.table.extractor.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfUtil {

    private static final SpreadsheetExtractionAlgorithm EXTRACTION_ALGORITHM = new SpreadsheetExtractionAlgorithm();

    public static boolean isValidPdf(byte[] pdfBytes) {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasTables(byte[] pdfBytes) throws IOException {
        try (PDDocument pdf = PDDocument.load(pdfBytes);
                ObjectExtractor extractor = new ObjectExtractor(pdf)) {

            for (int pageNumber = 1; pageNumber <= pdf.getNumberOfPages(); pageNumber++) {
                Page page = extractor.extract(pageNumber);
                List<Table> tables = EXTRACTION_ALGORITHM.extract(page);
                if (!tables.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String[]> extractAllTableRows(byte[] pdfBytes) throws IOException {
        List<String[]> allRows = new ArrayList<>();

        try (PDDocument pdf = PDDocument.load(pdfBytes);
                ObjectExtractor extractor = new ObjectExtractor(pdf)) {

            for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
                Page page = extractor.extract(pageNum);
                List<Table> tables = EXTRACTION_ALGORITHM.extract(page);

                for (Table table : tables) {
                    if (!allRows.isEmpty()) {
                        allRows.add(new String[] { "--- Table from page " + pageNum + " ---" });
                    }

                    for (List<RectangularTextContainer> row : table.getRows()) {
                        String[] cells = row.stream()
                                .map(RectangularTextContainer::getText)
                                .map(text -> text == null ? "" : text.trim())
                                .toArray(String[]::new);
                        allRows.add(cells);
                    }
                }
            }
        }

        return allRows;
    }
}