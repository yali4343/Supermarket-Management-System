package Suppliers.DAO;

import Suppliers.DTO.OrderDTO;
import Suppliers.DTO.OrderItemDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderDAO implements IOrderDAO {
    private static final String DB_URL = "jdbc:sqlite:suppliers.db";    public void createTableIfNotExists() {
        String sqlOrders = "CREATE TABLE IF NOT EXISTS orders (\n" +
                " order_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " phone_number INTEGER NOT NULL,\n" +
                " order_date TEXT NOT NULL\n" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create orders table first
            stmt.execute(sqlOrders);
            
            // Check if order_items table needs to be created/recreated with proper foreign key constraints
            boolean needsRecreation = false;
            try {
                ResultSet rs = stmt.executeQuery("PRAGMA table_info(order_items)");
                if (!rs.next()) {
                    // Table doesn't exist, create it with foreign key constraints
                    needsRecreation = false;
                } else {
                    // Table exists, check if foreign key constraints exist
                    ResultSet fkRs = stmt.executeQuery("PRAGMA foreign_key_list(order_items)");
                    boolean hasOrderIdFK = false;
                    boolean hasSupplierIdFK = false;
                    
                    while (fkRs.next()) {
                        String table = fkRs.getString("table");
                        String from = fkRs.getString("from");
                        String to = fkRs.getString("to");
                        
                        if ("orders".equals(table) && "order_id".equals(from) && "order_id".equals(to)) {
                            hasOrderIdFK = true;
                        }
                        if ("suppliers".equals(table) && "supplier_id".equals(from) && "supplier_id".equals(to)) {
                            hasSupplierIdFK = true;
                        }
                    }
                    fkRs.close();
                    
                    if (!hasOrderIdFK || !hasSupplierIdFK) {
                        needsRecreation = true;
                    }
                }
                rs.close();
            } catch (SQLException e) {
                // If we can't check, assume we need to create the table
                needsRecreation = false;
            }
            
            if (needsRecreation) {
                System.out.println("Recreating order_items table with proper foreign key constraints...");
                
                // Backup existing data
                List<String[]> backupData = new ArrayList<>();
                try {
                    ResultSet backupRs = stmt.executeQuery("SELECT order_id, product_id, quantity, supplier_id FROM order_items");
                    while (backupRs.next()) {
                        backupData.add(new String[]{
                            String.valueOf(backupRs.getInt("order_id")),
                            String.valueOf(backupRs.getInt("product_id")),
                            String.valueOf(backupRs.getInt("quantity")),
                            String.valueOf(backupRs.getInt("supplier_id"))
                        });
                    }
                    backupRs.close();
                } catch (SQLException e) {
                    System.err.println("Warning: Could not backup order_items data: " + e.getMessage());
                }
                
                // Drop and recreate table
                stmt.execute("DROP TABLE IF EXISTS order_items");
                
                String sqlItemsWithFK = "CREATE TABLE order_items (\n" +
                        " order_id INTEGER NOT NULL,\n" +
                        " product_id INTEGER NOT NULL,\n" +
                        " quantity INTEGER NOT NULL,\n" +
                        " supplier_id INTEGER NOT NULL,\n" +
                        " FOREIGN KEY(order_id) REFERENCES orders(order_id),\n" +
                        " FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)\n" +
                        ");";
                
                stmt.execute(sqlItemsWithFK);
                
                // Restore data
                if (!backupData.isEmpty()) {
                    try (PreparedStatement restoreStmt = conn.prepareStatement(
                            "INSERT INTO order_items (order_id, product_id, quantity, supplier_id) VALUES (?, ?, ?, ?)")) {
                        for (String[] row : backupData) {
                            restoreStmt.setInt(1, Integer.parseInt(row[0]));
                            restoreStmt.setInt(2, Integer.parseInt(row[1]));
                            restoreStmt.setInt(3, Integer.parseInt(row[2]));
                            restoreStmt.setInt(4, Integer.parseInt(row[3]));
                            restoreStmt.addBatch();
                        }
                        restoreStmt.executeBatch();
                        System.out.println("Restored " + backupData.size() + " order items.");
                    }
                }
            } else {
                // Create table with foreign key constraints if it doesn't exist
                String sqlItemsWithFK = "CREATE TABLE IF NOT EXISTS order_items (\n" +
                        " order_id INTEGER NOT NULL,\n" +
                        " product_id INTEGER NOT NULL,\n" +
                        " quantity INTEGER NOT NULL,\n" +
                        " supplier_id INTEGER NOT NULL,\n" +
                        " FOREIGN KEY(order_id) REFERENCES orders(order_id),\n" +
                        " FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)\n" +
                        ");";
                        
                stmt.execute(sqlItemsWithFK);
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating orders or order_items table:");
            e.printStackTrace();
        }
    }    @Override
    public void insert(OrderDTO dto) throws SQLException {
        String insertOrderSql = "INSERT INTO orders (phone_number, order_date) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, supplier_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql);
             PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {

            conn.setAutoCommit(false);

            try {
                // הוספת ההזמנה
                orderStmt.setLong(1, dto.getPhoneNumber());
                orderStmt.setString(2, dto.getOrderDate().toString());
                orderStmt.executeUpdate();

                // קבלת ה-ID האחרון שנוצר
                int orderId;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                        dto.setOrderID(orderId);
                    } else {
                        throw new SQLException("Failed to retrieve order ID.");
                    }
                }

                // הוספת הפריטים
                for (OrderItemDTO item : dto.getItems()) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setInt(4, item.getSupplierId());
                    itemStmt.addBatch();
                }

                itemStmt.executeBatch();
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                    if (e.getMessage().contains("supplier_id")) {
                        throw new SQLException("Cannot create order: supplier_id " + 
                            dto.getItems().stream().findFirst().map(item -> item.getSupplierId()).orElse(-1) + 
                            " does not exist in suppliers table", e);
                    }
                    throw new SQLException("Foreign key constraint violation: " + e.getMessage(), e);
                }
                throw e;
            }
        }
    }


    @Override
    public void deleteById(int orderId) throws SQLException {
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement itemStmt = conn.prepareStatement(deleteItemsSql);
             PreparedStatement orderStmt = conn.prepareStatement(deleteOrderSql)) {

            itemStmt.setInt(1, orderId);
            itemStmt.executeUpdate();

            orderStmt.setInt(1, orderId);
            orderStmt.executeUpdate();
        }
    }

    @Override
    public OrderDTO getById(int orderId) throws SQLException {
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String itemsSql = "SELECT product_id, quantity, supplier_id FROM order_items WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             PreparedStatement itemStmt = conn.prepareStatement(itemsSql)) {

            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();

            if (!orderRs.next()) return null;

            long phone = orderRs.getLong("phone_number");
            LocalDateTime date = LocalDateTime.parse(orderRs.getString("order_date"));

            List<OrderItemDTO> items = new ArrayList<>();
            itemStmt.setInt(1, orderId);
            ResultSet itemRs = itemStmt.executeQuery();
            while (itemRs.next()) {
                int productId = itemRs.getInt("product_id");
                int quantity = itemRs.getInt("quantity");
                int supplierId = itemRs.getInt("supplier_id");
                items.add(new OrderItemDTO(productId, quantity, supplierId));
            }

            OrderDTO dto = new OrderDTO(phone, date, items);
            dto.setOrderID(orderId);
            return dto;
        }
    }

    @Override
    public List<OrderDTO> getAll() throws SQLException {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT order_id FROM orders";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(getById(rs.getInt("order_id")));
            }
        }

        return orders;
    }

    @Override
    public List<OrderDTO> getBySupplierId(int supplierId) throws SQLException {
        List<OrderDTO> allOrders = getAll();
        List<OrderDTO> filteredOrders = new ArrayList<>();

        for (OrderDTO order : allOrders) {
            boolean hasSupplier = order.getItems().stream().anyMatch(i -> i.getSupplierId() == supplierId);
            if (hasSupplier) filteredOrders.add(order);
        }

        return filteredOrders;
    }

    @Override
    public List<OrderDTO> searchOrders(LocalDateTime startDate, LocalDateTime endDate, Integer supplierId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT o.order_id FROM orders o");
        List<Object> params = new ArrayList<>();

        if (supplierId != null) {
            sql.append(" JOIN order_items oi ON o.order_id = oi.order_id WHERE oi.supplier_id = ?");
            params.add(supplierId);
        }

        if (startDate != null || endDate != null) {
            sql.append(supplierId == null ? " WHERE" : " AND");
            if (startDate != null) {
                sql.append(" order_date >= ?");
                params.add(startDate.toString());
            }
            if (endDate != null) {
                if (startDate != null) sql.append(" AND");
                sql.append(" order_date <= ?");
                params.add(endDate.toString());
            }
        }

        List<OrderDTO> orders = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OrderDTO order = getById(rs.getInt("order_id"));
                if (order != null) {
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public void clearTable() {
        String sql = "DELETE FROM orders";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Cleared all records from 'orders' table.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear 'orders' table: " + e.getMessage());
        }
    }


}
