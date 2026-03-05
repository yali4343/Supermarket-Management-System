package Inventory.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String DB_URL = "jdbc:sqlite:Inventory.db";

    /**
     * Establishes and returns a connection to the SQLite database.
     *
     * @return Connection object to Inventory.db
     * @throws SQLException if connection fails
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

   public static void main(String[] args) {
       try (Connection conn = connect()) {
           if (conn != null) {
               System.out.println("Connected to SQLite database successfully.");
           }
       } catch (SQLException e) {
           System.err.println("Failed to connect to the database.");
          e.printStackTrace();
        }
   }
}
