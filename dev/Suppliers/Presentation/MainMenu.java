package Suppliers.Presentation;

import Suppliers.Init.SuppliersInitializer;

import java.sql.SQLException;
import java.util.Scanner;

public class MainMenu {

    public static void run(Scanner scanner, boolean withSampleData) throws SQLException {
        OrderMenuHandler orderMenuHandler = new OrderMenuHandler();
        SupplierMenuHandler supplierMenuHandler = new SupplierMenuHandler();
        
        SuppliersInitializer initializer = new SuppliersInitializer();
        SuppliersInitializer.initializeAllTables();

        initializer.initializeDatabase(withSampleData);

        // Welcome message
        System.out.println("=====================================");
        System.out.println("|                                   |");
        System.out.println("|  Welcome to the Supplier Module!  |");
        System.out.println("|                                   |");
        System.out.println("=====================================\n");

        int choice = -1;
        while (choice != 4) {
            System.out.println("========== Suppliers Module Main Menu ==========");
            System.out.println("1. Search supplier");
            System.out.println("2. Create new supplier");
            System.out.println("3. Search for a past order");
            System.out.println("4. Back to Inventory-Suppliers Main Menu");
            System.out.print("Enter your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid choice. Please try again.\n");
                scanner.next(); // consume bad input
                continue;
            }

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> supplierMenuHandler.searchSupplierMenu(scanner);
                case 2 -> {
                    int supplier_ID = SupplierMenuHandler.createSupplier(scanner);
                    SupplierMenuHandler.afterSupplierCreatedMenu(scanner, supplier_ID);
                }
                case 3 -> orderMenuHandler.printPastOrder();
                case 4 -> System.out.println("Returning to the main Inventory-Supplier menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }
}
