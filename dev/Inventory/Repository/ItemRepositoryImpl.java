package Inventory.Repository;

import java.time.LocalDate;
import java.util.List;

import Inventory.DTO.ItemDTO;
import Inventory.DAO.IItemsDAO;
import Inventory.DAO.JdbcItemDAO;
import Inventory.DataBase.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of the ItemRepository interface using a JDBC DAO.
 * Provides methods to interact with the items in the inventory.
 */
public class ItemRepositoryImpl implements IItemRepository {
    private final IItemsDAO itemDAO;


    public ItemRepositoryImpl() {
        this.itemDAO = new JdbcItemDAO();
    }


    @Override
    public void addItem(ItemDTO item) {
        try {
            itemDAO.Insert(item);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateItem(ItemDTO item) {
        try {
            itemDAO.Update(item);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteItem(int itemId) {
        try {
            itemDAO.DeleteByItemId(itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ItemDTO getItemById(int itemId) {
        try {
            return itemDAO.GetItemById(itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ItemDTO> getAllItems() {
        try {
            return itemDAO.getAllItems();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ItemDTO> getItemsByProductId(int productId) {
        return itemDAO.getItemsByProductId(productId);
    }


    @Override
    public void markItemAsDefect(int itemId) {
        try {
            itemDAO.signAsDefective(itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ItemDTO> getDefectiveItems() {
        return itemDAO.findDefectiveItems();
    }

    @Override
    public List<ItemDTO> getItemsByBranchId(int branchId) {
        return itemDAO.getItemsByBranchId(branchId);
    }

    @Override
    public void markItemAsDefective(int itemId, int branchId) throws SQLException {
        itemDAO.markItemAsDefective(itemId, branchId);
    }

    @Override
    public List<ItemDTO> getItemsByBranch(int branchId) throws SQLException {
        return itemDAO.getItemsByBranch(branchId);
    }

    @Override
    public List<ItemDTO> getExpiredItemsByBranchId(int branchId, LocalDate today) throws SQLException {
        return itemDAO.getExpiredItemsByBranchId(branchId, today);
    }

    @Override    public int countItemsByCatalogNumber(int catalogNumber, int branchId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM items WHERE catalog_number = ? AND branch_id = ? AND is_defect = 0";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, catalogNumber);
            stmt.setInt(2, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    @Override
    public int getNextAvailableItemId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(item_id) + 1, 1) as next_id FROM items";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        }
        return 1; // If table is empty, start with ID 1
    }
}
