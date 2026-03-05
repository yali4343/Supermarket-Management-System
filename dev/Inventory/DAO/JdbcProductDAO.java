package Inventory.DAO;

import Inventory.DTO.ProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDAO implements IProductDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement statement = conn.createStatement()) {

            // Check if table exists and has unique constraint on product_name
            boolean needsRecreation = false;
            try {
                ResultSet rs = statement.executeQuery("PRAGMA table_info(products)");
                if (!rs.next()) {
                    needsRecreation = false; // Table doesn't exist, create normally
                } else {
                    // Table exists, check if unique constraint on product_name exists
                    ResultSet indexRs = statement.executeQuery("PRAGMA index_list(products)");
                    boolean hasUniqueConstraint = false;
                    while (indexRs.next()) {
                        String indexName = indexRs.getString("name");
                        boolean isUnique = indexRs.getBoolean("unique");
                        if (isUnique) {
                            // Check if this unique index is on product_name
                            ResultSet indexInfoRs = statement.executeQuery("PRAGMA index_info(" + indexName + ")");
                            while (indexInfoRs.next()) {
                                String columnName = indexInfoRs.getString("name");
                                if ("product_name".equals(columnName)) {
                                    hasUniqueConstraint = true;
                                    break;
                                }
                            }
                            indexInfoRs.close();
                        }
                        if (hasUniqueConstraint) break;
                    }
                    indexRs.close();
                    
                    if (!hasUniqueConstraint) {
                        needsRecreation = true;
                    }
                }
                rs.close();
            } catch (SQLException e) {
                needsRecreation = false;
            }            if (needsRecreation) {
                // Backup existing data
                List<ProductDTO> existingProducts = new ArrayList<>();
                try {
                    // Use a direct query instead of getAllProducts() to avoid static context issues
                    String backupSql = "SELECT * FROM products";
                    try (ResultSet rs = statement.executeQuery(backupSql)) {
                        while (rs.next()) {
                            ProductDTO dto = new ProductDTO();
                            dto.setCatalogNumber(rs.getInt("catalog_number"));
                            dto.setProductName(rs.getString("product_name"));
                            dto.setCategory(rs.getString("category"));
                            dto.setSubCategory(rs.getString("sub_category"));
                            dto.setSupplierName(rs.getString("supplier_name"));
                            dto.setSize(rs.getInt("product_size"));
                            dto.setProductDemandLevel(rs.getInt("product_demand_level"));
                            dto.setSupplyDaysInWeek(rs.getString("supply_days_in_week"));
                            dto.setSupplyTime(rs.getInt("supply_time"));
                            dto.setQuantityInStore(rs.getInt("quantity_in_store"));
                            dto.setQuantityInWarehouse(rs.getInt("quantity_in_warehouse"));
                            dto.setMinimumQuantityForAlert(rs.getInt("minimum_quantity_for_alert"));
                            dto.setCostPriceBeforeSupplierDiscount(rs.getDouble("cost_price_before_supplier_discount"));
                            dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
                            dto.setStoreDiscount(rs.getDouble("store_discount"));
                            dto.setCostPriceAfterSupplierDiscount(rs.getDouble("cost_price_after_supplier_discount"));
                            dto.setSalePriceBeforeStoreDiscount(rs.getDouble("sale_price_before_store_discount"));
                            dto.setSalePriceAfterStoreDiscount(rs.getDouble("sale_price_after_store_discount"));
                            existingProducts.add(dto);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Warning: Could not backup existing product data: " + e.getMessage());
                }
                
                // Drop and recreate table
                statement.execute("DROP TABLE IF EXISTS products");
                
                String createTableSql = "CREATE TABLE products (\n"
                        + " catalog_number INTEGER PRIMARY KEY,\n"
                        + " product_name TEXT NOT NULL UNIQUE,\n"
                        + " category TEXT,\n"
                        + " sub_category TEXT,\n"
                        + " supplier_name TEXT,\n"
                        + " product_size INTEGER,\n"
                        + " product_demand_level INTEGER,\n"
                        + " supply_days_in_week TEXT,\n"
                        + " supply_time INTEGER,\n"
                        + " quantity_in_store INTEGER DEFAULT 0,\n"
                        + " quantity_in_warehouse INTEGER DEFAULT 0,\n"
                        + " minimum_quantity_for_alert INTEGER,\n"
                        + " cost_price_before_supplier_discount REAL,\n"
                        + " supplier_discount REAL DEFAULT 0.0,\n"
                        + " store_discount REAL DEFAULT 0.0,\n"
                        + " cost_price_after_supplier_discount REAL DEFAULT 0.0,\n"
                        + " sale_price_before_store_discount REAL DEFAULT 0.0,\n"
                        + " sale_price_after_store_discount REAL DEFAULT 0.0\n"
                        + ");";
                
                statement.execute(createTableSql);
                  // Restore data (removing duplicates by product_name)
                if (!existingProducts.isEmpty()) {
                    java.util.Set<String> addedNames = new java.util.HashSet<>();
                    for (ProductDTO product : existingProducts) {
                        if (!addedNames.contains(product.getProductName())) {
                            try {
                                String insertSql = "INSERT INTO products (" +
                                        "catalog_number, product_name, category, sub_category, supplier_name, product_size, " +
                                        "product_demand_level, supply_days_in_week, supply_time, quantity_in_store, quantity_in_warehouse, " +
                                        "minimum_quantity_for_alert, cost_price_before_supplier_discount, " +
                                        "supplier_discount, store_discount, " +
                                        "cost_price_after_supplier_discount, sale_price_before_store_discount, sale_price_after_store_discount) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                
                                try (PreparedStatement pstmt = statement.getConnection().prepareStatement(insertSql)) {
                                    pstmt.setInt(1, product.getCatalogNumber());
                                    pstmt.setString(2, product.getProductName());
                                    pstmt.setString(3, product.getCategory());
                                    pstmt.setString(4, product.getSubCategory());
                                    pstmt.setString(5, product.getSupplierName());
                                    pstmt.setInt(6, product.getSize());
                                    pstmt.setInt(7, product.getProductDemandLevel());
                                    pstmt.setString(8, product.getSupplyDaysInWeek());
                                    pstmt.setInt(9, product.getSupplyTime());
                                    pstmt.setInt(10, product.getQuantityInStore());
                                    pstmt.setInt(11, product.getQuantityInWarehouse());
                                    pstmt.setInt(12, product.getMinimumQuantityForAlert());
                                    pstmt.setDouble(13, product.getCostPriceBeforeSupplierDiscount());
                                    pstmt.setDouble(14, product.getSupplierDiscount());
                                    pstmt.setDouble(15, product.getStoreDiscount());
                                    pstmt.setDouble(16, product.getCostPriceAfterSupplierDiscount());
                                    pstmt.setDouble(17, product.getSalePriceBeforeStoreDiscount());
                                    pstmt.setDouble(18, product.getSalePriceAfterStoreDiscount());
                                    pstmt.executeUpdate();
                                }
                                addedNames.add(product.getProductName());
                            } catch (SQLException e) {
                                System.err.println("Warning: Could not restore product " + product.getProductName() + ": " + e.getMessage());
                            }
                        } else {
                            System.out.println("Skipped duplicate product name: " + product.getProductName());
                        }
                    }
                }
                System.out.println("✅ Products table recreated with unique product_name constraint.");
            } else {
                String createTableSql = "CREATE TABLE IF NOT EXISTS products (\n"
                        + " catalog_number INTEGER PRIMARY KEY,\n"
                        + " product_name TEXT NOT NULL UNIQUE,\n"
                        + " category TEXT,\n"
                        + " sub_category TEXT,\n"
                        + " supplier_name TEXT,\n"
                        + " product_size INTEGER,\n"
                        + " product_demand_level INTEGER,\n"
                        + " supply_days_in_week TEXT,\n"
                        + " supply_time INTEGER,\n"
                        + " quantity_in_store INTEGER DEFAULT 0,\n"
                        + " quantity_in_warehouse INTEGER DEFAULT 0,\n"
                        + " minimum_quantity_for_alert INTEGER,\n"
                        + " cost_price_before_supplier_discount REAL,\n"
                        + " supplier_discount REAL DEFAULT 0.0,\n"
                        + " store_discount REAL DEFAULT 0.0,\n"
                        + " cost_price_after_supplier_discount REAL DEFAULT 0.0,\n"
                        + " sale_price_before_store_discount REAL DEFAULT 0.0,\n"
                        + " sale_price_after_store_discount REAL DEFAULT 0.0\n"
                        + ");";

                statement.execute(createTableSql);
                System.out.println("✅ The 'products' table was created or already exists.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error creating the 'products' table:");
            e.printStackTrace();
        }
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    @Override
    public void Insert(ProductDTO dto) throws SQLException {
        String sql = "INSERT INTO products (" +
                "catalog_number, product_name, category, sub_category, supplier_name, product_size, " +
                "product_demand_level, supply_days_in_week, supply_time, quantity_in_store, quantity_in_warehouse, " +
                "minimum_quantity_for_alert, cost_price_before_supplier_discount, " +
                "supplier_discount, store_discount, " +
                "cost_price_after_supplier_discount, sale_price_before_store_discount, sale_price_after_store_discount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getCatalogNumber());
            pstmt.setString(2, dto.getProductName());
            pstmt.setString(3, dto.getCategory());
            pstmt.setString(4, dto.getSubCategory());
            pstmt.setString(5, dto.getSupplierName());
            pstmt.setInt(6, dto.getSize());
            pstmt.setInt(7, dto.getProductDemandLevel());
            pstmt.setString(8, dto.getSupplyDaysInWeek());
            pstmt.setInt(9, dto.getSupplyTime());
            pstmt.setInt(10, dto.getQuantityInStore());
            pstmt.setInt(11, dto.getQuantityInWarehouse());
            pstmt.setInt(12, dto.getMinimumQuantityForAlert());
            pstmt.setDouble(13, round(dto.getCostPriceBeforeSupplierDiscount()));
            pstmt.setDouble(14, round(dto.getSupplierDiscount()));
            pstmt.setDouble(15, round(dto.getStoreDiscount()));
            pstmt.setDouble(16, round(dto.getCostPriceAfterSupplierDiscount()));
            pstmt.setDouble(17, round(dto.getSalePriceBeforeStoreDiscount()));
            pstmt.setDouble(18, round(dto.getSalePriceAfterStoreDiscount()));

            pstmt.executeUpdate();
        }
    }

    @Override
    public void Update(ProductDTO dto) throws SQLException {
        String sql = "UPDATE products SET " +
                "product_name = ?, category = ?, sub_category = ?, supplier_name = ?, product_size = ?, " +
                "cost_price_before_supplier_discount = ?, supplier_discount = ?, store_discount = ?, " +
                "supply_days_in_week = ?, supply_time = ?, product_demand_level = ?, " +
                "quantity_in_store = ?, quantity_in_warehouse = ?, minimum_quantity_for_alert = ?, " +
                "cost_price_after_supplier_discount = ?, sale_price_before_store_discount = ?, sale_price_after_store_discount = ? " +
                "WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getProductName());
            ps.setString(2, dto.getCategory());
            ps.setString(3, dto.getSubCategory());
            ps.setString(4, dto.getSupplierName());
            ps.setInt(5, dto.getSize());
            ps.setDouble(6, round(dto.getCostPriceBeforeSupplierDiscount()));
            ps.setDouble(7, round(dto.getSupplierDiscount()));
            ps.setDouble(8, round(dto.getStoreDiscount()));
            ps.setString(9, dto.getSupplyDaysInWeek());
            ps.setInt(10, dto.getSupplyTime());
            ps.setInt(11, dto.getProductDemandLevel());
            ps.setInt(12, dto.getQuantityInStore());
            ps.setInt(13, dto.getQuantityInWarehouse());
            ps.setInt(14, dto.getMinimumQuantityForAlert());
            ps.setDouble(15, round(dto.getCostPriceAfterSupplierDiscount()));
            ps.setDouble(16, round(dto.getSalePriceBeforeStoreDiscount()));
            ps.setDouble(17, round(dto.getSalePriceAfterStoreDiscount()));
            ps.setInt(18, dto.getCatalogNumber());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("The product with catalog number = " + dto.getCatalogNumber() + " not found");
            } else {
                System.out.println("The product was updated successfully");
            }
        }
    }

    @Override
    public ProductDTO GetProductByCatalogNumber(int catalogNumber) throws SQLException {
        String sql = "SELECT * FROM products WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, catalogNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProductDTO(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving products from DB: " + e.getMessage());
        }

        return products;
    }

    private ProductDTO mapResultSetToProductDTO(ResultSet rs) throws SQLException {
        ProductDTO dto = new ProductDTO();
        dto.setCatalogNumber(rs.getInt("catalog_number"));
        dto.setProductName(rs.getString("product_name"));
        dto.setCategory(rs.getString("category"));
        dto.setSubCategory(rs.getString("sub_category"));
        dto.setSupplierName(rs.getString("supplier_name"));
        dto.setSize(rs.getInt("product_size"));
        dto.setProductDemandLevel(rs.getInt("product_demand_level"));
        dto.setSupplyDaysInWeek(rs.getString("supply_days_in_week"));
        dto.setQuantityInStore(rs.getInt("quantity_in_store"));
        dto.setQuantityInWarehouse(rs.getInt("quantity_in_warehouse"));
        dto.setMinimumQuantityForAlert(rs.getInt("minimum_quantity_for_alert"));
        dto.setCostPriceBeforeSupplierDiscount(rs.getDouble("cost_price_before_supplier_discount"));
        dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
        dto.setStoreDiscount(rs.getDouble("store_discount"));
        dto.setCostPriceAfterSupplierDiscount(rs.getDouble("cost_price_after_supplier_discount"));
        dto.setSalePriceBeforeStoreDiscount(rs.getDouble("sale_price_before_store_discount"));
        dto.setSalePriceAfterStoreDiscount(rs.getDouble("sale_price_after_store_discount"));
        return dto;
    }

    public void DeleteByCatalogNumber(int catalogNumber) throws SQLException {
        String sql = "DELETE FROM products WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, catalogNumber);
            ps.executeUpdate();
        }
    }

    @Override
    public void UpdateCostPrice(int catalogNumber, double newCostPrice) throws SQLException {
        String sql = "UPDATE products SET cost_price_before_supplier_discount = ? WHERE catalog_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newCostPrice);
            stmt.setInt(2, catalogNumber);
            stmt.executeUpdate();
        }
    }

    @Override
    public void UpdateCalculatedPrices(ProductDTO product) throws SQLException {
        String sql = """
        UPDATE products SET 
            cost_price_after_supplier_discount = ?, 
            sale_price_before_store_discount = ?, 
            sale_price_after_store_discount = ?
        WHERE catalog_number = ?
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, product.getCostPriceAfterSupplierDiscount());
            stmt.setDouble(2, product.getSalePriceBeforeStoreDiscount());
            stmt.setDouble(3, product.getSalePriceAfterStoreDiscount());
            stmt.setInt(4, product.getCatalogNumber());
            stmt.executeUpdate();
        }
    }



    public void UpdateProductSupplyTime(ProductDTO dto) throws SQLException {
        String sql = "UPDATE products SET supply_time = ? WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getSupplyTime());
            ps.setInt(2, dto.getCatalogNumber());
            ps.executeUpdate();
        }
    }

    public void UpdateDemand(ProductDTO dto) throws SQLException {
        String sql = "UPDATE products SET product_demand_level = ? WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getProductDemandLevel());
            ps.setInt(2, dto.getCatalogNumber());
            ps.executeUpdate();
        }
    }


    @Override
    public List<ProductDTO> getProductsBySizes(List<Integer> sizes) throws SQLException {
        List<ProductDTO> products = new ArrayList<>();

        if (sizes == null || sizes.isEmpty()) return products;

        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE product_size IN (");
        for (int i = 0; i < sizes.size(); i++) {
            sql.append("?");
            if (i < sizes.size() - 1) sql.append(",");
        }
        sql.append(")");

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < sizes.size(); i++) {
                stmt.setInt(i + 1, sizes.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductDTO product = new ProductDTO(
                        rs.getInt("catalog_number"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("sub_category"),
                        rs.getString("supplier_name"),
                        rs.getInt("product_size"),
                        rs.getDouble("cost_price_before_supplier_discount"),
                        rs.getDouble("sale_price_before_store_discount"),
                        rs.getDouble("sale_price_after_store_discount"),
                        rs.getString("supply_days_in_week"),
                        rs.getInt("minimum_quantity_for_alert")
                );
                products.add(product);
            }
        }

        return products;
    }

}
