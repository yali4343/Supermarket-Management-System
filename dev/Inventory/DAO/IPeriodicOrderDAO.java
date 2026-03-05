package Inventory.DAO;

import Inventory.DTO.PeriodicOrderDTO;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for managing periodic order operations in the database.
 */
public interface IPeriodicOrderDAO {

    /**
     * Inserts a new periodic order into the database.
     *
     * @param dto the periodic order to insert
     * @throws SQLException if a database error occurs
     */
    void insertPeriodicOrder(PeriodicOrderDTO dto) throws SQLException;

    /**
     * Updates an existing periodic order in the database.
     *
     * @param dto the updated periodic order data
     * @throws SQLException if the periodic order doesn't exist or update fails
     */
    void updatePeriodicOrder(PeriodicOrderDTO dto) throws SQLException;

    /**
     * Deletes a periodic order from the database by its ID.
     *
     * @param orderId the periodic order ID
     * @throws SQLException if deletion fails
     */
    void deletePeriodicOrderById(int orderId) throws SQLException;

    /**
     * Retrieves a periodic order from the database by its ID.
     *
     * @param orderId the periodic order ID
     * @return the periodic order data, or null if not found
     * @throws SQLException if a query error occurs
     */
    PeriodicOrderDTO getPeriodicOrderById(int orderId) throws SQLException;

    /**
     * Retrieves all periodic orders from the database.
     *
     * @return a list of all periodic orders
     * @throws SQLException if a query error occurs
     */
    List<PeriodicOrderDTO> getAllPeriodicOrders() throws SQLException;
}
