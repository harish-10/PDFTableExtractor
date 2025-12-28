package com.pdf.table.extractor.service;

import com.pdf.table.extractor.util.CsvUtil;
import com.pdf.table.extractor.util.PdfUtil;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    public boolean isValidPdf(byte[] pdfBytes) {
        return PdfUtil.isValidPdf(pdfBytes);
    }

    public boolean hasTables(byte[] pdfBytes) throws IOException {
        return PdfUtil.hasTables(pdfBytes);
    }

    public byte[] extractTablesToCsv(byte[] pdfBytes) throws IOException {
        List<String[]> tableRows = PdfUtil.extractAllTableRows(pdfBytes);

        if (tableRows.isEmpty()) {
            return CsvUtil.writeEmptyCsv();
        }

        return CsvUtil.writeRowsToCsv(tableRows);
    }

}
