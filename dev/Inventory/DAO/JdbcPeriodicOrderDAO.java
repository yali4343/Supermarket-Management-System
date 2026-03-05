package Inventory.DAO;

import Inventory.DTO.PeriodicOrderDTO;
import Inventory.DataBase.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPeriodicOrderDAO implements IPeriodicOrderDAO {

    static {
        try (Connection conn = DatabaseConnector.connect();
             Statement statement = conn.createStatement()) {

            String createTableSql = """
                CREATE TABLE IF NOT EXISTS periodic_orders (
                    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_catalog_number INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    order_date TEXT DEFAULT (datetime('now')),
                    supplier_discount REAL DEFAULT 0.0,
                    supplier_id INTEGER NOT NULL,
                    supplier_name TEXT DEFAULT '',
                    days_in_the_week TEXT DEFAULT '',
                    agreement_id INTEGER NOT NULL,
                    branch_id INTEGER NOT NULL
                );
            """;

            statement.execute(createTableSql);
            System.out.println("✅ Periodic orders table created (if it did not already exist).");

        } catch (SQLException e) {
            System.err.println("❌ Error creating periodic orders table:");
            e.printStackTrace();
        }
    }

    @Override
    public void insertPeriodicOrder(PeriodicOrderDTO dto) throws SQLException {
        String sql = """
            INSERT INTO periodic_orders (
                product_catalog_number,
                quantity,
                order_date,
                supplier_discount,
                supplier_id,
                supplier_name,
                days_in_the_week,
                agreement_id,
                branch_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, dto.getProductCatalogNumber());
            preparedStatement.setInt(2, dto.getQuantity());
            preparedStatement.setString(3, dto.getOrderDate());
            preparedStatement.setDouble(4, dto.getSupplierDiscount());
            preparedStatement.setInt(5, dto.getSupplierId());
            preparedStatement.setString(6, dto.getSupplierName());
            preparedStatement.setString(7, dto.getDaysInTheWeek());
            preparedStatement.setInt(8, dto.getAgreementId());
            preparedStatement.setInt(9, dto.getBranchId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void updatePeriodicOrder(PeriodicOrderDTO dto) throws SQLException {
        String sql = """
            UPDATE periodic_orders
            SET product_catalog_number = ?, 
                quantity = ?, 
                supplier_id = ?, 
                supplier_name = ?, 
                agreement_id = ?, 
                supplier_discount = ?, 
                days_in_the_week = ?, 
                branch_id = ?
            WHERE order_id = ?
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, dto.getProductCatalogNumber());
            preparedStatement.setInt(2, dto.getQuantity());
            preparedStatement.setInt(3, dto.getSupplierId());
            preparedStatement.setString(4, dto.getSupplierName());
            preparedStatement.setInt(5, dto.getAgreementId());
            preparedStatement.setDouble(6, dto.getSupplierDiscount());
            preparedStatement.setString(7, dto.getDaysInTheWeek());
            preparedStatement.setInt(8, dto.getBranchId());
            preparedStatement.setInt(9, dto.getOrderId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("⚠️ No periodic order found with order_id = " + dto.getOrderId());
            } else {
                System.out.println("✅ Periodic order updated successfully.");
            }
        }
    }

    @Override
    public void deletePeriodicOrderById(int orderId) throws SQLException {
        String sql = "DELETE FROM periodic_orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public PeriodicOrderDTO getPeriodicOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM periodic_orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    PeriodicOrderDTO dto = new PeriodicOrderDTO();
                    dto.setOrderId(rs.getInt("order_id"));
                    dto.setProductCatalogNumber(rs.getInt("product_catalog_number"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setOrderDate(rs.getString("order_date"));
                    dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
                    dto.setSupplierId(rs.getInt("supplier_id"));
                    dto.setSupplierName(rs.getString("supplier_name"));
                    dto.setDaysInTheWeek(rs.getString("days_in_the_week"));
                    dto.setAgreementId(rs.getInt("agreement_id"));
                    dto.setBranchId(rs.getInt("branch_id"));
                    return dto;
                }
            }
        }

        return null;
    }

    @Override
    public List<PeriodicOrderDTO> getAllPeriodicOrders() throws SQLException {
        List<PeriodicOrderDTO> periodicOrders = new ArrayList<>();
        String sql = "SELECT * FROM periodic_orders";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PeriodicOrderDTO dto = new PeriodicOrderDTO();
                dto.setOrderId(rs.getInt("order_id"));
                dto.setProductCatalogNumber(rs.getInt("product_catalog_number"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setOrderDate(rs.getString("order_date"));
                dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
                dto.setSupplierId(rs.getInt("supplier_id"));
                dto.setSupplierName(rs.getString("supplier_name"));
                dto.setDaysInTheWeek(rs.getString("days_in_the_week"));
                dto.setAgreementId(rs.getInt("agreement_id"));
                dto.setBranchId(rs.getInt("branch_id"));

                periodicOrders.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving periodic orders from DB: " + e.getMessage());
            throw e;
        }

        return periodicOrders;
    }

    public List<PeriodicOrderDTO> getAllPeriodicOrdersByBranch(int branchId) throws SQLException {
        List<PeriodicOrderDTO> periodicOrders = new ArrayList<>();
        String sql = "SELECT * FROM periodic_orders WHERE branch_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PeriodicOrderDTO dto = new PeriodicOrderDTO();
                dto.setOrderId(rs.getInt("order_id"));
                dto.setProductCatalogNumber(rs.getInt("product_catalog_number"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setOrderDate(rs.getString("order_date"));
                dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
                dto.setSupplierId(rs.getInt("supplier_id"));
                dto.setSupplierName(rs.getString("supplier_name"));
                dto.setDaysInTheWeek(rs.getString("days_in_the_week"));
                dto.setAgreementId(rs.getInt("agreement_id"));
                dto.setBranchId(rs.getInt("branch_id"));

                periodicOrders.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving periodic orders by branch: " + e.getMessage());
            throw e;
        }

        return periodicOrders;
    }
}
