package Inventory.Repository;

import Inventory.DTO.PeriodicOrderDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Repository interface for handling periodic order business logic operations.
 */
public interface IPeriodicOrderRepository {    List<PeriodicOrderDTO> getAllPeriodicOrders() throws SQLException;

    PeriodicOrderDTO getPeriodicOrderById(int orderId) throws SQLException;

    void insertPeriodicOrder(PeriodicOrderDTO dto) throws SQLException;

    void updatePeriodicOrder(PeriodicOrderDTO dto) throws SQLException;

    void deletePeriodicOrderById(int orderId) throws SQLException;

    void saveAll(List<PeriodicOrderDTO> orders) throws SQLException;

}
