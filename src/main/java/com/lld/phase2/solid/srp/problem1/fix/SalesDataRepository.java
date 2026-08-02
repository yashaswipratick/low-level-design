package com.lld.phase2.solid.srp.problem1.fix;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: SRP VIOLATION — this class has 3 distinct reasons to change:
//   1. DB query / schema changes        → fixed

@Repository
public class SalesDataRepository {

    private final DataSource dataSource;

    public SalesDataRepository(DataSource dataSource) {
        this.dataSource  = dataSource;
    }

    public List<Map<String, Object>> fetchSalesData(LocalDate from, LocalDate to) {
        String sql = "SELECT * FROM orders WHERE created_at BETWEEN ? AND ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch sales data", e);
        }
        return result;
    }
}
