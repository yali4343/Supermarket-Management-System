package Inventory.Presentation;

import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;
import Inventory.Domain.InventoryController;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for item-related UI operations.
 * Handles all interactions related to individual inventory items.
 */
public class ItemManagementUIController extends BaseUIController {

    /**
     * Constructor for ItemManagementUIController.
     *
     * @param inventory_controller The inventory controller instance
     * @param currentBranchId The current branch ID
     */
    public ItemManagementUIController(InventoryController inventory_controller, int currentBranchId) {
        super(inventory_controller, currentBranchId);
    }

    /**
     * Shows details of a specific item.
     */
    public void showItemDetails() {
        System.out.print("Enter item ID: ");
        int item_Id;
        try {
            item_Id = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numeric Item ID.");
            return;
        }

        String details = inventory_controller.getItemController().showItemDetails(item_Id, current_branch_id);

        if (details != null && !details.trim().isEmpty()
                && !details.contains("not found")
                && !details.contains("does not exist")) {
            printSectionHeader("Item Details");
            System.out.println(details);
        } else {
            System.out.println("No item with ID " + item_Id + " was found in Branch " + current_branch_id + ".");
        }
    }

    /**
     * Adds new item(s) to inventory.
     */
    public void addItemsToInventory() {
        System.out.println("Enter Product Catalog Number:");
        int catalogNumber = Integer.parseInt(scan.nextLine());

        boolean productExists = !inventory_controller.getProductController().isUnknownCatalogNumber(catalogNumber);

        if (productExists) {
            System.out.println("Product found in the system.");
            System.out.print("How many units would you like to add? ");
            int quantityToAdd = Integer.parseInt(scan.nextLine());

            System.out.print("Enter storage location for all units (Warehouse or InteriorStore): ");
            String storageLocation = scan.nextLine().trim();

            System.out.print("Enter expiry date for all units (format: dd/MM/yyyy): ");
            String expiryDate = scan.nextLine().trim();

            int nextItemId = inventory_controller.getItemController().getNextAvailableItemId();

            for (int i = 0; i < quantityToAdd; i++) {
                int currentItemId = nextItemId + i;
                inventory_controller.getItemController().addItem(
                        currentItemId,
                        current_branch_id,
                        catalogNumber,
                        storageLocation,
                        expiryDate
                );
            }

            System.out.println("\n-----------------------------------------");
            System.out.println(quantityToAdd + " items successfully added for Product Catalog Number " + catalogNumber + ".");
            System.out.println("-----------------------------------------\n");
        } else {
            System.out.println("Product not found. Please add the product first.");
        }
    }

    /**
     * Removes an item from inventory.
     */
    public void removeItem() {
        System.out.print("Enter item ID: ");
        int item_Id = Integer.parseInt(scan.nextLine());

        if (!inventory_controller.getItemController().itemExistsInBranch(item_Id, current_branch_id)) {
            System.out.println("Item does not exist in Branch " + current_branch_id + ".");
            return;
        }

        // Get item details before removal
        ItemDTO item = inventory_controller.getItemController().getItem(item_Id, current_branch_id);
        if (item == null) {
            System.out.println("❌ Failed to retrieve item details before removal.");
            return;
        }

        int catalog_number = item.getCatalogNumber();
        String product_name = inventory_controller.getItemController().getItemName(item_Id, current_branch_id);
        double sale_price = 0.0;

        try {
            ProductDTO productDTO = inventory_controller.getProductRepository().getProductByCatalogNumber(catalog_number);
            if (productDTO != null) {
                sale_price = productDTO.getSalePriceAfterStoreDiscount();
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to retrieve product details: " + e.getMessage());
        }

        System.out.println("What is the reason for removing the item?");
        System.out.println("(1) Purchase\n(2) Defect");
        int reason = Integer.parseInt(scan.nextLine());

        if (reason == 1) {
            inventory_controller.getItemController().removeItemByPurchase(item_Id, current_branch_id);
        } else if (reason == 2) {
            inventory_controller.getItemController().removeItemByDefect(item_Id, current_branch_id);
        } else {
            System.out.println("Invalid choice. Please enter 1 or 2.");
            return;
        }

        boolean alert = inventory_controller.getReportController().shouldTriggerAlertAfterRemoval(current_branch_id, catalog_number);

        System.out.println("\n-----------------------------------------");
        if (reason == 1) {
            System.out.println("The item \"" + product_name + "\" has been marked as purchased and removed from Branch " + current_branch_id + ".");
            System.out.printf("The item was sold for: %.2f ₪ (after store discount)\n", sale_price);
        } else {
            System.out.println("The item \"" + product_name + "\" has been marked as defective and removed from Branch " + current_branch_id + ".");
        }

        if (alert) {
            System.out.println("ALERT: The product \"" + product_name + "\" in Branch " + current_branch_id + " has reached a critical amount!");
            System.out.println("Please consider reordering.");
        }
        System.out.println("-----------------------------------------");
    }

    /**
     * Marks an item as defective.
     */
    public void markAsDefect() {
        System.out.print("Enter item ID: ");
        int itemId;

        try {
            itemId = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Item ID must be a numeric value.");
            return;
        }

        // Check if the item exists in the current branch
        if (!inventory_controller.getItemController().itemExistsInBranch(itemId, current_branch_id)) {
            System.out.println("Item with ID " + itemId + " was not found in Branch " + current_branch_id + ".");
            return;
        }

        // Proceed to mark as defective
        boolean success = inventory_controller.getItemController().markItemAsDefective(itemId, current_branch_id);

        if (success) {
            System.out.println("Item with ID " + itemId + " in Branch " + current_branch_id + " has been marked as defective.");
        } else {
            System.out.println("Failed to mark item as defective. Please try again.");
        }
    }

    /**
     * Updates the storage location of an item.
     */
    public void updateItemStorageLocation() {
        // First, display all available item IDs in the current branch
        try {
            List<ItemDTO> items = inventory_controller.getItemController().getAllItemsByBranchId(current_branch_id);
            
            if (items.isEmpty()) {
                System.out.println("No items found in Branch " + current_branch_id + ".");
                return;
            }
            
            printSectionHeader("Available Items in Branch " + current_branch_id);
            System.out.printf("%-8s %-30s %-15s %-10s%n", "Item ID", "Product Name", "Location", "Section");
            System.out.println("------------------------------------------------------------------------");
            
            for (ItemDTO item : items) {
                String productName = inventory_controller.getItemController().getItemName(item.getItemId(), current_branch_id);
                if (productName.isEmpty()) {
                    productName = "Unknown Product";
                }
                // Truncate product name if too long
                if (productName.length() > 28) {
                    productName = productName.substring(0, 25) + "...";
                }
                
                String location = item.getStorageLocation() != null ? item.getStorageLocation() : "Not Set";
                String section = item.getSectionInStore() != null ? item.getSectionInStore() : "Not Set";
                
                System.out.printf("%-8d %-30s %-15s %-10s%n", 
                    item.getItemId(), productName, location, section);
            }
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Total items: " + items.size());
            System.out.println();
            
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving items: " + e.getMessage());
            return;
        }
        
        // Prompt the user to enter the item ID
        System.out.println("Enter item ID:");
        int id = Integer.parseInt(scan.nextLine());

        // Ask the user what they want to change: location, section, or both
        System.out.println("What would you like to change?\n(1) Location\n(2) Section\n(3) Both");
        int choice = Integer.parseInt(scan.nextLine());

        // Initialize variables to hold new location and/or section values
        String location = null, section = null;

        // If the user wants to change the location (or both location and section)
        if (choice == 1 || choice == 3) {
            System.out.println("Enter new location (Warehouse or InteriorStore):");
            location = scan.nextLine();
        }

        // If the user wants to change the section (or both location and section)
        if (choice == 2 || choice == 3) {
            System.out.println("Enter new section (e.g., A1, B2, etc.):");
            section = scan.nextLine();
        }

        // Attempt to update the item's location and/or section in the selected branch
        boolean updated = inventory_controller.getItemController().updateItemLocation(id, current_branch_id, location, section);

        // Display the result of the update
        if (updated) {
            System.out.println("\n-----------------------------------------");
            System.out.println("Item with ID " + id + " updated successfully in Branch " + current_branch_id + ".");
            if (location != null) {
                System.out.println("New location: " + location);
            }
            if (section != null) {
                System.out.println("New section: " + section);
            }
            System.out.println("-----------------------------------------\n");
        } else {
            // Item not found in the current branch
            System.out.println("Item with ID " + id + " was not found in Branch " + current_branch_id + ".");
        }
    }
}
