package Inventory.Repository;

import Inventory.DAO.IPeriodicOrderDAO;
import Inventory.DAO.JdbcPeriodicOrderDAO;
import Inventory.DTO.PeriodicOrderDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Concrete implementation of the IPeriodicOrderRepository interface,
 * delegating work to the DAO layer for database operations.
 */
public class PeriodicOrderRepositoryImpl implements IPeriodicOrderRepository {

    private final IPeriodicOrderDAO periodicOrderDAO;

    public PeriodicOrderRepositoryImpl() {
        this.periodicOrderDAO = new JdbcPeriodicOrderDAO();
    }

    @Override
    public List<PeriodicOrderDTO> getAllPeriodicOrders() throws SQLException {
        return periodicOrderDAO.getAllPeriodicOrders();
    }

    @Override
    public PeriodicOrderDTO getPeriodicOrderById(int orderId) throws SQLException {
        return periodicOrderDAO.getPeriodicOrderById(orderId);
    }

    @Override
    public void insertPeriodicOrder(PeriodicOrderDTO dto) throws SQLException {
        periodicOrderDAO.insertPeriodicOrder(dto);
    }

    @Override
    public void updatePeriodicOrder(PeriodicOrderDTO dto) throws SQLException {
        periodicOrderDAO.updatePeriodicOrder(dto);
    }

    @Override
    public void deletePeriodicOrderById(int orderId) throws SQLException {
        periodicOrderDAO.deletePeriodicOrderById(orderId);
    }

    @Override
    public void saveAll(List<PeriodicOrderDTO> orders) throws SQLException {
        for (PeriodicOrderDTO order : orders) {
            periodicOrderDAO.insertPeriodicOrder(order);
        }
    }
}
