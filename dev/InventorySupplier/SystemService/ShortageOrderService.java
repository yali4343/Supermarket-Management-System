package InventorySupplier.SystemService;

import Inventory.DTO.ShortageOrderDTO;
import Inventory.Repository.IShortageOrderRepository;
import Suppliers.DTO.OrderProductDetailsDTO;
import Suppliers.Domain.OrderByShortageController;
import Suppliers.Repository.IInventoryOrderRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortageOrderService implements WakeUpListener {
    private final OrderByShortageController orderByShortageController;
    private final IShortageOrderRepository shortageOrderRepository;

    public ShortageOrderService(IInventoryOrderRepository orderRepository, IShortageOrderRepository shortageOrderRepository) {
        this.orderByShortageController = new OrderByShortageController(orderRepository);
        this.shortageOrderRepository = shortageOrderRepository;
    }
    public ShortageOrderService(OrderByShortageController controller, IShortageOrderRepository shortageOrderRepository) {
        this.orderByShortageController = controller;
        this.shortageOrderRepository = shortageOrderRepository;
    }

    /**
     * Triggered manually with parameters for shortage orders.
     * Converts the given shortage map to HashMap and creates a supplier order.
     */
    public void onWakeUp(Map<Integer, Integer> shortageMap, int branchId) {
        try {
            long fakePhoneNumber = 1234567890L; // demo purpose only

            // Convert to HashMap if needed
            HashMap<Integer, Integer> shortageHashMap = new HashMap<>(shortageMap);

            List<OrderProductDetailsDTO> orderDetails = orderByShortageController.getShortageOrderProductDetails(shortageHashMap, fakePhoneNumber);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String orderDate = LocalDateTime.now().format(formatter);            for (OrderProductDetailsDTO details : orderDetails) {
                // Check if there's already a pending order for this product and branch
                if (shortageOrderRepository.hasPendingOrderForProduct(details.getProductId(), branchId)) {
                    System.out.println("⚠️ Skipping duplicate order for product " + details.getProductId() + 
                                     " at branch " + branchId + " - pending order already exists");
                    continue;
                }

                ShortageOrderDTO dto = new ShortageOrderDTO(
                        0, // Auto-incremented order_id
                        details.getProductId(),
                        details.getQuantity(),
                        details.getPrice(),
                        details.getDiscount(),
                        orderDate,
                        branchId,
                        String.join(", ", details.getDeliveryDays()), // daysInTheWeek
                        details.getSupplierId(),
                        details.getSupplierName(),
                        details.getQuantity(), // quantityNeeded same as order quantity
                        0, // currentStock starts at 0
                        "PENDING" // initial status
                );
                
                shortageOrderRepository.insert(dto);
                System.out.println("✅ Created new shortage order for product " + details.getProductId() + 
                                 " at branch " + branchId);
            }

            System.out.println("✅ Shortage order saved to DB successfully for branch " + branchId);

        } catch (SQLException e) {
            System.err.println("❌ Failed to place shortage order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Not used in this implementation — required by interface.
     */
    @Override
    public void onWakeUp() {
        // Not used (left empty intentionally)
    }
}
