package Suppliers.Presentation;

import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.Repository.IProductSupplierRepository;
import Suppliers.Domain.ProductSupplierManagementController;
import Suppliers.Repository.ProductSupplierRepositoryImpl;
import Suppliers.DAO.IDiscountDAO;
import Suppliers.DAO.IProductSupplierDAO;
import Suppliers.DAO.JdbcDiscountDAO;
import Suppliers.DAO.JdbcProductSupplierDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProductMenuHandler {
    public static ProductSupplierManagementController productSupplierManagementController;

    // Static initializer to ensure the controller is initialized before any static method is called
    static {
        initializeController();
    }

    public ProductMenuHandler() {
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();
        // יצירת Repository
        IProductSupplierRepository productSupplierRepository = new ProductSupplierRepositoryImpl(productSupplierDAO,discountDAO);
        // יצירת קונטרולרים
        productSupplierManagementController = new ProductSupplierManagementController(productSupplierRepository);
    }

    private static void initializeController() {
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();
        // יצירת Repository
        IProductSupplierRepository productSupplierRepository = new ProductSupplierRepositoryImpl(productSupplierDAO,discountDAO);
        // יצירת קונטרולרים
        productSupplierManagementController = new ProductSupplierManagementController(productSupplierRepository);
    }

    public static void addNewProduct(Scanner scanner, int supplierID, int agreementID) throws SQLException {
        System.out.println("\nLet's add a new product...");
        int catalog_Number = Inputs.read_int(scanner, "Enter Catalog Number: ");
//      int catalog_Number = scanner.nextInt();
//----------Check for unique catalog number

//        int catalog_Number;
//        while (true) {
//            catalog_Number = Inputs.read_int(scanner, "Enter Catalog Number: ");
//            boolean isCatalogUnique = validateUniqueCatalogNumber(controller, catalog_Number, supplierID);
//            if (isCatalogUnique) {
//                break; // Catalog number is unique -> can exit loop
//            }
//            // אחרת נחזור לראש הלולאה ונבקש שוב
//        }
//----------------בדיקת מספר מזהה ייחודי--------------------
        int  product_id = Inputs.read_int(scanner, "Enter Product ID: ");
//        int product_id = scanner.nextInt();
//        int product_id;
//        while (true) {
//            product_id = Inputs.read_int(scanner, "Enter Product ID: ");
//            boolean isProductIDUnique = validateUniqueProductIDNumber(controller, product_id, supplierID);
//            if (isProductIDUnique) {
//                break; // Only exit loop if ID is unique
//            }
//        }

            double price = Inputs.read_double(scanner, "Enter Product Price: ");

            System.out.println("Enter Unit of Measure: ");
            String unitsOfMeasure = scanner.next();


        ProductSupplierDTO productSupplierDTO = new ProductSupplierDTO(catalog_Number,product_id,supplierID, agreementID, price, unitsOfMeasure);
            productSupplierManagementController.createProductSupplier(productSupplierDTO);
//            Product product = controller.createProduct(catalog_Number, product_id, price, unitsOfMeasure, supplierID);
//            controller.addProductToAgreement(product_id, product, agreementID);

            int choice = -1;
            while (choice != 2) {
                System.out.println("Do you want to add new discount rule?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                System.out.print("Enter your choice: ");

                choice = Inputs.read_input_for_choice(scanner);

                switch (choice) {
                    case 1:
                        System.out.println("Use case: Add discount rule to product");
                        readAndAddDiscountRules(scanner, productSupplierDTO.getCatalog_Number(), product_id,  supplierID,  agreementID);
                        System.out.println("\nProduct added successfully.");
                        return;
                    case 2:
                        System.out.println("No discount rules will be added.");
                        System.out.println("\nProduct added successfully.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

    }



    public static void readAndAddDiscountRules(Scanner scanner, int catalogNumber, int productID, int supplierID, int agreementID) throws SQLException {

            int numOfRules = Inputs.read_int(scanner, "Enter number of discount rules: ");

            for (int i = 0; i < numOfRules; i++) {
                System.out.println("Discount Rule #" + (i + 1));
                int amount = Inputs.read_int(scanner, "Enter minimum amount for discount: ");
                int discount = Inputs.read_int(scanner, "Enter discount percentage (e.g., 10 for 10%): ");

                DiscountDTO dto = new DiscountDTO(productID, supplierID, agreementID, amount, discount);
                productSupplierManagementController.addOrUpdateDiscount(dto);
            }

    }



    public static void removeProduct(Scanner scanner, int agreementID, int supplierID) throws SQLException {
        List<ProductSupplierDTO> productList = productSupplierManagementController.getProductSuppliers(supplierID, agreementID);

        if (productList.isEmpty()) {
            System.out.println(" No products found in this agreement.");
            return;
        }

        System.out.println(" Products in the agreement:");
        for (int i = 0; i < productList.size(); i++) {
            ProductSupplierDTO dto = productList.get(i);
            System.out.println((i + 1) + ") Catalog #" + dto.getCatalog_Number() +
                    " | Product ID: " + dto.getProduct_id() +
                    " | Unit: " + dto.getUnitsOfMeasure() +
                    " | Price: " + dto.getPrice());
        }

        int choice = Inputs.read_int(scanner, "Enter the number of the product to remove (1-" + productList.size() + "): ");

        if (choice < 1 || choice > productList.size()) {
            System.out.println(" Invalid choice. Operation cancelled.");
            return;
        }

        ProductSupplierDTO selectedProduct = productList.get(choice - 1);
        int catalogNumber = selectedProduct.getCatalog_Number();
        int productID = selectedProduct.getProduct_id();

        productSupplierManagementController.deleteProductFromAgreement(productID, catalogNumber, supplierID, agreementID);
        System.out.println(" Product removed successfully.\n");
    }

    public static ProductSupplierDTO showProduct(Scanner scanner,int supplierID, int agreementID) throws SQLException {
        List<ProductSupplierDTO> productList = productSupplierManagementController.getProductSuppliers(supplierID, agreementID);

        if (productList.isEmpty()) {
            System.out.println(" No products found in this agreement.");
            return null;
        }

        System.out.println(" Products in the agreement:");
        for (int i = 0; i < productList.size(); i++) {
            ProductSupplierDTO dto = productList.get(i);
            System.out.println((i + 1) + ") Catalog #" + dto.getCatalog_Number() +
                    " | Product ID: " + dto.getProduct_id() +
                    " | Unit: " + dto.getUnitsOfMeasure() +
                    " | Price: " + dto.getPrice());
        }

        int choice = Inputs.read_int(scanner, "Enter the number of the product (1-" + productList.size() + "): ");

        if (choice < 1 || choice > productList.size()) {
            System.out.println(" Invalid choice. Operation cancelled.");
            return null;
        }

        ProductSupplierDTO selectedProduct = productList.get(choice - 1);
        return selectedProduct;
    }


    public static void editProductTerms(Scanner scanner, int supplierID, Integer agreementID) throws SQLException {
        System.out.println("\nEdit Product Supply Terms:");

        ProductSupplierDTO productSupplierDTO = showProduct(scanner,supplierID,agreementID);

        if (productSupplierDTO == null) return;

        int choice = -1;
        while (choice != 0) {
            System.out.println("\nChoose what you want to update:");
            System.out.println("1. Update Product Price");
            System.out.println("2. Update Unit of Measure");
            System.out.println("3. Add or Update Discount Rule");
            System.out.println("0. Return to previous menu");
            System.out.print("Enter your choice: ");

            choice = Inputs.read_input_for_choice(scanner);

            switch (choice) {
                case 1:
                    updateProductPrice(scanner, productSupplierDTO.getProduct_id(),supplierID, productSupplierDTO.getCatalog_Number(),agreementID);
                    break;
                case 2:
                    updateProductUnit(scanner, productSupplierDTO.getProduct_id(), productSupplierDTO.getCatalog_Number(), agreementID);
                    break;
                case 3:
                    readAndAddDiscountRules( scanner, productSupplierDTO.getCatalog_Number(),productSupplierDTO.getProduct_id(),  supplierID, agreementID);
                    break;
//                case 4:
//                    updateDiscountRules(scanner, controller, catalogNumber, agreementID);
//                    break;
                case 0:
                    System.out.println("Returning to previous menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void updateProductPrice(Scanner scanner, int productID, int supplierID, int catalogNumber, Integer agreementID) throws SQLException {
        double newPrice = Inputs.read_double(scanner, "Enter new price: ");
        productSupplierManagementController.setProductPrice(productID, catalogNumber, supplierID, newPrice);
        System.out.println("Product price updated.");
    }

    public static void updateProductUnit(Scanner scanner, int productID, int catalogNumber, Integer agreementID) throws SQLException {
        System.out.print("Enter new unit of measure: ");
        String newUnit = scanner.next();
        productSupplierManagementController.updateUnit(productID, catalogNumber, agreementID,newUnit);
        System.out.println("Product unit updated.");
    }



}
