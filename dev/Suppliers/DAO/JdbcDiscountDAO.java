package Suppliers.DAO;

import Suppliers.DTO.DiscountDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcDiscountDAO implements IDiscountDAO {

    private static final String DB_URL = "jdbc:sqlite:suppliers.db";


    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS discounts (\n"
                + " product_id INTEGER NOT NULL,\n"
                + " supplier_id INTEGER NOT NULL,\n"
                + " agreement_id INTEGER NOT NULL,\n"
                + " amount INTEGER NOT NULL,\n"
                + " discount_percentage REAL NOT NULL,\n"
                + " PRIMARY KEY (product_id, supplier_id, agreement_id, amount)\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating discounts table:");
            e.printStackTrace();
        }
    }



    @Override
    public void insert(DiscountDTO discount) throws SQLException {
        String sql = "INSERT OR REPLACE INTO discounts (product_id, supplier_id, agreement_id, amount, discount_percentage) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, discount.getProductID());
            pstmt.setInt(2, discount.getSupplierID());
            pstmt.setInt(3, discount.getAgreementID());
            pstmt.setInt(4, discount.getAmount());
            pstmt.setDouble(5, discount.getDiscountPercentage());
            pstmt.executeUpdate();
        }
    }


    @Override
    public void update(DiscountDTO discount) throws SQLException {
        String sql = "UPDATE discounts SET amount = ?, discount_percentage = ? WHERE product_id = ? AND supplier_id = ? AND agreement_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, discount.getAmount());
            pstmt.setDouble(2, discount.getDiscountPercentage());
            pstmt.setInt(3, discount.getProductID());
            pstmt.setInt(4, discount.getSupplierID());
            pstmt.setInt(5, discount.getAgreementID());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteByAgreement(int agreementId) throws SQLException {
        String sql = "DELETE FROM discounts WHERE agreement_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, agreementId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteBySupplier(int supplierID) throws SQLException {
        String sql = "DELETE FROM discounts WHERE supplier_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierID);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteDiscountForProduct(int productId, int supplierId, int agreementId) throws SQLException {
        String sql = "DELETE FROM discounts WHERE product_id = ? AND supplier_id = ? AND agreement_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, supplierId);
            pstmt.setInt(3, agreementId);
            pstmt.executeUpdate();
        }
    }


    @Override
    public DiscountDTO getBestMatchingDiscount(int productId, int supplierId, int agreementId, int quantity) throws SQLException {
        String sql = """
        SELECT * FROM discounts 
        WHERE product_id = ? 
          AND supplier_id = ? 
          AND agreement_id = ? 
          AND amount <= ?
        ORDER BY amount DESC
        LIMIT 1
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.setInt(2, supplierId);
            pstmt.setInt(3, agreementId);
            pstmt.setInt(4, quantity);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new DiscountDTO(
                            rs.getInt("product_id"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getInt("amount"),
                            rs.getInt("discount_percentage")
                    );
                }
            }
        }

        return null; // אין הנחה מתאימה
    }


    @Override
    public DiscountDTO getBestDiscount(int productId, int quantity) throws SQLException {
        String sql = "SELECT * FROM discounts " +
                "WHERE product_id = ? AND amount <= ? " +
                "ORDER BY discount_percentage DESC " +
                "LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, quantity);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new DiscountDTO(
                            rs.getInt("product_id"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getInt("amount"),
                            rs.getInt("discount_percentage")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<DiscountDTO> getDiscountsForProductByID(int productId, int quantity) throws SQLException {
        List<DiscountDTO> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts WHERE product_id = ? AND amount <= ? ORDER BY amount ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.setInt(2, quantity);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DiscountDTO dto = new DiscountDTO(
                            rs.getInt("product_id"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getInt("amount"),
                            rs.getDouble("discount_percentage")
                    );
                    discounts.add(dto);
                }
            }
        }

        return discounts;
    }


    public void clearTable() {
        String sql = "DELETE FROM discounts";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Cleared all records from 'discounts' table.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear 'discounts' table: " + e.getMessage());
        }
    }




}
