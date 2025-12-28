package com.pdf.table.extractor.util;

import com.opencsv.CSVWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

public class CsvUtil {

    public static byte[] writeRowsToCsv(List<String[]> rows) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos))) {
            writer.writeAll(rows);
        }

        return baos.toByteArray();
    }

    public static byte[] writeEmptyCsv() throws IOException {
        return writeRowsToCsv(Arrays.asList(new String[][] { { "No tables found in the PDF" } }));
    }
}