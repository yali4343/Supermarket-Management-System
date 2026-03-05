package Inventory.DAO;

import Inventory.DataBase.DatabaseConnector;
import Inventory.DTO.SoldItemDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSoldItemDAO implements ISoldItemDAO {
    static {
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {

            String createTableSql = """
                CREATE TABLE IF NOT EXISTS sold_items (
                    sold_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    catalog_number INTEGER NOT NULL,
                    branch_id INTEGER NOT NULL,
                    sale_date TEXT NOT NULL,
                    sale_price REAL NOT NULL
                );
            """;

            stmt.execute(createTableSql);
            System.out.println("✅ 'sold_items' table is ready.");
        } catch (SQLException e) {
            System.err.println("❌ Error creating 'sold_items' table:");
            e.printStackTrace();
        }
    }

    @Override
    public void insert(SoldItemDTO item) throws SQLException {
        String sql = "INSERT INTO sold_items (catalog_number, branch_id, sale_date, sale_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getCatalogNumber());
            ps.setInt(2, item.getBranchId());
            ps.setString(3, item.getSaleDate().toString());
            ps.setDouble(4, item.getSalePrice());

            ps.executeUpdate();
        }
    }

    @Override
    public List<SoldItemDTO> getSalesByCatalogAndBranch(int catalogNumber, int branchId) throws SQLException {
        List<SoldItemDTO> results = new ArrayList<>();
        String sql = "SELECT * FROM sold_items WHERE catalog_number = ? AND branch_id = ? ORDER BY sale_date ASC";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, catalogNumber);
            ps.setInt(2, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SoldItemDTO item = new SoldItemDTO();
                    item.setCatalogNumber(rs.getInt("catalog_number"));
                    item.setBranchId(rs.getInt("branch_id"));
                    item.setSaleDate(LocalDate.parse(rs.getString("sale_date")));
                    item.setSalePrice(rs.getDouble("sale_price"));
                    results.add(item);
                }
            }
        }
        return results;
    }
}