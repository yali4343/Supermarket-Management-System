package Suppliers.Init;

import Suppliers.DAO.*;
import Suppliers.DTO.AgreementDTO;
import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DTO.SupplierDTO;

import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * DataInitializer is responsible for populating the system with sample data.
 * This includes suppliers, agreements, products (with discount rules), and orders.
 *
 * Sample Data Strategy:
 * 1. Supplier Structure:
 *    - Multiple suppliers (Prigat, Tnuva, Osem, etc.) with different payment terms
 *    - Various delivery schedules (different days of the week)
 *    - Mix of cash and bank transfer payment methods
 *
 * 2. Product Pricing Strategy:
 *    - Same products available from multiple suppliers at different price points
 *    - Base price variations reflect supplier size and service quality
 *    - Discount structures encourage bulk ordering
 *    - Example: Product 1204 available from multiple suppliers with different prices
 *
 * 3. Discount Structure:
 *    - Volume-based discounts (higher discounts for larger quantities)
 *    - Different discount thresholds per supplier
 *    - Discount percentages range from 5% to 20%
 */
public class SuppliersInitializer {
    private final JdbcSupplierDAO supplierDAO;
    private final JdbcAgreementDAO agreementDAO;
    private final JdbcProductSupplierDAO productSupplierDAO;
    private final JdbcDiscountDAO discountDAO;
    private final JdbcOrderDAO orderDAO;

    public SuppliersInitializer() {
        this.supplierDAO = new JdbcSupplierDAO();
        this.agreementDAO = new JdbcAgreementDAO();
        this.productSupplierDAO = new JdbcProductSupplierDAO();
        this.discountDAO = new JdbcDiscountDAO();
        this.orderDAO = new JdbcOrderDAO();
    }

    public static void initializeAllTables() {
        new JdbcSupplierDAO().createTableIfNotExists();
        new JdbcAgreementDAO().createTableIfNotExists();
        new JdbcProductSupplierDAO().createProductSupplierTableIfNotExists();
        new JdbcDiscountDAO().createTableIfNotExists();
        new JdbcOrderDAO().createTableIfNotExists();
    }

    public LinkedHashMap<Integer, Integer> initializeDatabase(boolean withSampleData) throws SQLException {
        agreementDAO.createTableIfNotExists();

        if (withSampleData) {
            return insertSampleData();  // מחזיר את המפה
        }

        return new LinkedHashMap<>(); // במקרה שאין נתונים לדוגמה
    }

    private LinkedHashMap<Integer, Integer> insertSampleData() throws SQLException {        // Use helper method to get or create suppliers
        int supplierID1 = getOrCreateSupplier(new SupplierDTO("Prigat", 83, 9987, "Cash", "Prepaid", 5551234, "data@mail.com"));
        int supplierID2 = getOrCreateSupplier(new SupplierDTO("Tnuva", 84, 9007, "Bank Transfer", "Standing Order", 5671234, "OneMoreData@mail.com"));
        int supplierID3 = getOrCreateSupplier(new SupplierDTO("Osem", 85, 9107, "Bank Transfer", "Standing Order", 5678234, "BlaBlaData@mail.com"));
        int supplierID4 = getOrCreateSupplier(new SupplierDTO("Heinz", 86, 9207, "Cash", "Prepaid", 5678235, "BlaBlaData1@mail.com"));
        int supplierID5 = getOrCreateSupplier(new SupplierDTO("Sano", 87, 9307, "Bank Transfer", "Standing Order", 5678236, "BlaBlaData2@mail.com"));
        int supplierID6 = getOrCreateSupplier(new SupplierDTO("Elite", 81, 9407, "Bank Transfer", "Standing Order", 5678237, "BlaBlaData3@mail.com"));
        int supplierID7 = getOrCreateSupplier(new SupplierDTO("Neviot", 82, 9507, "Cash", "Prepaid", 5678238, "BlaBlaData4@mail.com"));
        int supplierID8 = getOrCreateSupplier(new SupplierDTO("Telma", 80, 9607, "Bank Transfer", "Standing Order", 5678239, "BlaBlaData5@mail.com"));        // Only create agreements if they don't already exist for the supplier
        int agreementID1 = getOrCreateAgreement(supplierID1, new String[]{"Mon", "Wed", "Fri"}, false);
        int agreementID2 = getOrCreateAgreement(supplierID2, new String[]{"Tue", "Thu"}, true);
        int agreementID3 = getOrCreateAgreement(supplierID3, new String[]{"Sun", "Wed"}, false);
        int agreementID4 = getOrCreateAgreement(supplierID4, new String[]{"Thu"}, false);
        int agreementID5 = getOrCreateAgreement(supplierID5, new String[]{"Mon"}, true);
        int agreementID6 = getOrCreateAgreement(supplierID6, new String[]{"Tue"}, false);
        int agreementID7 = getOrCreateAgreement(supplierID7, new String[]{"Wed"}, false);
        int agreementID8 = getOrCreateAgreement(supplierID8, new String[]{"Fri"}, true);

        LinkedHashMap<Integer, Integer> supplierIDAndAgreementsID = new LinkedHashMap<>();
        supplierIDAndAgreementsID.put(supplierID1, agreementID1);
        supplierIDAndAgreementsID.put(supplierID2, agreementID2);
        supplierIDAndAgreementsID.put(supplierID3, agreementID3);
        supplierIDAndAgreementsID.put(supplierID4, agreementID4);
        supplierIDAndAgreementsID.put(supplierID5, agreementID5);
        supplierIDAndAgreementsID.put(supplierID6, agreementID6);
        supplierIDAndAgreementsID.put(supplierID7, agreementID7);
        supplierIDAndAgreementsID.put(supplierID8, agreementID8);        // Insert product-supplier relationships if they don't exist
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1004,0, supplierID1, agreementID1, 6.5, "L"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1005,1, supplierID2, agreementID2, 8.0, "g"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1006,2, supplierID3, agreementID3, 4.5, "g"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1007, 3,supplierID4, agreementID4, 6, "L"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1008,4, supplierID5, agreementID5, 9.5, "g"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1009,5, supplierID6, agreementID6, 20, "g"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1010,6, supplierID7, agreementID7, 5, "g"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1011, 7,supplierID8, agreementID8, 3, "L"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO( 1012,8, supplierID8, agreementID8, 7.5, "g"));
        insertProductSupplierIfNotExists(new ProductSupplierDTO(1013,9, supplierID8, agreementID8, 12, "g"));

        // Insert discounts if they don't exist
        insertDiscountIfNotExists(new DiscountDTO(1004, supplierID1, agreementID1, 20, 10.0));
        insertDiscountIfNotExists(new DiscountDTO(1005, supplierID2, agreementID2, 10, 5.0));
        insertDiscountIfNotExists(new DiscountDTO(1006, supplierID3, agreementID3, 50, 15.0));
        insertDiscountIfNotExists(new DiscountDTO(1007, supplierID4, agreementID4, 20, 8.0));
        insertDiscountIfNotExists(new DiscountDTO(1008, supplierID5, agreementID5, 10, 12.0));
        insertDiscountIfNotExists(new DiscountDTO(1009, supplierID6, agreementID6, 50, 20.0));
        insertDiscountIfNotExists(new DiscountDTO(1010, supplierID7, agreementID7, 20, 7.5));
        insertDiscountIfNotExists(new DiscountDTO(1011, supplierID8, agreementID8, 10, 5.0));
        insertDiscountIfNotExists(new DiscountDTO(1012, supplierID8, agreementID8, 50, 10.0));
        insertDiscountIfNotExists(new DiscountDTO(1013, supplierID8, agreementID8, 20, 18.0));

        return supplierIDAndAgreementsID;
    }

    public void clearAllData() {
        // סדר מחיקה: הנחות → מוצרי ספק → הסכמים → ספקים
        discountDAO.clearTable();
        productSupplierDAO.clearTable();
        agreementDAO.clearTable();
        supplierDAO.clearTable();
        orderDAO.clearTable();



        System.out.println("✅ All supplier-related data deleted successfully.");
    }

    /**
     * Helper method to get existing supplier by name or create a new one if it doesn't exist
     */
    private int getOrCreateSupplier(SupplierDTO supplierDTO) throws SQLException {
        try {
            // First try to get the supplier by name if it exists
            int existingId = supplierDAO.getIdByName(supplierDTO.getSupplierName());
            if (existingId > 0) {
                System.out.println("✅ Found existing supplier: " + supplierDTO.getSupplierName() + " (ID: " + existingId + ")");
                return existingId;
            }
        } catch (SQLException e) {
            // Supplier doesn't exist, continue to create it
        }
          try {
            // Try to create new supplier
            int newId = supplierDAO.insertAndGetID(supplierDTO);
            return newId;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                // If unique constraint failed, try to get existing supplier again
                int existingId = supplierDAO.getIdByName(supplierDTO.getSupplierName());
                if (existingId > 0) {
                    System.out.println("✅ Retrieved existing supplier after constraint error: " + supplierDTO.getSupplierName() + " (ID: " + existingId + ")");
                    return existingId;
                }
            }
            throw e; // Re-throw if it's a different error
        }
    }

    /**
     * Helper method to get existing agreement or create a new one if it doesn't exist
     */
    private int getOrCreateAgreement(int supplierId, String[] deliveryDays, boolean selfPickup) throws SQLException {
        try {            // Try to create new agreement
            int newId = agreementDAO.insertAndGetID(new AgreementDTO(supplierId, deliveryDays, selfPickup));
            return newId;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                // If unique constraint failed, get existing agreement
                // For now, we'll get the first agreement for this supplier
                // In a real scenario, you might want more sophisticated matching
                var agreements = agreementDAO.getBySupplierId(supplierId);
                if (!agreements.isEmpty()) {
                    int existingId = agreements.get(0).getAgreement_ID();
                    System.out.println("✅ Using existing agreement for supplier " + supplierId + " (ID: " + existingId + ")");
                    return existingId;
                }
            }
            throw e; // Re-throw if it's a different error
        }
    }    /**
     * Helper method to insert product-supplier relationship if it doesn't already exist
     */    private void insertProductSupplierIfNotExists(ProductSupplierDTO productSupplierDTO) {
        try {
            productSupplierDAO.insert(productSupplierDTO);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                // Product-supplier relationship already exists, skip silently
            } else {
                System.err.println("❌ Error creating product-supplier relationship: " + e.getMessage());
            }
        }
    }/**
     * Helper method to insert discount if it doesn't already exist
     */
    private void insertDiscountIfNotExists(DiscountDTO discountDTO) {        try {
            discountDAO.insert(discountDTO);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                // Discount already exists, skip silently
            } else {
                System.err.println("❌ Error creating discount: " + e.getMessage());
            }
        }
    }

}
