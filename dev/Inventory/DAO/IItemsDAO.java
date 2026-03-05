package Inventory.DAO;

import Inventory.DTO.ItemDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface for managing items in the inventory database.
 */
public interface IItemsDAO {

    /**
     * Inserts a new item into the database.
     * @param dto the item to insert
     * @throws SQLException if a database error occurs
     */
    void Insert(ItemDTO dto) throws SQLException;

    /**
     * Updates an existing item in the database.
     * @param dto the updated item data
     * @throws SQLException if the item doesn't exist or update fails
     */
    void Update(ItemDTO dto) throws SQLException;

    /**
     * Deletes an item from the database by its ID.
     * @param Id the item ID
     * @throws SQLException if deletion fails
     */
    void DeleteByItemId(int Id) throws SQLException;

    /**
     * Retrieves a specific item by its ID.
     * @param Id the item ID
     * @return the item data, or null if not found
     * @throws SQLException if a query error occurs
     */
    ItemDTO GetItemById(int Id) throws SQLException;

    /**
     * Marks an item as defective.
     * @param itemId the ID of the item to mark
     * @throws SQLException if update fails
     */
    void signAsDefective(int itemId) throws SQLException;

    /**
     * Retrieves all items from the database.
     * @return a list of all items
     * @throws SQLException if a query error occurs
     */
    List<ItemDTO> getAllItems() throws SQLException;

    /**
     * Retrieves all ItemDTOs that match the given product catalog number.
     * @param productId the product catalog number
     * @return a list of matching ItemDTOs
     */
    List<ItemDTO> getItemsByProductId(int productId);

    /**
     * Retrieves all ItemDTOs that are marked as defective.
     * @return a list of defective ItemDTOs
     */
    List<ItemDTO> findDefectiveItems();

    List<ItemDTO> getItemsByBranchId(int branchId);

    void markItemAsDefective(int itemId, int branchId) throws SQLException;

    List<ItemDTO> getItemsByBranch(int branchId) throws SQLException;

    List<ItemDTO> getExpiredItemsByBranchId(int branchId, LocalDate today) throws SQLException;

}