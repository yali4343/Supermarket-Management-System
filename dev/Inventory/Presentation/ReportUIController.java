package Inventory.Presentation;

import Inventory.Domain.InventoryController;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for report-related UI operations.
 * Handles all interactions related to generating various inventory reports.
 */
public class ReportUIController extends BaseUIController {

    /**
     * Constructor for ReportUIController.
     *
     * @param inventory_controller The inventory controller instance
     * @param currentBranchId The current branch ID
     */
    public ReportUIController(InventoryController inventory_controller, int currentBranchId) {
        super(inventory_controller, currentBranchId);
    }

    /**
     * Generates inventory report based on user choices.
     */
    public void generateInventoryReport() {
        System.out.println("\n===================================================");
        System.out.println("Inventory Report Options (Branch " + current_branch_id + ")");
        System.out.println("===================================================");

        System.out.println("Choose report type:");
        System.out.println("1. By Categories");
        System.out.println("2. By Sub-Categories");
        System.out.println("3. By Catalog Number");
        System.out.print("Enter your choice (1-3): ");

        int reportChoice;
        try {
            reportChoice = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to menu.");
            return;
        }

        System.out.println("\nFilter by Size:");
        System.out.println("1. Small");
        System.out.println("2. Medium");
        System.out.println("3. Big");
        System.out.println("4. All Sizes");
        System.out.print("Enter your size filter choice (1-4): ");

        int sizeChoice;
        try {
            sizeChoice = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to menu.");
            return;
        }

        List<Integer> sizeFilters = getSizeFilters(sizeChoice);
        if (sizeFilters.isEmpty()) {
            System.out.println("Invalid size selection. Returning to menu.");
            return;
        }

        switch (reportChoice) {
            case 1 -> generateReportByCategories(sizeFilters);
            case 2 -> generateReportBySubCategories(sizeFilters);
            case 3 -> generateReportByCatalogNumber(sizeFilters);
            default -> System.out.println("Invalid report choice. Returning to menu.");
        }
    }

    /**
     * Generates report filtered by categories.
     */
    private void generateReportByCategories(List<Integer> sizeFilters) {
        printSectionHeader("Inventory Report by Categories");

        System.out.print("Enter categories separated by commas: ");
        String[] categories = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);

        String report = inventory_controller.getReportController()
                .inventoryReportByCategories(categories, current_branch_id, sizeFilters);

        if (report != null && !report.trim().isEmpty() && !report.contains("does not exist") && !report.contains("No valid categories")) {
            printSectionHeader("Report Output");
            System.out.println(report);
        } else {
            System.out.println("No matching categories found in Branch " + current_branch_id + ".");
        }
    }

    /**
     * Generates report filtered by sub-categories.
     */
    private void generateReportBySubCategories(List<Integer> sizeFilters) {
        printSectionHeader("Inventory Report by Sub-Categories");

        System.out.print("Enter sub-categories separated by commas: ");
        String[] subCategories = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);

        String report = inventory_controller.getReportController()
                .inventoryReportBySubCategories(subCategories, current_branch_id, sizeFilters);

        if (report != null && !report.trim().isEmpty() && !report.contains("does not exist") && !report.contains("No valid sub-categories")) {
            printSectionHeader("Report Output");
            System.out.println(report);
        } else {
            System.out.println("No matching sub-categories found in Branch " + current_branch_id + ".");
        }
    }

    /**
     * Generates report filtered by catalog numbers.
     */
    private void generateReportByCatalogNumber(List<Integer> sizeFilters) {
        printSectionHeader("Inventory Report by Catalog Numbers");

        System.out.print("Enter catalog numbers separated by commas: ");
        String[] catalogNumbers = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);

        String report = inventory_controller.getReportController()
                .inventoryReportByCatalogNumbers(catalogNumbers, current_branch_id, sizeFilters);

        if (report != null && !report.trim().isEmpty() && !report.contains("does not exist") && !report.contains("No valid catalog numbers")) {
            printSectionHeader("Report Output");
            System.out.println(report);
        } else {
            System.out.println("No matching catalog numbers found in Branch " + current_branch_id + ".");
        }
    }

    /**
     * Converts the user's size choice to a list of size filters.
     */
    private List<Integer> getSizeFilters(int sizeChoice) {
        return switch (sizeChoice) {
            case 1 -> List.of(1); // Small
            case 2 -> List.of(2); // Medium
            case 3 -> List.of(3); // Big
            case 4 -> List.of(1, 2, 3); // All Sizes
            default -> List.of();
        };
    }

    /**
     * Generates a report of defective and expired items.
     */
    public void generateDefectAndExpiredReport() {
        // Retrieve the defect and expired report
        String report = inventory_controller.getReportController().defectAndExpiredReport(current_branch_id);

        // Check if the report contains meaningful data
        if (report != null && !report.trim().isEmpty()
                && !(report.contains("No defective items") && report.contains("No expired items"))) {
            // Valid defect/expired report found — display it
            printSectionHeader("Defect and Expired Items Report");
            System.out.println(report);
        } else {
            // No defective or expired items — display a friendly message
            System.out.println("No defective or expired items found in Branch " + current_branch_id + ".");
        }
    }

    /**
     * Generates a shortage inventory report.
     */
    public void generateShortageInventoryReport() {
        // Retrieve the reorder alert report
        String report = inventory_controller.getReportController().generateShortageInventoryReport(current_branch_id);

        // Check if the report contains a real shortage alert
        String noShortageMessage = "All the products in Branch " + current_branch_id + " are above their minimum required amount.";
        String branchNotFoundMessage = "Branch " + current_branch_id + " not found.";

        if (report != null && !report.trim().isEmpty()
                && !report.equals(noShortageMessage)
                && !report.equals(branchNotFoundMessage)) {

            // Valid reorder report found — display it
            printSectionHeader("Reorder Alert Report");
            System.out.println(report);
        } else {
            // No products require reordering — display a friendly message
            System.out.println("No products currently require reordering in Branch " + current_branch_id + ".");
        }
    }
}
