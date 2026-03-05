package Inventory.DAO;

import Inventory.DTO.ShortageOrderDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcShortageOrderDAO implements IShortageOrderDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement statement = conn.createStatement()) {
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS shortage_orders (
                    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_catalog_number INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    cost_price_before_supplier_discount REAL NOT NULL,
                    supplier_discount REAL NOT NULL,
                    order_date TEXT NOT NULL,
                    branch_id INTEGER NOT NULL,
                    days_in_the_week TEXT NOT NULL,
                    supplier_id INTEGER NOT NULL,
                    supplier_name TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    completion_date TEXT
                )
                """;
            statement.execute(createTableSql);
        } catch (SQLException e) {
            System.err.println("Failed to create shortage_orders table: " + e.getMessage());
        }
    }

    @Override
    public void insertShortageOrder(ShortageOrderDTO order) throws SQLException {
        String sql = """
            INSERT INTO shortage_orders (
                product_catalog_number, quantity, cost_price_before_supplier_discount,
                supplier_discount, order_date, branch_id, days_in_the_week,
                supplier_id, supplier_name, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getProductCatalogNumber());
            pstmt.setInt(2, order.getQuantity());
            pstmt.setDouble(3, order.getCostPriceBeforeSupplierDiscount());
            pstmt.setDouble(4, order.getSupplierDiscount());
            pstmt.setString(5, order.getOrderDate());
            pstmt.setInt(6, order.getBranchId());
            pstmt.setString(7, order.getDaysInTheWeek());
            pstmt.setInt(8, order.getSupplierId());
            pstmt.setString(9, order.getSupplierName());
            pstmt.setString(10, "PENDING");
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateShortageOrder(ShortageOrderDTO order) throws SQLException {
        String sql = """
            UPDATE shortage_orders SET
                quantity = ?, 
                cost_price_before_supplier_discount = ?,
                supplier_discount = ?,
                order_date = ?,
                branch_id = ?,
                days_in_the_week = ?,
                supplier_id = ?,
                supplier_name = ?,
                status = ?,
                completion_date = CASE WHEN ? = 'DELIVERED' THEN datetime('now') ELSE null END
            WHERE order_id = ?
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getQuantity());
            pstmt.setDouble(2, order.getCostPriceBeforeSupplierDiscount());
            pstmt.setDouble(3, order.getSupplierDiscount());
            pstmt.setString(4, order.getOrderDate());
            pstmt.setInt(5, order.getBranchId());
            pstmt.setString(6, order.getDaysInTheWeek());
            pstmt.setInt(7, order.getSupplierId());
            pstmt.setString(8, order.getSupplierName());
            pstmt.setString(9, order.getStatus());
            pstmt.setString(10, order.getStatus());
            pstmt.setInt(11, order.getOrderId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteShortageOrderById(int id) throws SQLException {
        String sql = "DELETE FROM shortage_orders WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public ShortageOrderDTO getShortageOrderById(int id) throws SQLException {
        String sql = "SELECT * FROM shortage_orders WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ShortageOrderDTO(
                        rs.getInt("order_id"),
                        rs.getInt("product_catalog_number"),
                        rs.getInt("quantity"),
                        rs.getDouble("cost_price_before_supplier_discount"),
                        rs.getDouble("supplier_discount"),
                        rs.getString("order_date"),
                        rs.getInt("branch_id"),
                        rs.getString("days_in_the_week"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getInt("quantity"), // using order quantity as needed quantity
                        0, // default current stock 
                        rs.getString("status") // get actual status from DB
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<ShortageOrderDTO> getAllShortageOrders() throws SQLException {
        List<ShortageOrderDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM shortage_orders";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ShortageOrderDTO dto = new ShortageOrderDTO(
                    rs.getInt("order_id"),
                    rs.getInt("product_catalog_number"),
                    rs.getInt("quantity"),
                    rs.getDouble("cost_price_before_supplier_discount"),
                    rs.getDouble("supplier_discount"),
                    rs.getString("order_date"),
                    rs.getInt("branch_id"),
                    rs.getString("days_in_the_week"),
                    rs.getInt("supplier_id"),
                    rs.getString("supplier_name"),
                    rs.getInt("quantity"), // using order quantity as needed quantity
                    0, // default current stock 
                    rs.getString("status") // get actual status from DB
                );
                orders.add(dto);
            }
        }
        return orders;
    }

    @Override
    public String getLastOrderDateForProduct(int catalogNumber, int branchId) throws SQLException {
        String sql = """
            SELECT order_date FROM shortage_orders 
            WHERE product_catalog_number = ? AND branch_id = ?
            ORDER BY order_date DESC LIMIT 1""";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, catalogNumber);
            pstmt.setInt(2, branchId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("order_date");
                }
            }
        }
        return null;    }    @Override
    public boolean hasBeenProcessedToday(int branchId) throws SQLException {
        String sql = "SELECT 1 FROM shortage_orders WHERE branch_id = ? AND DATE(completion_date) = DATE('now')";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }    @Override
    public void markProcessedForToday(int branchId) throws SQLException {
        String sql = "UPDATE shortage_orders SET status = 'DELIVERED', completion_date = datetime('now') WHERE branch_id = ? AND status = 'PENDING'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean hasPendingOrderForProduct(int catalogNumber, int branchId) throws SQLException {
        String sql = """
            SELECT 1 FROM shortage_orders 
            WHERE product_catalog_number = ? AND branch_id = ? AND status = 'PENDING'
            LIMIT 1""";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, catalogNumber);
            pstmt.setInt(2, branchId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
