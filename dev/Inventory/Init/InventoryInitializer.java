package Inventory.Init;

import Inventory.DAO.*;
import Inventory.DTO.ItemDTO;
import Inventory.DTO.PeriodicOrderDTO;
import Inventory.DTO.ProductDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class InventoryInitializer {


    public static void initializeAllTables() {
        System.out.println("Creating all necessary tables using DAO static initializers...");
        new JdbcProductDAO();
        new JdbcItemDAO();
        new JdbcShortageOrderDAO();
        new JdbcPeriodicOrderDAO();
        System.out.println("✅ All DAO-related tables initialized successfully.");
    }

    public static void preloadProducts() {
        JdbcProductDAO productDAO = new JdbcProductDAO();
        List<ProductDTO> products = Arrays.asList(
                new ProductDTO(1004, "Orange Juice 1L", "Beverages", "Juices", "Prigat", 1, 6.5, 10.0, 5.0, "MONDAY, WEDNESDAY, FRIDAY", 2),
                new ProductDTO(1005, "Butter 200g", "Dairy", "Butter", "Tnuva", 1, 8.0, 5.0, 3.0, "TUESDAY, THURSDAY", 2),
                new ProductDTO(1006, "White Rice 1kg", "Grocery", "Rice", "Osem", 1, 4.5, 15.0, 7.0, "Sunday, WEDNESDAY", 3),
                new ProductDTO(1007, "Tomato Sauce 500ml", "Grocery", "Sauces", "Heinz", 1, 6.0, 8.0, 4.0, "THURSDAY", 3),
                new ProductDTO(1008, "Yellow Cheese 200g", "Dairy", "Cheese", "Tnuva", 1, 9.5, 12.0, 5.0, "TUESDAY, THURSDAY", 4),
                new ProductDTO(1009, "Toilet Paper 12-pack", "Household", "Toiletries", "Sano", 2, 20.0, 20.0, 10.0, "MONDAY", 2),
                new ProductDTO(1010, "Chocolate Bar 100g", "Snacks", "Chocolate", "Elite", 1, 5.0, 7.5, 2.5, "TUESDAY", 1),
                new ProductDTO(1011, "Mineral Water 1.5L", "Beverages", "Water", "Neviot", 1, 3.0, 5.0, 1.0, "WEDNESDAY", 2),
                new ProductDTO(1012, "Dish Soap 750ml", "Cleaning", "Detergents", "Sano", 1, 7.5, 10.0, 4.0, "THURSDAY", 3),
                new ProductDTO(1013, "Cornflakes 750g", "Cereal", "Breakfast", "Telma", 1, 12.0, 18.0, 6.0, "FRIDAY", 5)
        );

        for (ProductDTO dto : products) {
            try {
                if (productDAO.GetProductByCatalogNumber(dto.getCatalogNumber()) == null) {
                    productDAO.Insert(dto);
                }

            } catch (SQLException e) {
                System.err.println("❌ Failed to preload product: " + dto.getProductName());
                e.printStackTrace();
            }        }
    }

    public static void preloadItems() {
        JdbcItemDAO itemDAO = new JdbcItemDAO();
        List<ItemDTO> items = Arrays.asList(
                new ItemDTO(1004, 1, "Warehouse", "A1", false, "2025-06-30"),
                new ItemDTO(1005, 1, "Store", "B1", false, "2025-07-15"),
                new ItemDTO(1006, 1, "Store", "C1", false, "2025-09-10"),
                new ItemDTO(1007, 2, "Warehouse", "A2", false, "2025-05-01"),
                new ItemDTO(1008, 2, "Store", "B2", false, "2025-06-20"),
                new ItemDTO(1009, 2, "Store", "C2", false, "2025-07-25"),
                new ItemDTO(1010, 3, "Warehouse", "A3", false, "2025-08-30"),
                new ItemDTO(1011, 3, "Store", "B3", false, "2025-10-10"),
                new ItemDTO(1012, 3, "Store", "C3", false, "2025-11-15"),
                new ItemDTO(1013, 4, "Warehouse", "A4", false, "2025-12-01"),
                new ItemDTO(1004, 4, "Store", "B4", false, "2026-01-20"),
                new ItemDTO(1005, 4, "Store", "C4", false, "2026-02-28"),
                new ItemDTO(1006, 5, "Warehouse", "A5", false, "2025-09-05"),
                new ItemDTO(1007, 5, "Store", "B5", false, "2025-10-01"),
                new ItemDTO(1008, 5, "Store", "C5", false, "2025-11-11"),
                new ItemDTO(1009, 6, "Warehouse", "A6", false, "2025-12-12"),
                new ItemDTO(1010, 6, "Store", "B6", false, "2026-01-01"),
                new ItemDTO(1011, 6, "Store", "C6", false, "2026-02-14"),
                new ItemDTO(1012, 7, "Warehouse", "A7", false, "2026-03-03"),
                new ItemDTO(1013, 7, "Store", "B7", false, "2026-04-04"),
                new ItemDTO(1004, 7, "Store", "C7", false, "2026-05-05"),
                new ItemDTO(1005, 8, "Warehouse", "A8", false, "2026-06-06"),
                new ItemDTO(1006, 8, "Store", "B8", false, "2026-07-07"),
                new ItemDTO(1007, 8, "Store", "C8", false, "2026-08-08"),
                new ItemDTO(1008, 9, "Warehouse", "A9", false, "2026-09-09"),
                new ItemDTO(1009, 9, "Store", "B9", false, "2026-10-10"),
                new ItemDTO(1010, 9, "Store", "C9", false, "2026-11-11"),
                new ItemDTO(1011, 10, "Warehouse", "A10", false, "2026-12-12"),
                new ItemDTO(1012, 10, "Store", "B10", false, "2027-01-01"),
                new ItemDTO(1013, 10, "Store", "C10", false, "2027-02-02")
        );

        for (ItemDTO item : items) {
            try {
                itemDAO.Insert(item);
            } catch (Exception e) {
                System.err.println("❌ Failed to preload item: " + e.getMessage());
            }
        }
    }

    public static void preloadPeriodicOrders(LinkedHashMap<Integer,Integer> supplierIdAndAgreementsID) {
        JdbcPeriodicOrderDAO dao = new JdbcPeriodicOrderDAO();

        // שלב 1: חילוץ מזהים לפי הסדר
        Iterator<Map.Entry<Integer, Integer>> iterator = supplierIdAndAgreementsID.entrySet().iterator();

        int supplierID1 = 0, agreementID1 = 0;
        int supplierID2 = 0, agreementID2 = 0;
        int supplierID3 = 0, agreementID3 = 0;

        if (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            supplierID1 = entry.getKey(); agreementID1 = entry.getValue();
        }
        if (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            supplierID2 = entry.getKey(); agreementID2 = entry.getValue();
        }
        if (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            supplierID3 = entry.getKey(); agreementID3 = entry.getValue();
        }        // שלב 2: יצירת רשימת הזמנות תקופתיות עם שמות ספקים תואמים
        List<PeriodicOrderDTO> orders = Arrays.asList(
                new PeriodicOrderDTO(0, 1004, 2, "2025-06-02", 0.65, supplierID1, getSupplierNameById(supplierID1), 
                    "MONDAY, WEDNESDAY, FRIDAY", agreementID1, 1, "MONDAY, WEDNESDAY, FRIDAY", null, null, 0),
                new PeriodicOrderDTO(0, 1005, 3, "2025-06-02", 0.80, supplierID2, getSupplierNameById(supplierID2), 
                    "TUESDAY, THURSDAY", agreementID2, 2, "TUESDAY, THURSDAY", null, null, 0),
                new PeriodicOrderDTO(0, 1006, 4, "2025-06-02", 0.45, supplierID3, getSupplierNameById(supplierID3), 
                    "SUNDAY, WEDNESDAY", agreementID3, 3, "SUNDAY, WEDNESDAY", null, null, 0),
                new PeriodicOrderDTO(0, 1013, 5, "2025-06-02", 0.50, supplierID1, getSupplierNameById(supplierID1), 
                    "FRIDAY", agreementID1, 1, "FRIDAY", null, null, 0)
        );

        for (PeriodicOrderDTO dto : orders) {
            try {
                dao.insertPeriodicOrder(dto);
            } catch (SQLException e) {
                System.err.println("❌ Failed to insert periodic order for product " + dto.getProductCatalogNumber());
                e.printStackTrace();
            }
        }    }

    private static String getSupplierNameById(int supplierId) {
        return switch (supplierId) {
            case 1 -> "Prigat";
            case 2 -> "Tnuva";
            case 3 -> "Osem";
            default -> "Unknown";
        };
    }


    public static void clearAllTables() {
        String url = "jdbc:sqlite:Inventory.db";        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {        // Order of deletion is important due to foreign key constraints
        stmt.executeUpdate("DELETE FROM items");
        stmt.executeUpdate("DELETE FROM sold_items");
            stmt.executeUpdate("DELETE FROM periodic_orders");
            stmt.executeUpdate("DELETE FROM shortage_orders");
            stmt.executeUpdate("DELETE FROM products");

            System.out.println("🧹 All existing table data has been cleared.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear table data: " + e.getMessage());
        }
    }    public static void preloadAllInitialData(LinkedHashMap<Integer,Integer> supplierIdAndAgreementsID) {
        preloadProducts();
        preloadItems();
        preloadPeriodicOrders(supplierIdAndAgreementsID); //add SuppliersID and AgreementID
    }


}
