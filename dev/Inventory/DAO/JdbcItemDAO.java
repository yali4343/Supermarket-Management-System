package Inventory.DAO;

import Inventory.DataBase.DatabaseConnector;
import Inventory.DTO.ItemDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcItemDAO implements IItemsDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";    
    
    static {
        try (Connection conn = DatabaseConnector.connect();
             Statement statement = conn.createStatement()) {

            // Check if table exists and has unique constraint that needs to be removed
            boolean needsRecreation = false;
            try {
                ResultSet rs = statement.executeQuery("PRAGMA table_info(Items)");
                if (!rs.next()) {
                    // Table doesn't exist, create it normally
                    needsRecreation = false;
                } else {
                    // Check if table has unique constraint we need to remove
                    ResultSet indexRs = statement.executeQuery("PRAGMA index_list(Items)");
                    boolean hasUniqueConstraint = false;
                    while (indexRs.next()) {
                        String indexName = indexRs.getString("name");
                        boolean isUnique = indexRs.getBoolean("unique");
                        
                        // Skip the primary key constraint (which we want to keep)
                        if (isUnique && !indexName.equals("sqlite_autoindex_Items_1")) {
                            // Found non-primary key unique constraint, need to recreate table
                            hasUniqueConstraint = true;
                            break;
                        }
                    }
                    indexRs.close();
                    
                    if (hasUniqueConstraint) {
                        needsRecreation = true;
                    }
                }
                rs.close();
            } catch (SQLException e) {
                needsRecreation = false;
            }

            if (needsRecreation) {
                // Backup existing data
                List<ItemDTO> existingItems = new ArrayList<>();
                try {
                    String backupSql = "SELECT * FROM Items";
                    try (ResultSet rs = statement.executeQuery(backupSql)) {
                        while (rs.next()) {
                            ItemDTO dto = new ItemDTO();
                            dto.setItemId(rs.getInt("item_id"));
                            dto.setCatalogNumber(rs.getInt("catalog_number"));
                            dto.setBranchId(rs.getInt("branch_id"));
                            dto.setLocation(rs.getString("storage_location"));
                            dto.setSectionInStore(rs.getString("section_in_store"));
                            dto.setIsDefective(rs.getBoolean("is_defect"));
                            dto.setExpirationDate(rs.getString("item_expiring_date"));
                            existingItems.add(dto);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Warning: Could not backup existing item data: " + e.getMessage());
                }
                
                // Drop and recreate table WITHOUT unique constraint
                statement.execute("DROP TABLE IF EXISTS Items");
                String createTableSql = """
                    CREATE TABLE Items (
                     item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                     catalog_number INTEGER NOT NULL,
                     branch_id INTEGER NOT NULL,
                     storage_location TEXT,
                     section_in_store TEXT,
                     is_defect BOOLEAN DEFAULT 0,
                     item_expiring_date TEXT,
                     FOREIGN KEY (catalog_number) REFERENCES Products(catalog_number)
                    );
                """;
                
                statement.execute(createTableSql);
                
                // Reset auto-increment to start from 1
                statement.execute("DELETE FROM sqlite_sequence WHERE name='Items'");
                
                // Restore all data, including duplicates
                if (!existingItems.isEmpty()) {
                    for (ItemDTO item : existingItems) {
                        try {
                            String insertSql = "INSERT INTO Items (catalog_number, branch_id, storage_location, section_in_store, is_defect, item_expiring_date) VALUES (?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement pstmt = statement.getConnection().prepareStatement(insertSql)) {
                                pstmt.setInt(1, item.getCatalogNumber());
                                pstmt.setInt(2, item.getBranchId());
                                pstmt.setString(3, item.getStorageLocation());
                                pstmt.setString(4, item.getSectionInStore());
                                pstmt.setBoolean(5, item.IsDefective());
                                pstmt.setString(6, item.getItemExpiringDate());
                                pstmt.executeUpdate();
                            }
                        } catch (SQLException e) {
                            System.err.println("Warning: Could not restore item for catalog " + item.getCatalogNumber() + ": " + e.getMessage());
                        }
                    }
                }
                System.out.println("✅ Items table recreated without unique constraint - now allows duplicate items");
            } else {
                // Create table normally if it doesn't exist
                String createTableSql = """
                    CREATE TABLE IF NOT EXISTS Items (
                     item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                     catalog_number INTEGER NOT NULL,
                     branch_id INTEGER NOT NULL,
                     storage_location TEXT,
                     section_in_store TEXT,
                     is_defect BOOLEAN DEFAULT 0,
                     item_expiring_date TEXT,
                     FOREIGN KEY (catalog_number) REFERENCES Products(catalog_number)
                    );
                """;

                statement.execute(createTableSql);
                System.out.println("✅ The 'Items' table was created or already exists.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error creating the 'Items' table:");
            e.printStackTrace();
        }
    }
    
    @Override
    public void Insert(ItemDTO dto) throws SQLException {
        // Now we just insert the item without checking for duplicates
        String sql = "INSERT INTO items (" +
                "catalog_number, branch_id, storage_location, section_in_store, " +
                "is_defect, item_expiring_date" +
                ") VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstatement.setInt(1, dto.getCatalogNumber());
            pstatement.setInt(2, dto.getBranchId());            pstatement.setString(3, dto.getStorageLocation());
            pstatement.setString(4, dto.getSectionInStore());
            pstatement.setBoolean(5, dto.IsDefective());
            pstatement.setString(6, dto.getItemExpiringDate());

            pstatement.executeUpdate();
            
            // Since getGeneratedKeys() is not supported by SQLite JDBC driver,
            // use a separate query to get the last inserted ID if we need it
            if (dto.getItemId() <= 0) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        dto.setItemId(rs.getInt(1));
                    }
                }
            }
        }
    }

    @Override
    public void Update(ItemDTO dto) throws SQLException {
        String sql = "UPDATE items SET catalog_number = ?, branch_id = ?, storage_location = ?, section_in_store = ?, is_defect = ?, item_expiring_date = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, dto.getCatalogNumber());
            pstatement.setInt(2, dto.getBranchId());
            pstatement.setString(3, dto.getStorageLocation());
            pstatement.setString(4, dto.getSectionInStore());
            pstatement.setBoolean(5, dto.IsDefective());
            pstatement.setString(6, dto.getItemExpiringDate());
            pstatement.setInt(7, dto.getItemId());

            int rowsAffected = pstatement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No item found with item_id = " + dto.getItemId());
            } else {
                System.out.println("The item was updated successfully.");
            }
        }
    }

    public void UpdateStorageLocation(ItemDTO dto) throws SQLException {
        String sql = "UPDATE items SET storage_location = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setString(1, dto.getStorageLocation());
            pstatement.setInt(2, dto.getItemId());

            int rowsAffected = pstatement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No item found with item_id = " + dto.getItemId());
            } else {
                System.out.println("The item was updated successfully.");
            }
        }
    }

    @Override
    public void DeleteByItemId(int id) throws SQLException {
        String sql = "DELETE FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, id);
            pstatement.executeUpdate();
        }
    }

    @Override
    public ItemDTO GetItemById(int id) throws SQLException {
        String sql = "SELECT * FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, id);
            try (ResultSet rs = pstatement.executeQuery()) {
                if (rs.next()) {
                    ItemDTO dto = new ItemDTO();
                    dto.setItemId(rs.getInt("item_id"));
                    dto.setCatalogNumber(rs.getInt("catalog_number"));
                    dto.setBranchId(rs.getInt("branch_id"));
                    dto.setLocation(rs.getString("storage_location"));
                    dto.setSectionInStore(rs.getString("section_in_store"));
                    dto.setIsDefective(rs.getBoolean("is_defect"));
                    dto.setExpirationDate(rs.getString("item_expiring_date"));
                    return dto;
                }
            }
        }

        return null;
    }

    @Override
    public void signAsDefective(int itemId) throws SQLException {
        String sql = "UPDATE items SET is_defect = 1 WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, itemId);
            int rowsAffected = pstatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No item found with item_id = " + itemId);
            } else {
                System.out.println("Item with item_id = " + itemId + " marked as defective.");
            }
        }
    }

    @Override
    public List<ItemDTO> getAllItems() throws SQLException {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM items";

        try (Connection conn = DatabaseConnector.connect();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                ItemDTO dto = new ItemDTO();
                dto.setItemId(rs.getInt("item_id"));
                dto.setCatalogNumber(rs.getInt("catalog_number"));
                dto.setBranchId(rs.getInt("branch_id"));
                dto.setLocation(rs.getString("storage_location"));
                dto.setIsDefective(rs.getBoolean("is_defect"));
                dto.setExpirationDate(rs.getString("item_expiring_date"));
                items.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving items from DB: " + e.getMessage());
            throw e;
        }

        return items;
    }

    @Override
    public List<ItemDTO> getItemsByProductId(int productId) {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM Items WHERE catalog_number = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, productId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ItemDTO item = new ItemDTO();
                    item.setItemId(rs.getInt("item_id"));
                    item.setCatalogNumber(rs.getInt("catalog_number"));
                    item.setBranchId(rs.getInt("branch_id"));
                    item.setLocation(rs.getString("storage_location"));
                    item.setSectionInStore(rs.getString("section_in_store"));
                    item.setIsDefective(rs.getBoolean("is_defect"));
                    item.setExpirationDate(rs.getString("item_expiring_date"));

                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving items by product ID: " + e.getMessage());
        }

        return items;
    }

    @Override
    public List<ItemDTO> findDefectiveItems() {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM Items WHERE is_defect = 1";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ItemDTO item = new ItemDTO();
                item.setItemId(rs.getInt("item_id"));
                item.setCatalogNumber(rs.getInt("catalog_number"));
                item.setBranchId(rs.getInt("branch_id"));
                item.setLocation(rs.getString("storage_location"));
                item.setSectionInStore(rs.getString("section_in_store"));
                item.setIsDefective(rs.getBoolean("is_defect"));
                item.setExpirationDate(rs.getString("item_expiring_date"));

                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving defective items: " + e.getMessage());
        }

        return items;
    }

    @Override
    public List<ItemDTO> getItemsByBranchId(int branchId) {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE branch_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemDTO item = mapResultSetToItemDTO(rs);
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving items for branch " + branchId + ": " + e.getMessage());
        }

        return items;
    }

    private ItemDTO mapResultSetToItemDTO(ResultSet rs) throws SQLException {
        ItemDTO item = new ItemDTO();
        item.setItemId(rs.getInt("item_id"));
        item.setCatalogNumber(rs.getInt("catalog_number"));
        item.setBranchId(rs.getInt("branch_id"));
        item.setExpirationDate(rs.getString("item_expiring_date"));
        item.setLocation(rs.getString("storage_location"));
        item.setSectionInStore(rs.getString("section_in_store"));
        item.setIsDefective(rs.getBoolean("is_defect"));
        return item;
    }

    @Override
    public void markItemAsDefective(int itemId, int branchId) throws SQLException {
        String sql = "UPDATE items SET is_defect = 1 WHERE item_id = ? AND branch_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.setInt(2, branchId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<ItemDTO> getItemsByBranch(int branchId) throws SQLException {
        List<ItemDTO> results = new ArrayList<>();

        String sql = "SELECT * FROM items WHERE branch_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ItemDTO item = new ItemDTO(
                        rs.getInt("item_id"),
                        rs.getInt("catalog_number"),
                        rs.getInt("branch_id"),
                        rs.getString("storage_location"),
                        rs.getString("section_in_store"),
                        rs.getBoolean("is_defect"),
                        rs.getString("item_expiring_date")
                );
                results.add(item);
            }
        }

        return results;
    }

    @Override
    public List<ItemDTO> getExpiredItemsByBranchId(int branchId, LocalDate today) throws SQLException {
        List<ItemDTO> results = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE branch_id = ? AND date(item_expiring_date) < date(?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            stmt.setString(2, today.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ItemDTO item = new ItemDTO(
                        rs.getInt("catalog_number"),
                        rs.getInt("branch_id"),
                        rs.getString("storage_location"),
                        rs.getString("section_in_store"),
                        rs.getBoolean("is_defect"),
                        rs.getString("item_expiring_date")
                );
                item.setItemId(rs.getInt("item_id"));
                results.add(item);
            }
        }

        return results;
    }
}