package Inventory.Presentation;

import Inventory.Domain.InventoryController;
import java.util.Scanner;

/**
 * Base abstract class for all UI controllers.
 * Contains common fields and methods used across all controllers.
 */
public abstract class BaseUIController {
    protected final Scanner scan;
    protected final InventoryController inventory_controller;
    protected final int current_branch_id;

    /**
     * Constructor for the BaseUIController.
     *
     * @param inventory_controller The inventory controller instance
     * @param currentBranchId The current branch ID
     */
    public BaseUIController(InventoryController inventory_controller, int currentBranchId) {
        this.scan = new Scanner(System.in);
        this.inventory_controller = inventory_controller;
        this.current_branch_id = currentBranchId;
    }
    
    /**
     * Prints a section header with the given title
     * 
     * @param title The title of the section
     */
    protected void printSectionHeader(String title) {
        System.out.println("\n----------- " + title + " -----------");
    }
    
    /**
     * Prints a separation line
     */
    protected void printSeparator() {
        System.out.println("----------------------------------------");
    }
}
