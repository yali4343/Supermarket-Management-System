package InventorySupplier.Presentation;

import Inventory.Domain.InventoryController;
import Inventory.Init.InventoryInitializer;
import Inventory.Presentation.MenuController;
import Suppliers.Presentation.MainMenu;
import Suppliers.Init.SuppliersInitializer;
import InventorySupplier.SystemService.PeriodicOrderService;
import Suppliers.Init.SupplierRepositoryInitializer;
import Inventory.Repository.*;
import Inventory.DTO.*;
import Suppliers.Repository.IInventoryOrderRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class InventorySupplierMainMenu {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);        // Initialize tables for both modules upfront
        InventoryInitializer.initializeAllTables();
        SuppliersInitializer.initializeAllTables();

        // Create InventoryController upfront (not just inside handleInventory)
        InventoryController inventoryController = new InventoryController();

        System.out.println("Welcome to the Inventory-Suppliers Menu! Do you want to load data to database?");
        System.out.println("1. Load data");
        System.out.println("2. Start with an empty system");
        System.out.print("Enter your choice: ");
        int dataChoice = getValidatedInt(scanner);

        switch (dataChoice) {
            case 1 -> {
                System.out.println("Loading existing data from the database...");

                SuppliersInitializer suppliersInitializer = new SuppliersInitializer();
                LinkedHashMap<Integer, Integer> supplierIdAndAgreementsID = suppliersInitializer.initializeDatabase(true);

                InventoryInitializer.preloadAllInitialData(supplierIdAndAgreementsID);                // קריאה לטעינה מה-DB לזיכרון:
                inventoryController.loadFromDatabase();
            }
            case 2 -> System.out.println("Starting with an empty system.");
            default -> System.out.println("Invalid choice. Starting with empty system by default.");
        }

        boolean exitSystem = false;

        while (!exitSystem) {
            System.out.println("\n==============================================");
            System.out.println("Welcome to the Inventory-Suppliers Menu! What would you like to manage?");
            System.out.println("1. Inventory System");
            System.out.println("2. Supplier System");
            System.out.println("3. Exit the Inventory-Suppliers system");
            System.out.print("Enter your choice (1-3): ");

            int choice = getValidatedInt(scanner);

            switch (choice) {
                case 1 -> handleInventory(scanner, inventoryController);
                case 2 -> handleSuppliers(scanner);
                case 3 -> {
                    System.out.println("Exiting system and clearing all data...");
                    InventoryInitializer.clearAllTables();
                    new SuppliersInitializer().clearAllData();
                    System.out.println("✅ All data deleted. Goodbye!");
                    exitSystem = true;
                }
                default -> System.out.println("Invalid choice. Please enter 1 to 4.");
            }
        }

        scanner.close();
    }    private static void handleInventory(Scanner scanner, InventoryController inventoryController) {
        System.out.println("You have selected Inventory.");

        System.out.print("Enter your Branch ID (1-10): ");
        int branchId = getValidatedInt(scanner);

        // Automatically check and process pending orders for this branch
        checkAndProcessPendingOrders(inventoryController, branchId);

        MenuController inventoryMenu = new MenuController(inventoryController, branchId);
        inventoryMenu.runMenu(); // Returns to main menu
    }

    private static void handleSuppliers(Scanner scanner) throws SQLException {
        System.out.println("You have selected Suppliers Menu.");
        MainMenu.run(scanner,false);
    }

    private static int getValidatedInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine(); // Clear buffer to avoid issues with nextLine
        return result;
    }

    /**
     * Automatically check for and process pending orders (both shortage and periodic orders)
     * scheduled to arrive today based on their days_in_the_week field.
     * 
     * @param inventoryController The inventory controller instance
     * @param branchId The branch ID to check orders for
     */
    private static void checkAndProcessPendingOrders(InventoryController inventoryController, int branchId) {
        System.out.println("\n🔄 Checking for pending orders scheduled for today...");
        
        boolean anyOrdersProcessed = false;
        String currentDay = LocalDate.now().getDayOfWeek().name().toUpperCase();
        
        try {
            // 1. Check and process pending periodic orders
            System.out.println("📋 Checking periodic orders for " + currentDay + "...");
            
            IPeriodicOrderRepository periodicOrderRepository = new PeriodicOrderRepositoryImpl();
            IItemRepository itemRepository = new ItemRepositoryImpl();
            IInventoryOrderRepository supplierOrderRepository = new SupplierRepositoryInitializer().getSupplierOrderRepository();
            
            PeriodicOrderService periodicOrderService = new PeriodicOrderService(
                supplierOrderRepository, 
                periodicOrderRepository, 
                itemRepository
            );
            
            boolean periodicOrdersProcessed = periodicOrderService.start(branchId);
            if (periodicOrdersProcessed) {
                System.out.println("✅ Periodic orders processed successfully for branch " + branchId);
                anyOrdersProcessed = true;
            } else {
                System.out.println("ℹ️ No periodic orders scheduled for today at branch " + branchId);
            }
            
            // 2. Check and process pending shortage orders scheduled for today
            System.out.println("📦 Checking shortage orders for " + currentDay + "...");
            
            IShortageOrderRepository shortageOrderRepository = new ShortageOrderRepositoryImpl();
            List<ShortageOrderDTO> todaysShortageOrders = shortageOrderRepository.getAll().stream()
                .filter(order -> order.getBranchId() == branchId && 
                               "PENDING".equals(order.getStatus()) &&
                               order.getDaysInTheWeek() != null && 
                               order.getDaysInTheWeek().toUpperCase().contains(currentDay))
                .collect(Collectors.toList());
                
            if (!todaysShortageOrders.isEmpty()) {
                System.out.println("📦 Processing " + todaysShortageOrders.size() + " shortage orders scheduled for today...");
                
                for (ShortageOrderDTO order : todaysShortageOrders) {
                    // Get product details
                    ProductDTO product = inventoryController.getProductRepository()
                        .getProductByCatalogNumber(order.getProductCatalogNumber());
                    if (product == null) continue;

                    // Get current inventory level
                    int currentStock = itemRepository.countItemsByCatalogNumber(
                        order.getProductCatalogNumber(), branchId);                    // Add new items to inventory
                    for (int i = 0; i < order.getQuantity(); i++) {
                        ItemDTO newItem = new ItemDTO(
                            order.getProductCatalogNumber(), 
                            branchId,
                            "Warehouse", // storage_location
                            "A1", // section_in_store
                            false, // is_defective
                            LocalDate.now().plusWeeks(1).toString() // item_expiring_date - one week from today
                        );
                        itemRepository.addItem(newItem);
                    }

                    // Update order status
                    order.setStatus("DELIVERED");
                    shortageOrderRepository.update(order);

                    // Get updated inventory level
                    int newStock = itemRepository.countItemsByCatalogNumber(
                        order.getProductCatalogNumber(), branchId);

                    System.out.println("✅ Delivered shortage order #" + order.getOrderId() + ":");
                    System.out.println("   Product: " + product.getProductName() + " (Catalog #" + product.getCatalogNumber() + ")");
                    System.out.println("   Previous stock: " + currentStock + " → New stock: " + newStock);
                    System.out.println("   Added quantity: " + order.getQuantity());
                    
                    anyOrdersProcessed = true;
                }
                
                // Mark shortage orders as processed for today to prevent duplicate processing
                shortageOrderRepository.markProcessedForToday(branchId);
                
            } else {
                System.out.println("ℹ️ No shortage orders scheduled for today at branch " + branchId);
            }
            
            // Summary message
            if (anyOrdersProcessed) {
                System.out.println("\n🎉 Order processing completed! Your inventory has been updated with today's deliveries.");
                System.out.println("   You can view the updated stock levels in the inventory reports.");
            } else {
                System.out.println("\nℹ️ No orders were scheduled for delivery today at branch " + branchId + ".");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error while checking pending orders: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while processing orders: " + e.getMessage());
        }
        
        System.out.println("════════════════════════════════════════════════════════════════");
    }
}
