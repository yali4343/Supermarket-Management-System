package Suppliers.DAO;

import Suppliers.DTO.ProductSupplierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductSupplierDAO implements IProductSupplierDAO {
    private static final String DB_URL = "jdbc:sqlite:suppliers.db";

    public void createProductSupplierTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS product_supplier (\n"
                    + " product_id INTEGER NOT NULL,\n"
                    + " catalog_number INTEGER NOT NULL,\n"
                    + " supplier_id INTEGER NOT NULL,\n"
                    + " agreement_id INTEGER NOT NULL,\n"
                    + " unit TEXT NOT NULL,\n"
                    + " price REAL NOT NULL,\n"
                    + " PRIMARY KEY (product_id, catalog_number, supplier_id)\n"
                    + ");";

            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error while creating product_supplier table:");
            e.printStackTrace();
        }
    }


    @Override
    public void insert(ProductSupplierDTO dto) throws SQLException {
        String sql = "INSERT INTO product_supplier (product_id, catalog_number, supplier_id, agreement_id, unit, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getProduct_id());
            pstmt.setInt(2, dto.getCatalog_Number());
            pstmt.setInt(3, dto.getSupplierID());
            pstmt.setInt(4, dto.getAgreement_id());
            pstmt.setString(5, dto.getUnitsOfMeasure());
            pstmt.setDouble(6, dto.getPrice());
            pstmt.executeUpdate();
        }
    }


    @Override
    public void update(ProductSupplierDTO dto) throws SQLException {
        String sql = "UPDATE product_supplier SET unit = ?, price = ? WHERE product_id = ? AND catalog_number = ? AND supplier_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getUnitsOfMeasure());
            pstmt.setDouble(2, dto.getPrice());
            pstmt.setInt(3, dto.getProduct_id());
            pstmt.setInt(4, dto.getCatalog_Number());
            pstmt.setInt(5, dto.getSupplierID());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteOneProduct(int productId, int catalogNumber, int supplierId) throws SQLException {
        String sql = "DELETE FROM product_supplier WHERE product_id = ? AND catalog_number = ? AND supplier_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, catalogNumber);
            pstmt.setInt(3, supplierId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteAllProductsFromSupplier(int supplierId) throws SQLException {
        String sql = "DELETE FROM product_supplier WHERE supplier_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteAllProductsFromAgreement(int agreementId) throws SQLException {
        String sql = "DELETE FROM product_supplier WHERE agreement_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, agreementId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateProductUnit(int catalogNumber, String newUnit, int agreementID) {
        String sql = "UPDATE product_supplier SET unit = ? WHERE catalog_number = ? AND agreement_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUnit);
            pstmt.setInt(2, catalogNumber);
            pstmt.setInt(3, agreementID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void SetPrice(int catalogNumber, double  newPrice, int agreementID) {
        String sql = "UPDATE product_supplier SET price = ? WHERE catalog_number = ? AND agreement_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble (1, newPrice);
            pstmt.setInt(2, catalogNumber);
            pstmt.setInt(3, agreementID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ProductSupplierDTO getOneProduct(int productId, int catalogNumber, int supplierId) throws SQLException {
        String sql = "SELECT * FROM product_supplier WHERE product_id = ? AND catalog_number = ? AND supplier_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, catalogNumber);
            pstmt.setInt(3, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductSupplierDTO(
                            rs.getInt("product_id"),
                            rs.getInt("catalog_number"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getDouble("price"),
                            rs.getString("unit")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public ProductSupplierDTO getOneProductByProductIDAndSupplierID(int productId, int supplierId) throws SQLException {
        String sql = "SELECT * FROM product_supplier WHERE product_id = ? AND supplier_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductSupplierDTO(
                            rs.getInt("product_id"),
                            rs.getInt("catalog_number"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getDouble("price"),
                            rs.getString("unit")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<ProductSupplierDTO> getProductsByProductID(int productID) throws SQLException {
        List<ProductSupplierDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM product_supplier WHERE product_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductSupplierDTO dto = new ProductSupplierDTO(
                            rs.getInt("product_id"),
                            rs.getInt("catalog_number"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getDouble("price"),       // שים לב: צריך setDouble ולא setInt
                            rs.getString("unit")
                    );
                    list.add(dto);
                }
            }
        }
        return list;
    }

    @Override
    public ProductSupplierDTO getOneProductByProductIDAgreementIDSupplierID(int productId, int supplierId, int agreementId) throws SQLException {
        return null;
    }


    @Override
    public List<ProductSupplierDTO> getProductsByAgreement(int supplier_ID, int agreement_ID) throws SQLException {
        List<ProductSupplierDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM product_supplier WHERE agreement_id = ? AND supplier_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, agreement_ID);
            pstmt.setInt(2, supplier_ID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductSupplierDTO dto = new ProductSupplierDTO(
                            rs.getInt("product_id"),
                            rs.getInt("catalog_number"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getDouble("price"),       // שים לב: צריך setDouble ולא setInt
                            rs.getString("unit")
                    );
                    list.add(dto);
                }
            }
        }
        return list;
    }

    @Override
    public ProductSupplierDTO getCheapestProductSupplier(int productId) {
        String sql = "SELECT * FROM product_supplier " +
                "WHERE product_id = ? " +
                "ORDER BY price ASC " +
                "LIMIT 1;";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int catalogNumber = rs.getInt("catalog_number");
                int supplierID = rs.getInt("supplier_id");
                int agreementID = rs.getInt("agreement_id");
                double price = rs.getDouble("price");
                String unit = rs.getString("unit"); // שם העמודה לפי הסכימה

                return new ProductSupplierDTO(
                        catalogNumber,
                        productId,
                        supplierID,
                        agreementID,
                        price,
                        unit
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching cheapest product supplier:");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ProductSupplierDTO getOneProductByProductID(int productId, int supplierId) throws SQLException {
        String sql = "SELECT * FROM product_supplier WHERE product_id = ? AND supplier_id = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.setInt(2, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductSupplierDTO(
                            rs.getInt("catalog_number"),
                            rs.getInt("product_id"),
                            rs.getInt("supplier_id"),
                            rs.getInt("agreement_id"),
                            rs.getDouble("price"),
                            rs.getString("unit")
                    );
                }
            }
        }

        return null; // אם לא נמצא מוצר מתאים
    }

    public void clearTable() {
        String sql = "DELETE FROM product_supplier";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Cleared all records from 'product_supplier' table.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear 'product_supplier' table: " + e.getMessage());
        }
    }


}
