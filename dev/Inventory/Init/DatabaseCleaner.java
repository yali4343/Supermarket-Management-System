
package Inventory.Init;

import Inventory.DataBase.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCleaner {

    public static void dropAllTables() {
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS items");
            stmt.execute("DROP TABLE IF EXISTS products");
            stmt.execute("DROP TABLE IF EXISTS periodic_orders");

            System.out.println("✅ All tables dropped successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Error dropping tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
