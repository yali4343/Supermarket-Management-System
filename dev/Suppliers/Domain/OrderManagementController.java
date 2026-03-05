package Suppliers.Domain;

import Suppliers.DTO.OrderDTO;
import Suppliers.DAO.IOrderDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class OrderManagementController {
    private final IOrderDAO orderDAO;

    public OrderManagementController(IOrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public List<OrderDTO> getAllOrders() throws SQLException {
        return orderDAO.getAll();
    }

    public List<OrderDTO> searchOrders(LocalDateTime startDate, LocalDateTime endDate, Integer supplierId) throws SQLException {
        return orderDAO.searchOrders(startDate, endDate, supplierId);
    }

    public OrderDTO getOrderById(int orderId) throws SQLException {
        return orderDAO.getById(orderId);
    }

    public List<OrderDTO> getOrdersBySupplierId(int supplierId) throws SQLException {
        return orderDAO.getBySupplierId(supplierId);
    }
}
