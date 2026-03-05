package Inventory.Presentation;

import Inventory.Domain.*;
import java.util.Scanner;

/**
 * Main menu controller that delegates to specialized controllers.
 * Acts as a facade for all the UI controllers.
 */
public class MenuController {
    private final Scanner scan;
    private final InventoryController inventory_controller;
    private final int current_branch_id;

    // Specialized UI controllers
    private final ItemManagementUIController itemController;
    private final ProductManagementUIController productController;
    private final ReportUIController reportController;
    private final OrderManagementUIController orderController;
    private final DiscountUIController discountController;

    /**
     * Constructor for MenuController.
     *
     * @param inventory_controller The inventory controller instance
     * @param currentBranchId The current branch ID
     */
    public MenuController(InventoryController inventory_controller, int currentBranchId) {
        this.scan = new Scanner(System.in);
        this.inventory_controller = inventory_controller;
        this.current_branch_id = currentBranchId;

        // Initialize all specialized controllers
        this.itemController = new ItemManagementUIController(inventory_controller, currentBranchId);
        this.productController = new ProductManagementUIController(inventory_controller, currentBranchId);
        this.reportController = new ReportUIController(inventory_controller, currentBranchId);
        this.orderController = new OrderManagementUIController(inventory_controller, currentBranchId);
        this.discountController = new DiscountUIController(inventory_controller, currentBranchId);
    }

    /**
     * Prints a welcome message for the inventory module
     */
    private void printWelcome() {
        System.out.println("""
    =============================================
    |                                            |
    |   Welcome to the Inventory Module!     |
    |                                            |
    =============================================""");
        System.out.println("        You are currently in Branch #" + current_branch_id);
        System.out.println("=============================================\n");
    }

    /**
     * Main entry point to run the inventory menu.
     */
    public void runMenu() {
        printWelcome();
        int mainChoice = 0;
        while (mainChoice != 3) {
            System.out.println("""
        Main Menu:
        1. Inventory Functions (Part 1)
        2. Order Management & Tracking Menu: (Part 2)
        3. Exit
        """);

            try {
                mainChoice = Integer.parseInt(scan.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid option (1-3).");
                continue;
            }

            switch (mainChoice) {
                case 1 -> runPart1InventoryMenu();
                case 2 -> runPart2InventoryMenu();
                case 3 -> System.out.println("Thank you for using the Inventory Module. Goodbye!");
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Runs the order management menu (Part 2).
     */
    private void runPart2InventoryMenu() {
        int choice = 0;
        while (choice != 6) {
            System.out.println("""
            
            Order Management Menu:
            1. Show current shortage alerts
            2. Update a periodic order
            3. Place shortage-based supplier orders
            4. View all periodic orders
            5. View pending shortage orders
            6. Return to main menu
            """);

            try {
                choice = Integer.parseInt(scan.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> orderController.showShortageAlertsForBranch();
                case 2 -> orderController.updatePeriodicOrder();
                case 3 -> orderController.placeShortageBasedSupplierOrder();
                case 4 -> orderController.viewAllPeriodicOrders();
                case 5 -> orderController.viewPendingShortageOrders();
                case 6 -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter a number between 1-6.");
            }
        }
    }

    /**
     * Runs the inventory management menu (Part 1).
     */
    private void runPart1InventoryMenu() {
        int choice = 0;
        while (choice != 13) {
            printPart1Menu();
            try {
                choice = Integer.parseInt(scan.nextLine().trim());
                handleChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Prints the Part 1 menu options.
     */
    private void printPart1Menu() {
        System.out.println("""
    Inventory Module Menu:
    1. Show item details
    2. Add item(s) to inventory (new or existing product)
    3. Remove an item
    4. Show the purchase prices of a product
    5. Mark an item as defective
    6. Generate inventory report
    7. Generate a defective and expired items report
    8. Apply supplier/store discount to a product group
    9. Show product quantity in warehouse and store
    10. Generate a shortage inventory report
    11. Update product demand level
    12. Update item storage location
    13. Exit
    """);
    }

    /**
     * Handles the user's choice from the Part 1 menu.
     *
     * @param choice The menu option selected
     */
    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> itemController.showItemDetails();
            case 2 -> itemController.addItemsToInventory();
            case 3 -> itemController.removeItem();
            case 4 -> productController.showPurchasePrices();
            case 5 -> itemController.markAsDefect();
            case 6 -> reportController.generateInventoryReport();
            case 7 -> reportController.generateDefectAndExpiredReport();
            case 8 -> discountController.applyDiscount();
            case 9 -> productController.showProductQuantities();
            case 10 -> reportController.generateShortageInventoryReport();
            case 11 -> productController.updateProductDemandLevel();
            case 12 -> itemController.updateItemStorageLocation();
            case 13 -> System.out.println("Exiting the Inventory menu.");
            default -> System.out.println("Invalid option. Please try again.");
        }
    }
}
