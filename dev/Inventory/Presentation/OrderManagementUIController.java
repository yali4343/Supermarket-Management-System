package Inventory.Presentation;

import Inventory.DTO.PeriodicOrderDTO;
import Inventory.DTO.ProductDTO;
import Inventory.DTO.ShortageOrderDTO;
import Inventory.Domain.InventoryController;
import InventorySupplier.SystemService.ShortageOrderService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for order-related UI operations.
 * Handles all interactions related to periodic and shortage orders.
 */
public class OrderManagementUIController extends BaseUIController {

    /**
     * Constructor for OrderManagementUIController.
     *
     * @param inventory_controller The inventory controller instance
     * @param currentBranchId The current branch ID
     */
    public OrderManagementUIController(InventoryController inventory_controller, int currentBranchId) {
        super(inventory_controller, currentBranchId);
    }

    /**
     * Shows shortage alerts for the current branch.
     */
    public void showShortageAlertsForBranch() {
        try {
            Map<Integer, Integer> shortages = inventory_controller.getReportController().getShortageProductsMap(current_branch_id);
            
            if (shortages.isEmpty()) {
                System.out.println("No shortage alerts for Branch " + current_branch_id + ".");
                return;
            }
            
            printSectionHeader("Current Shortages in Branch " + current_branch_id);
            
            for (Map.Entry<Integer, Integer> entry : shortages.entrySet()) {
                int catalogNumber = entry.getKey();
                int shortageAmount = entry.getValue();
                
                try {
                    ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(catalogNumber);
                    String productName = product != null ? product.getProductName() : "Unknown Product";
                    
                    System.out.printf("Product: %s (Catalog #%d) - Shortage: %d units%n", 
                            productName, catalogNumber, shortageAmount);
                    
                } catch (Exception e) {
                    System.out.println("Product with Catalog #" + catalogNumber + " - Shortage: " + shortageAmount + " units");
                }
            }
            
            System.out.println("\nTotal shortage products: " + shortages.size());
            
        } catch (Exception e) {
            System.out.println("❌ Error getting shortage alerts: " + e.getMessage());
        }
    }    /**
     * Updates a periodic order in the system.
     */
    public void updatePeriodicOrder() {
        try {
            // Display all periodic orders for the branch first
            viewAllPeriodicOrders();
            
            System.out.println("\nEnter periodic order ID to update (or 0 to cancel):");
            int orderId = Integer.parseInt(scan.nextLine());
            
            if (orderId == 0) {
                System.out.println("Update canceled.");
                return;
            }
            
            PeriodicOrderDTO order = inventory_controller.getPeriodicOrderRepository().getPeriodicOrderById(orderId);
            
            if (order == null || order.getBranchId() != current_branch_id) {
                System.out.println("Order not found or does not belong to this branch.");
                return;
            }
            
            // Check if order is scheduled for today or tomorrow
            if (isOrderScheduledForTodayOrTomorrow(order.getDaysInTheWeek())) {
                System.out.println("❌ Cannot update order #" + orderId + " as it is scheduled for delivery today or tomorrow.");
                System.out.println("   Orders scheduled for immediate delivery cannot be modified.");
                return;
            }
            
            System.out.println("Current details:");
            System.out.println("- Product: " + order.getProductCatalogNumber());
            System.out.println("- Quantity: " + order.getQuantity());
            System.out.println("- Supply Days: " + order.getDaysInTheWeek() + " (set by supplier, cannot be changed)");
            
            System.out.println("\nUpdate quantity? (Y/N)");
            if (scan.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("Enter new quantity:");
                int newQuantity = Integer.parseInt(scan.nextLine());
                order.setQuantity(newQuantity);
            }
            
            try {
                inventory_controller.getPeriodicOrderRepository().updatePeriodicOrder(order);
                System.out.println("✅ Order #" + orderId + " successfully updated.");
            } catch (Exception e) {
                System.out.println("❌ Failed to update order: " + e.getMessage());
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid number input: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Places a shortage-based supplier order.
     */
    public void placeShortageBasedSupplierOrder() {
        try {
            List<ShortageOrderDTO> shortageOrders = inventory_controller.getShortageOrderRepository().getAll()
                .stream()
                .filter(o -> o.getBranchId() == current_branch_id && o.getStatus().equals("PENDING"))
                .collect(Collectors.toList());

            if (!shortageOrders.isEmpty()) {
                printSectionHeader("Existing Pending Shortage Orders");
                for (ShortageOrderDTO order : shortageOrders) {
                    ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(order.getProductCatalogNumber());
                    String productName = product != null ? product.getProductName() : "Unknown Product";
                    
                    System.out.println("Order #" + order.getOrderId() + ": " + productName + 
                            " (Catalog #" + order.getProductCatalogNumber() + ") - " + order.getQuantity() + " units");
                }
                System.out.println();
            } else {
                System.out.println("No existing pending shortage orders found.\n");
            }

            boolean anyOrdersProcessed = false;
            
            System.out.println("\n----------- Checking for New Shortages -----------");
            
            // Use the existing shortage detection system that correctly handles products with 0 stock
            Map<Integer, Integer> shortages = inventory_controller.getReportController().getShortageProductsMap(current_branch_id);
            
            boolean newShortagesFound = false;
            if (!shortages.isEmpty()) {
                for (Map.Entry<Integer, Integer> entry : shortages.entrySet()) {
                    int catalogNumber = entry.getKey();
                    int shortageAmount = entry.getValue();
                    
                    // Check if this shortage already has a pending order
                    boolean alreadyOrdered = shortageOrders.stream()
                            .anyMatch(o -> o.getProductCatalogNumber() == catalogNumber);
                    
                    if (!alreadyOrdered) {
                        try {
                            ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(catalogNumber);
                            if (product != null) {
                                String productName = product.getProductName();
                                  System.out.println("Found new shortage for: " + productName + " (Catalog #" + 
                                        catalogNumber + "). Shortage amount: " + shortageAmount + " units");
                                
                                // Automatically create and process a shortage order
                                Map<Integer, Integer> shortageToProcess = new HashMap<>();
                                shortageToProcess.put(catalogNumber, shortageAmount);
                                
                                // Create a ShortageOrderService instance with required repositories
                                try {
                                    // Import necessary classes from Suppliers module
                                    Suppliers.Repository.IInventoryOrderRepository supplierRepo = 
                                        new Suppliers.Init.SupplierRepositoryInitializer().getSupplierOrderRepository();
                                    
                                    // Create service and process the shortage
                                    ShortageOrderService shortageService = new ShortageOrderService(
                                        supplierRepo, 
                                        inventory_controller.getShortageOrderRepository());
                                    
                                    shortageService.onWakeUp(shortageToProcess, current_branch_id);
                                    
                                    anyOrdersProcessed = true;
                                    newShortagesFound = true;
                                    System.out.println("✅ Order placed successfully");
                                } catch (Exception ex) {
                                    System.out.println("❌ Failed to place order: " + ex.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("❌ Error processing product with catalog #" + catalogNumber + ": " + e.getMessage());
                        }
                    }
                }
            }            if (!newShortagesFound) {
                System.out.println("No new shortages found requiring orders.");
            }
            
            if (anyOrdersProcessed) {
                System.out.println("\n✅ All shortage orders have been automatically processed.");
                System.out.println("You can view the status of your orders in 'View Pending Shortage Orders'.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error placing shortage-based supplier order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Views all periodic orders for the current branch.
     */
    public void viewAllPeriodicOrders() {
        try {
            List<PeriodicOrderDTO> orders = inventory_controller.getPeriodicOrderRepository()
                .getAllPeriodicOrders();

            // Filter for current branch            
            orders = orders.stream()
                .filter(order -> order.getBranchId() == current_branch_id)
                .collect(Collectors.toList());
            
            if (orders.isEmpty()) {
                System.out.println("\nNo periodic orders found for Branch #" + current_branch_id);
                return;
            }

            printSectionHeader("All Periodic Orders");
            for (PeriodicOrderDTO order : orders) {
                ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(order.getProductCatalogNumber());
                String productName = product != null ? product.getProductName() : "Unknown Product";                System.out.println("Periodic Order #" + order.getOrderId());
                System.out.println("Product: " + productName + " (Catalog #" + order.getProductCatalogNumber() + ")");
                System.out.println("Supply Days: " + order.getDaysInTheWeek());
                System.out.println("Quantity: " + order.getQuantity());
                System.out.println("Order Date: " + order.getOrderDate());
                
                // Use supplier name from the order, or get supplier name based on ID if it's missing
                String supplierName = order.getSupplierName();
                if (supplierName == null || supplierName.trim().isEmpty() || supplierName.equals("Unknown")) {
                    // Get supplier name by ID using the appropriate supplier service
                    try {
                        int supplierId = order.getSupplierId();
                        supplierName = getSupplierNameById(supplierId);
                    } catch (Exception e) {
                        supplierName = "Unknown";
                    }
                }
                
                System.out.println("Supplier: " + supplierName + " (ID: " + order.getSupplierId() + ")");
                printSeparator();
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve periodic orders: " + e.getMessage());
        }
    }

    /**
     * Views pending shortage orders for the current branch.
     */
    public void viewPendingShortageOrders() {
        try {
            List<ShortageOrderDTO> orders = inventory_controller.getShortageOrderRepository()
                .getAll();

            // Filter for current branch and pending status
            orders = orders.stream()
                .filter(order -> order.getBranchId() == current_branch_id && 
                               order.getStatus().equals("PENDING"))
                .collect(Collectors.toList());
            
            if (orders.isEmpty()) {
                System.out.println("\nNo pending shortage orders found for Branch #" + current_branch_id);
                return;
            }

            printSectionHeader("Pending Shortage Orders");
            for (ShortageOrderDTO order : orders) {
                ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(order.getProductCatalogNumber());
                String productName = product != null ? product.getProductName() : "Unknown Product";

                System.out.println("Shortage Order #" + order.getOrderId());
                System.out.println("Product: " + productName + " (Catalog #" + order.getProductCatalogNumber() + ")");
                System.out.println("Quantity: " + order.getQuantity());
                System.out.println("Current Stock: " + order.getCurrentStock());                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Supplier: " + order.getSupplierName() + " (ID: " + order.getSupplierId() + ")");
                System.out.println("Status: " + order.getStatus());
                
                printSeparator();
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve shortage orders: " + e.getMessage());
        }
    }
      /**
     * Gets the current day of the week.
     * 
     * @return The current day name in uppercase
     */
    @SuppressWarnings("unused")
    private String getCurrentDayOfWeek() {
        return LocalDate.now().getDayOfWeek().toString();
    }
    
    /**
     * Gets a supplier's delivery schedule.
     * 
     * @param supplierId The supplier ID
     * @return Comma-separated delivery days
     */
    @SuppressWarnings("unused")
    private String getSupplierDeliverySchedule(int supplierId) {
        try {
            // This should be implemented to fetch the supplier's delivery days from the database
            // For now, we're returning a mock value based on supplier ID
            switch (supplierId) {
                case 105:
                case 106: return "MONDAY,WEDNESDAY,FRIDAY";
                case 107:
                case 108: return "TUESDAY,THURSDAY";
                case 109:
                case 110: return "MONDAY,THURSDAY";
                case 111:
                case 112: return "WEDNESDAY,FRIDAY";
                default: return "MONDAY,WEDNESDAY,FRIDAY";
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to get supplier delivery days: " + e.getMessage());
            return "";
        }
    }    /**
     * Calculates estimated arrival time based on current day and delivery schedule.
     * 
     * @param currentDay The current day of the week
     * @param deliveryDays Comma-separated delivery days
     * @return A human-readable estimated arrival time
     */
    @SuppressWarnings("unused")
    private String calculateEstimatedArrival(String currentDay, String deliveryDays) {
        if (deliveryDays == null || deliveryDays.isEmpty()) {
            return "Unknown (no delivery schedule available)";
        }
        
        // Convert delivery days string to list and standardize format
        List<String> deliveryDaysList = Arrays.stream(deliveryDays.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        
        // List of days of week in order
        List<String> daysOfWeek = Arrays.asList(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        );
        
        // Determine the current day index
        int currentDayIndex = daysOfWeek.indexOf(currentDay);
        if (currentDayIndex == -1) {
            return "Unknown (could not determine current day)";
        }
        
        // Calculate days until next delivery
        int daysUntilDelivery = Integer.MAX_VALUE; // Start with maximum value
        
        for (String deliveryDay : deliveryDaysList) {
            int deliveryDayIndex = daysOfWeek.indexOf(deliveryDay);
            if (deliveryDayIndex == -1) continue; // Skip invalid day names
            
            int daysUntil;
            if (deliveryDayIndex > currentDayIndex) {
                // Delivery day is later this week
                daysUntil = deliveryDayIndex - currentDayIndex;
            } else if (deliveryDayIndex < currentDayIndex) {
                // Delivery day is next week
                daysUntil = 7 - currentDayIndex + deliveryDayIndex;
            } else {
                // Today is a delivery day - check if orders placed today will be delivered today
                // For simplicity, we'll assume if it's a delivery day, it will be delivered the same day
                return "Today";
            }
            
            // Keep the smallest days until delivery
            if (daysUntil < daysUntilDelivery) {
                daysUntilDelivery = daysUntil;
            }
        }
        
        // If no valid delivery day was found or calculated
        if (daysUntilDelivery == Integer.MAX_VALUE) {
            return "Unknown (no valid delivery days found)";
        }
        
        // Format return message
        if (daysUntilDelivery == 1) {
            return "Tomorrow";
        } else if (daysUntilDelivery == 2) {
            return "In two days from today";
        } else {
            return "In " + daysUntilDelivery + " days";
        }
    }
      /**
     * Gets the supplier name based on the supplier ID.
     * 
     * @param supplierId The ID of the supplier
     * @return The supplier name, or a default value if not found
     */
    private String getSupplierNameById(int supplierId) {
        // Map of common supplier IDs to their names
        // This should be replaced with a database lookup in a production environment
        switch (supplierId) {
            case 105: return "Tnuva";
            case 106: return "Tnuva";
            case 107: return "Osem";
            case 108: return "Heinz";
            case 109: return "Sano";
            case 110: return "Elite";
            case 111: return "Neviot";
            case 112: return "Telma";
            default: return "Supplier #" + supplierId;
        }
    }

    /**
     * Checks if a periodic order is scheduled for delivery today or tomorrow.
     * 
     * @param daysInTheWeek Comma-separated delivery days from the order
     * @return true if the order is scheduled for today or tomorrow, otherwise false
     */
    private boolean isOrderScheduledForTodayOrTomorrow(String daysInTheWeek) {
        if (daysInTheWeek == null || daysInTheWeek.isEmpty()) {
            return false;
        }
        
        // Get today and tomorrow
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        String todayName = today.getDayOfWeek().toString();
        String tomorrowName = tomorrow.getDayOfWeek().toString();
        
        // Convert delivery days string to list and standardize format
        List<String> deliveryDaysList = Arrays.stream(daysInTheWeek.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        
        // Check if today or tomorrow is in the delivery days
        return deliveryDaysList.contains(todayName) || deliveryDaysList.contains(tomorrowName);
    }
}
