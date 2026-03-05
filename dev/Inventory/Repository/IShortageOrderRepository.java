package Inventory.Repository;

import Inventory.DTO.ShortageOrderDTO;

import java.sql.SQLException;
import java.util.List;

public interface IShortageOrderRepository {
    void insert(ShortageOrderDTO dto) throws SQLException;
    void update(ShortageOrderDTO dto) throws SQLException;
    void deleteById(int id) throws SQLException;
    ShortageOrderDTO getById(int id) throws SQLException;
    List<ShortageOrderDTO> getAll() throws SQLException;
    String getLastOrderDateForProduct(int catalogNumber, int branchId) throws SQLException;

    /**
     * Checks if shortage orders for the given branch have already been processed today
     * @param branchId The branch ID to check
     * @return true if orders have been processed today, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean hasBeenProcessedToday(int branchId) throws SQLException;    /**
     * Marks shortage orders for the given branch as processed for today
     * @param branchId The branch ID to mark as processed
     * @throws SQLException if a database error occurs
     */
    void markProcessedForToday(int branchId) throws SQLException;

    /**
     * Checks if there's already a pending order for the given product and branch
     * @param catalogNumber The product catalog number
     * @param branchId The branch ID
     * @return true if a pending order exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean hasPendingOrderForProduct(int catalogNumber, int branchId) throws SQLException;
}
