package com.lld.phase2.solid.ocp.problem3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lld.phase2.solid.stubs.SalesRecord;
import com.lld.phase2.solid.stubs.UnsupportedFormatException;
import com.lld.phase2.solid.stubs.XmlMapper;

import java.util.List;

// TODO: OCP VIOLATION — adding a new export format requires modifying export().
//
// Current: CSV, JSON, XML
// Requested: XLSX, PARQUET, PDF
//
// Risk in production: modifying an if/else that handles CSV
// could accidentally break CSV export for existing consumers.
// Each format should be isolated from the others.
//
// Note: SalesRecord is used instead of Record to avoid shadowing java.lang.Record
// (the abstract base class for Java record types introduced in Java 16+).
//
// Your task:
//   1. Define RecordExporter interface: String format() + byte[] export(List<SalesRecord>)
//   2. Each format = one @Component implementing RecordExporter
//   3. DataExporter collects all at startup, dispatches by format key
//   4. export() is permanently closed for modification

public class DataExporter {

    private final ObjectMapper objectMapper;
    private final XmlMapper    xmlMapper;

    public DataExporter(ObjectMapper objectMapper, XmlMapper xmlMapper) {
        this.objectMapper = objectMapper;
        this.xmlMapper    = xmlMapper;
    }

    public byte[] export(List<SalesRecord> records, String format) {
        try {
            if (format.equals("CSV")) {
                StringBuilder sb = new StringBuilder("id,name\n");
                for (SalesRecord r : records) {
                    sb.append(r.getId()).append(",").append(r.getName()).append("\n");
                }
                return sb.toString().getBytes();

            } else if (format.equals("JSON")) {
                return objectMapper.writeValueAsBytes(records);

            } else if (format.equals("XML")) {
                return xmlMapper.writeValueAsBytes(records);

            }
            // Adding XLSX?    Modify here. OCP violated.
            // Adding PARQUET? Modify here. OCP violated.

        } catch (Exception e) {
            throw new RuntimeException("Export failed for format: " + format, e);
        }

        throw new UnsupportedFormatException(format);
    }
}
