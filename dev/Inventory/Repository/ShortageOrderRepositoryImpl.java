package Inventory.Repository;

import Inventory.DAO.IShortageOrderDAO;
import Inventory.DAO.JdbcShortageOrderDAO;
import Inventory.DTO.ShortageOrderDTO;

import java.sql.SQLException;
import java.util.List;

public class ShortageOrderRepositoryImpl implements IShortageOrderRepository {

    private final IShortageOrderDAO dao;

    public ShortageOrderRepositoryImpl() {
        this.dao = new JdbcShortageOrderDAO();
    }

    @Override
    public void insert(ShortageOrderDTO dto) throws SQLException {
        dao.insertShortageOrder(dto);
    }

    @Override
    public void update(ShortageOrderDTO dto) throws SQLException {
        dao.updateShortageOrder(dto);
    }

    @Override
    public void deleteById(int id) throws SQLException {
        dao.deleteShortageOrderById(id);
    }

    @Override
    public ShortageOrderDTO getById(int id) throws SQLException {
        return dao.getShortageOrderById(id);
    }

    @Override
    public List<ShortageOrderDTO> getAll() throws SQLException {
        return dao.getAllShortageOrders();
    }

    @Override
    public String getLastOrderDateForProduct(int catalogNumber, int branchId) throws SQLException {
        return dao.getLastOrderDateForProduct(catalogNumber, branchId);
    }

    @Override
    public boolean hasBeenProcessedToday(int branchId) throws SQLException {
        return dao.hasBeenProcessedToday(branchId);
    }    @Override
    public void markProcessedForToday(int branchId) throws SQLException {
        dao.markProcessedForToday(branchId);
    }

    @Override
    public boolean hasPendingOrderForProduct(int catalogNumber, int branchId) throws SQLException {
        return dao.hasPendingOrderForProduct(catalogNumber, branchId);
    }

    /**
     * Updates a shortage order's status to DELIVERED and sets its completion date
     * @param orderDTO The order to mark as completed
     * @throws SQLException if a database error occurs
     */
    public void completeOrder(ShortageOrderDTO orderDTO) throws SQLException {
        orderDTO.setStatus("DELIVERED");
        // Current date will be set when updating in the database
        update(orderDTO);
    }
}
