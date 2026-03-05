package Inventory.Presentation;

import Inventory.Domain.Discount;
import Inventory.Domain.InventoryController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Controller for discount-related UI operations.
 * Handles all interactions related to applying discounts to products.
 */
public class DiscountUIController extends BaseUIController {

    /**
     * Constructor for DiscountUIController.
     *
     * @param inventory_controller The inventory controller instance
     * @param currentBranchId The current branch ID
     */
    public DiscountUIController(InventoryController inventory_controller, int currentBranchId) {
        super(inventory_controller, currentBranchId);
    }

    /**
     * Applies discount to a group of products.
     */
    public void applyDiscount() {
        System.out.println("Apply Discount");
        System.out.println("Note: The discount you apply will affect ALL branches across the network.");
        System.out.println("Choose group to apply discount on:");
        System.out.println("(1) Category\n(2) Sub-Category\n(3) Product Size\n(4) Product Catalog Number");

        int type;
        try {
            type = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid group choice. Returning to menu.");
            return;
        }

        String category = null, sub_category = null;
        int catalog = -1;
        int size = -1;

        if (type == 1) {
            System.out.print("Enter category: ");
            category = scan.nextLine().trim();
            if (!inventory_controller.getProductController().hasCategory(category)) {
                System.out.println("This category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 2) {
            System.out.print("Enter sub-category: ");
            sub_category = scan.nextLine().trim();
            if (!inventory_controller.getProductController().hasSubCategory(sub_category)) {
                System.out.println("This sub-category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 3) {
            System.out.print("Enter product size (integer): ");
            try {
                size = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid product size. Returning to menu.");
                return;
            }
            
            // Convert size to final for use in lambda
            final int finalSize = size;
            boolean exists = inventory_controller.getProductController()
                    .getAllProducts()
                    .stream()
                    .anyMatch(p -> p.getSize() == finalSize);

            if (!exists) {
                System.out.println("No products found with size " + size + ". Returning to menu.");
                return;
            }

        } else if (type == 4) {
            System.out.print("Enter Product Catalog Number: ");
            try {
                catalog = Integer.parseInt(scan.nextLine());
                if (!inventory_controller.getProductController().productExists(catalog)) {
                    System.out.println("This product catalog number does not exist. Returning to menu.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Product Catalog Number. Returning to menu.");
                return;
            }
        } else {
            System.out.println("Invalid group choice. Returning to menu.");
            return;
        }

        System.out.println("Choose discount type:\n(1) Supplier Discount\n(2) Store Discount");
        int discount_type_input;
        try {
            discount_type_input = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid discount type. Returning to menu.");
            return;
        }

        if (discount_type_input != 1 && discount_type_input != 2) {
            System.out.println("Invalid discount type. Returning to menu.");
            return;
        }

        boolean is_supplier = discount_type_input == 1;

        double rate = -1;
        while (rate < 0 || rate > 100) {
            System.out.print("Enter discount rate (%): ");
            try {
                rate = Double.parseDouble(scan.nextLine());
                if (rate < 0 || rate > 100) {
                    System.out.println("Discount must be between 0 and 100. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
        LocalDate start = LocalDate.now();
        LocalDate end;

        while (true) {
            try {
                System.out.print("Enter end date (format: day.month.year, e.g., 30.6.2026): ");
                String endInput = scan.nextLine().replace("-", ".").replace("/", ".");
                end = LocalDate.parse(endInput, formatter);
                if (end.isBefore(start)) {
                    System.out.println("End date must be after today's date (" + start + "). Please try again.");
                } else {
                    break;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in the format: day.month.year (e.g., 30.5.2025)");
            }
        }

        Discount discount = new Discount(rate, start, end);

        boolean success;
        if (type == 1) {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForCategory(category, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForCategory(category, discount);
        } else if (type == 2) {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForSubCategory(sub_category, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForSubCategory(sub_category, discount);
        } else if (type == 3) {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForSize(size, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForSize(size, discount);
        } else {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForCatalogNumber(catalog, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForCatalogNumber(catalog, discount);
        }

        System.out.println("\n-----------------------------------------");
        if (success) {
            String target = switch (type) {
                case 1 -> category;
                case 2 -> sub_category;
                case 3 -> "Size " + size;
                default -> "Catalog #" + catalog;
            };
            String discountType = is_supplier ? "Supplier" : "Store";
            System.out.println(discountType + " discount of " + rate + "% was successfully applied to: " + target);
            System.out.println("Active from " + start + " to " + end);
            System.out.println("Note: This discount has been applied to ALL matching products in the current branch.");
        } else {
            System.out.println("Failed to apply discount. Check if the group exists or if the discount is valid.");
        }
        System.out.println("-----------------------------------------");
    }
}
