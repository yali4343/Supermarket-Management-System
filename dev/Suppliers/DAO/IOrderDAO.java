package Suppliers.DAO;

import Suppliers.DTO.OrderDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrderDAO {
    /**
     * Insert a new order into the database
     * @param dto The order to insert
     * @throws SQLException if database error occurs
     */
    void insert(OrderDTO dto) throws SQLException;

    /**
     * Delete an order by its ID
     * @param orderId The ID of the order to delete
     * @throws SQLException if database error occurs
     */
    void deleteById(int orderId) throws SQLException;

    /**
     * Get an order by its ID
     * @param orderId The ID of the order to retrieve
     * @return The order if found, null otherwise
     * @throws SQLException if database error occurs
     */
    OrderDTO getById(int orderId) throws SQLException;

    /**
     * Get all orders in the system
     * @return List of all orders
     * @throws SQLException if database error occurs
     */
    List<OrderDTO> getAll() throws SQLException;

    /**
     * Get all orders for a specific supplier
     * @param supplierId The ID of the supplier
     * @return List of orders for the supplier
     * @throws SQLException if database error occurs
     */
    List<OrderDTO> getBySupplierId(int supplierId) throws SQLException;

    /**
     * Search orders with optional filters
     * @param startDate Optional start date for order search
     * @param endDate Optional end date for order search
     * @param supplierId Optional supplier ID filter
     * @return List of orders matching the criteria
     * @throws SQLException if database error occurs
     */
    List<OrderDTO> searchOrders(LocalDateTime startDate, LocalDateTime endDate, Integer supplierId) throws SQLException;

    /**
     * Clears all records from the orders table
     */
    void clearTable() throws SQLException;
}
