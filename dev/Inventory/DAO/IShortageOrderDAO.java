package Inventory.DAO;

import Inventory.DTO.ShortageOrderDTO;

import java.sql.SQLException;
import java.util.List;

public interface IShortageOrderDAO {
    void insertShortageOrder(ShortageOrderDTO dto) throws SQLException;
    void updateShortageOrder(ShortageOrderDTO dto) throws SQLException;
    void deleteShortageOrderById(int id) throws SQLException;
    ShortageOrderDTO getShortageOrderById(int id) throws SQLException;
    List<ShortageOrderDTO> getAllShortageOrders() throws SQLException;    String getLastOrderDateForProduct(int catalogNumber, int branchId) throws SQLException;
    boolean hasBeenProcessedToday(int branchId) throws SQLException;
    void markProcessedForToday(int branchId) throws SQLException;
    boolean hasPendingOrderForProduct(int catalogNumber, int branchId) throws SQLException;
}
