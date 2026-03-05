package Inventory.Tests;

import Inventory.DataBase.DatabaseConnector;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A utility class for testing and inspecting database connections and structure.
 * 
 * <p>This diagnostic tool performs comprehensive testing of database connections
 * to both the Inventory and Suppliers databases, which are critical components
 * of the inventory management system. It validates that connections can be
 * established and provides detailed information about the database structure.</p>
 * 
 * <p>The class offers the following functionality:</p>
 * <ul>
 *   <li>Establishing and verifying connections to both Inventory.db and suppliers.db</li>
 *   <li>Displaying database metadata information (database type, version)</li>
 *   <li>Listing all tables present in each database</li>
 *   <li>Providing detailed schema information for each table, including column names,
 *       data types, and nullability constraints</li>
 * </ul>
 * 
 * <p>This tool is particularly useful during development, testing, and debugging
 * to verify that database schemas match expectations and that connections are
 * properly configured. It can help diagnose connectivity issues, schema
 * discrepancies, and validate that table structures are correct after migrations
 * or schema updates.</p>
 * 
 * <p>Usage: Run this class directly to perform database connection diagnostics
 * and print database structure to the console.</p>
 * 
 * @author ADSS Group AJ
 * @version 1.0
 * @since 2025-06-06
 */
public class DatabaseTestRunner {
    public static void main(String[] args) {
        testInventoryDB();
        System.out.println("\n" + "=".repeat(50) + "\n");
        testSuppliersDB();
    }

    private static void testInventoryDB() {
        System.out.println("Testing Inventory.db connection:");
        try (Connection conn = DatabaseConnector.connect()) {
            System.out.println("✅ Successfully connected to Inventory.db");
            printDatabaseInfo(conn);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to Inventory.db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSuppliersDB() {
        System.out.println("Testing suppliers.db connection:");
        try (Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:suppliers.db")) {
            System.out.println("✅ Successfully connected to suppliers.db");
            printDatabaseInfo(conn);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to suppliers.db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printDatabaseInfo(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        
        // Print database version info
        System.out.println("\nDatabase Information:");
        System.out.println("-".repeat(30));
        System.out.println("Database: " + metaData.getDatabaseProductName());
        System.out.println("Version: " + metaData.getDatabaseProductVersion());
        
        // Print tables
        System.out.println("\nDatabase Tables:");
        System.out.println("-".repeat(30));
        
        try (ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\n📋 Table: " + tableName);
                
                // Print column info for each table
                try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String columnType = columns.getString("TYPE_NAME");
                        String isNullable = columns.getString("IS_NULLABLE");
                        System.out.printf("   %-25s %-10s %s\n", 
                            columnName, 
                            columnType, 
                            isNullable.equals("YES") ? "nullable" : "not null"
                        );
                    }
                }
            }
        }
    }
}
