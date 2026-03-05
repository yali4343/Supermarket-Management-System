package InventorySupplier.SystemService;

import Inventory.DTO.ItemDTO;
import Inventory.DTO.PeriodicOrderDTO;
import Inventory.Repository.*;
import Suppliers.Repository.IInventoryOrderRepository;
import Inventory.DataBase.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PeriodicOrderService {
    private final IPeriodicOrderRepository periodicOrderRepository;
    private final IItemRepository itemRepository;

    public PeriodicOrderService(
            IInventoryOrderRepository orderRepository,
            IPeriodicOrderRepository periodicOrderRepository,
            IItemRepository itemRepository
    ) {
        this.periodicOrderRepository = periodicOrderRepository;
        this.itemRepository = itemRepository;
    }/**
     * Process periodic orders scheduled for today.
     * Periodic orders are processed but kept in the system for future scheduling.
     * 
     * @param branchId The ID of the branch to process orders for
     * @return true if any orders were processed, false otherwise
     */
    public boolean start(int branchId) {
        Connection connection = null;
        boolean ordersProcessed = false;

        try {
            connection = DatabaseConnector.connect();
            connection.setAutoCommit(false); // Start transaction

            String currentDay = LocalDate.now().getDayOfWeek().name();

            // Process periodic orders scheduled for today
            List<PeriodicOrderDTO> periodicOrders = periodicOrderRepository.getAllPeriodicOrders()
                    .stream()
                    .filter(order ->
                        order.getBranchId() == branchId &&
                        order.getDaysInTheWeek() != null &&
                        order.getDaysInTheWeek().toUpperCase().contains(currentDay)
                    )
                    .toList();

            // Process periodic orders
            for (PeriodicOrderDTO order : periodicOrders) {
                try {
                    processPeriodicOrder(order, branchId);
                    ordersProcessed = true;
                    System.out.println("✅ Processed periodic order #" + order.getOrderId() +
                                     " for product " + order.getProductCatalogNumber() +
                                     " (Quantity: " + order.getQuantity() + ")");
                } catch (Exception e) {
                    System.err.println("❌ Failed to process periodic order #" + order.getOrderId() + ": " + e.getMessage());
                    throw e; // Propagate to trigger rollback
                }
            }

            connection.commit(); // Commit transaction
            return ordersProcessed;        } catch (SQLException e) {
            System.err.println("❌ Database error during order processing: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                    System.err.println("⚠️ Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("❌ Failed to rollback transaction: " + ex.getMessage());
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error during order processing: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                    System.err.println("⚠️ Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("❌ Failed to rollback transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("❌ Failed to close database connection: " + e.getMessage());
                }
            }
        }
    }    private void processPeriodicOrder(PeriodicOrderDTO order, int branchId) throws SQLException {
        // Add items to inventory
        for (int i = 0; i < order.getQuantity(); i++) {
            ItemDTO item = new ItemDTO(
                    order.getProductCatalogNumber(),
                    branchId,
                    "Warehouse",
                    "A1", // section_in_store - default value
                    false,
                    LocalDate.now().plusWeeks(1).toString() // item_expiring - one week from today
            );
            itemRepository.addItem(item);
        }
    }
}
