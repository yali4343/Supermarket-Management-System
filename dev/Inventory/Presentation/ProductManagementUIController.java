package Inventory.Presentation;

import Inventory.Domain.InventoryController;
import Inventory.Domain.Product;
import java.sql.SQLException;

/**
 * Controller for product-related UI operations.
 * Handles all interactions related to products in inventory.
 */
public class ProductManagementUIController extends BaseUIController {

    /**
     * Constructor for ProductManagementUIController.
     *
     * @param inventory_controller The inventory controller instance
     * @param currentBranchId The current branch ID
     */
    public ProductManagementUIController(InventoryController inventory_controller, int currentBranchId) {
        super(inventory_controller, currentBranchId);
    }

    /**
     * Shows the purchase prices of a product.
     */
    public void showPurchasePrices() {
        System.out.print("Enter Product Catalog Number: ");

        int catalog;
        try {
            // Read the catalog number input
            catalog = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            // Handle invalid input
            System.out.println("Invalid input. Please enter a valid numeric Product Catalog Number.");
            return;
        }

        // Retrieve the purchase prices details
        String result = inventory_controller.getProductController().showProductPurchasesPrices(catalog, current_branch_id);

        // Check if the product does not exist or there are no purchases
        if (result != null && !result.trim().isEmpty()) {
            if (result.contains("No purchased items found")) {
                System.out.println("No purchases found for Product Catalog Number " + catalog + " in Branch " + current_branch_id + ".");
            } else if (result.contains("does not exist") || result.contains("Invalid Product Catalog Number")) {
                System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
            } else {
                // Valid purchase details found — display them
                printSectionHeader("Purchase Prices");
                System.out.println(result);
            }
        } else {
            System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
        }
    }

    /**
     * Shows the quantities of a product in different locations.
     */
    public void showProductQuantities() {
        System.out.print("Enter Product Catalog Number: ");

        int catalog;
        try {
            catalog = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numeric Product Catalog Number.");
            return;
        }

        // ✅ Update quantities in the database before displaying
        try {
            inventory_controller.getProductController().updateAllProductQuantities();
        } catch (Exception e) {
            System.err.println("❌ Failed to update product quantities in DB: " + e.getMessage());
            return;
        }

        // ⬇ Continue as usual
        String result = inventory_controller.getProductController().showProductQuantities(catalog, current_branch_id);

        if (result != null && !result.trim().isEmpty()) {
            if (result.contains("does not exist") || result.contains("No items found") || result.contains("Invalid Product Catalog Number")) {
                System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
            } else {
                System.out.println("\n-----------------------------");
                System.out.println("Product Quantities for Branch " + current_branch_id + ":");
                System.out.println(result);
                System.out.println("-----------------------------\n");
            }
        } else {
            System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
        }
    }

    /**
     * Updates the demand level of a product.
     */
    public void updateProductDemandLevel() {
        try {
            System.out.println("Enter Product Catalog Number:");
            int catalog = Integer.parseInt(scan.nextLine());

            System.out.println("Enter new demand level (1–5):");
            int demand = Integer.parseInt(scan.nextLine());
            
            if (demand < 1 || demand > 5) {
                System.out.println("Invalid demand level. Please enter a value between 1 and 5.");
                return;
            }

            // Attempt to update the product demand level only (supply days set to null)
            boolean updated = inventory_controller.getProductController().updateProductSupplyDetails(catalog, null, demand);

            if (updated) {
                System.out.println("\n-----------------------------------------");
                System.out.println("The demand level for the product with Catalog Number " + catalog + " has been updated across all branches.");
                System.out.println("New demand level: " + demand);
                System.out.println("-----------------------------------------\n");
            } else {
                System.out.println("Product with Catalog Number " + catalog + " not found in inventory.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error while updating product demand level: " + e.getMessage());
            System.out.println("Please try again or contact support.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric values where expected.");
        }
    }
}
