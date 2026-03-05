package Inventory.Domain;


import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;
import Inventory.InventoryUtils.DateUtils;
import Inventory.Repository.IItemRepository;
import Inventory.Repository.IProductRepository;
import Inventory.Repository.IPeriodicOrderRepository;
import Inventory.Repository.IShortageOrderRepository;
import Inventory.Repository.ItemRepositoryImpl;
import Inventory.Repository.ProductRepositoryImpl;
import Inventory.Repository.PeriodicOrderRepositoryImpl;
import Inventory.Repository.ShortageOrderRepositoryImpl;

import java.util.List;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Manages inventory operations: items, products, discounts, branches, and reports.
 * Supports data import from CSV and updates product stock levels across branches.
 */

public class InventoryController {
    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Branch> branches;
    private final HashMap<String, HashMap<String, HashMap<String, Integer>>> products_amount_map_by_category;
    private final ItemController item_controller;
    private final ProductController product_controller;
    private final DiscountController discount_controller;    private final ReportController report_controller;
    private final IProductRepository productRepository;
    private final IPeriodicOrderRepository periodicOrderRepository;
    private final IShortageOrderRepository shortageOrderRepository;


    public InventoryController() {
        this.products = new HashMap<>();
        this.branches = new HashMap<>();        this.products_amount_map_by_category = new HashMap<>();
        this.productRepository = new ProductRepositoryImpl();
        this.periodicOrderRepository = new PeriodicOrderRepositoryImpl();
        this.shortageOrderRepository = new ShortageOrderRepositoryImpl();// Initialize default 10 branches
        for (int i = 1; i <= 10; i++) {
            branches.putIfAbsent(i, new Branch(i));
        }        HashMap<Integer, ItemDTO> purchased_items = new HashMap<>();
        this.item_controller = new ItemController(branches, products, purchased_items);
        this.product_controller = new ProductController(products);
        this.discount_controller = new DiscountController(products);
        this.report_controller = new ReportController(branches, products);
    }


    /**
     * Returns the map of all existing branches in the system.
     * <p>
     * Each branch is identified by its unique branch ID.
     * Used to access branch-specific inventories and operations.
     *
     * @return a map of branch IDs to {@code Branch} objects.
     */
    public HashMap<Integer, Branch> getBranches() {
        return branches;
    }


    public void updateProductInventoryCount(boolean add, int branchId, String category, String subCategory, String size, String location) {
        // Ensure nested structure exists
        products_amount_map_by_category.putIfAbsent(category, new HashMap<>());
        HashMap<String, HashMap<String, Integer>> subCategoryMap = products_amount_map_by_category.get(category);

        subCategoryMap.putIfAbsent(subCategory, new HashMap<>());
        HashMap<String, Integer> sizeMap = subCategoryMap.get(subCategory);

        sizeMap.putIfAbsent(size, 0);
        sizeMap.put(size, sizeMap.get(size) + (add ? 1 : -1));

        // Update quantities in Product object using DB repository
        IItemRepository itemRepo = new ItemRepositoryImpl();
        List<ItemDTO> items = itemRepo.getAllItems().stream()
                .filter(i -> i.getBranchId() == branchId)
                .collect(Collectors.toList());

        for (ItemDTO item : items) {
            Product product = products.get(item.getCatalogNumber());
            if (product != null
                    && product.getCategory().equalsIgnoreCase(category)
                    && product.getSubCategory().equalsIgnoreCase(subCategory)
                    && String.valueOf(product.getSize()).equals(size)) {

                if (location.equalsIgnoreCase("Warehouse")) {
                    product.setQuantityInWarehouse(
                            Math.max(0, product.getQuantityInWarehouse() + (add ? 1 : -1))
                    );
                } else if (location.equalsIgnoreCase("InteriorStore")) {
                    product.setQuantityInStore(
                            Math.max(0, product.getQuantityInStore() + (add ? 1 : -1))
                    );
                }
            }
        }
    }


    public void loadFromDatabase() {
        System.out.println("🔄 Loading data from database using Repositories and DTOs...");

        try {
            // --- Load products ---
            List<ProductDTO> productDTOs = productRepository.getAllProducts();
            System.out.println("✅ Retrieved " + productDTOs.size() + " products from database.");

            for (ProductDTO dto : productDTOs) {
                Product product = new Product();

                product.setCatalogNumber(dto.getCatalogNumber());
                product.setProductName(dto.getProductName());
                product.setCategory(dto.getCategory());
                product.setSubCategory(dto.getSubCategory());
                product.setProductDemandLevel(dto.getProductDemandLevel());
                product.setSupplyDaysInTheWeek(dto.getSupplyDaysInWeek());
                product.setSupplierName(dto.getSupplierName());
                product.setSize(dto.getSize());
                product.setCostPriceBeforeSupplierDiscount(dto.getCostPriceBeforeSupplierDiscount());
                product.setSupplierDiscount(dto.getSupplierDiscount());
                product.setCostPriceAfterSupplierDiscount(dto.getCostPriceAfterSupplierDiscount());
                product.setStoreDiscount(dto.getStoreDiscount());
                product.setSalePriceBeforeStoreDiscount(dto.getSalePriceBeforeStoreDiscount());
                product.setSalePriceAfterStoreDiscount(dto.getSalePriceAfterStoreDiscount());
                product.setQuantityInWarehouse(dto.getQuantityInWarehouse());
                product.setQuantityInStore(dto.getQuantityInStore());
                product.setMinimumQuantityForAlert(dto.getMinimumQuantityForAlert());

                product_controller.addProduct(product); // Also adds to products map
            }

            // --- Load items ---
            List<ItemDTO> itemDTOs = item_controller.getItemRepository().getAllItems();
            System.out.println("✅ Retrieved " + itemDTOs.size() + " items from database.");

            for (ItemDTO dto : itemDTOs) {
                ItemDTO item = new ItemDTO();

                item.setItemId(dto.getItemId());
                item.setBranchId(dto.getBranchId());
                item.setExpirationDate(dto.getItemExpiringDate());
                item.setLocation(dto.getStorageLocation());
                item.setCatalogNumber(dto.getCatalogNumber());
                item.setIsDefective(dto.IsDefective());

                int branchId = item.getBranchId();
                int catalogNumber = item.getCatalogNumber();

                branches.putIfAbsent(branchId, new Branch(branchId));
                branches.get(branchId).addItem(item);

                if (!products.containsKey(catalogNumber)) {
                    System.err.println("⚠️ Warning: Item references missing Product (Catalog #" + catalogNumber + ")");
                    continue;
                }

                Product product = products.get(catalogNumber);
                updateProductInventoryCount(
                        true,
                        branchId,
                        product.getCategory(),
                        product.getSubCategory(),
                        String.valueOf(product.getSize()),
                        item.getStorageLocation()
                );
            }

            System.out.println("✅ Data loaded successfully using Repositories.");

        } catch (SQLException e) {
            System.err.println("❌ Failed to load data from database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Gets the item controller instance.
     * @return The item controller.
     */
    public ItemController getItemController() {
        return item_controller;
    }

    /**
     * Gets the product controller instance.
     * @return The product controller.
     */
    public ProductController getProductController() {
        return product_controller;
    }

    /**
     * Gets the discount controller instance.
     * @return The discount controller.
     */
    public DiscountController getDiscountController() {
        return discount_controller;
    }

    /**
     * Gets the report controller instance.
     * @return The report controller.
     */
    public ReportController getReportController() {
        return report_controller;
    }

    /**
     * Gets the product repository instance.
     * @return The product repository.
     */
    public IProductRepository getProductRepository() {
        return productRepository;    }

    /**
     * Gets the periodic order repository instance.
     * @return The periodic order repository.
     */
    public IPeriodicOrderRepository getPeriodicOrderRepository() {
        return periodicOrderRepository;
    }

    /**
     * Gets the shortage order repository instance.
     * @return The shortage order repository.
     */
    public IShortageOrderRepository getShortageOrderRepository() {
        return shortageOrderRepository;
    }

    public void addNewProductAndItems(int catalogNumber, int branchId, Scanner scan) {
        System.out.println("Product with Catalog Number " + catalogNumber + " does not exist.");
        System.out.println("Please enter full product details.");

        // Collect new product details from the user
        System.out.print("Enter Product Name: ");
        String productName = scan.nextLine().trim();

        System.out.print("Enter Category: ");
        String category = scan.nextLine().trim();

        System.out.print("Enter Sub-Category: ");
        String subCategory = scan.nextLine().trim();

        System.out.print("Enter Product Size (numeric value between 1-3): ");
        int size = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Cost Price Before Supplier Discount: ");
        double costPriceBefore = Double.parseDouble(scan.nextLine().trim());

        System.out.print("Enter Product Demand Level (1–5): ");
        int demandLevel = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Supply Days In The Week (e.g., Sunday,Wednesday): ");
        String supplyDays = scan.nextLine().trim();

        System.out.print("Enter Supplier Name: ");
        String supplierName = scan.nextLine().trim();

        System.out.print("Enter Supplier Discount (%): ");
        int supplierDiscount = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Store Discount (%): ");
        int storeDiscount = Integer.parseInt(scan.nextLine().trim());

        // Create and populate new Product object
        Product newProduct = new Product();
        populateProductData(newProduct, catalogNumber, productName, category, subCategory,
                demandLevel, supplyDays, supplierName,
                costPriceBefore, supplierDiscount, storeDiscount, size);

        // Add the new product to the system
        product_controller.addProduct(newProduct);

        System.out.println("\nProduct added successfully!");

        // Collect item details
        System.out.print("How many items would you like to add for this product? ");
        int quantityToAdd = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter storage location for all items (Warehouse or InteriorStore): ");
        String storageLocation = scan.nextLine().trim();

        System.out.print("Enter expiry date for all items (format: dd/MM/yyyy): ");
        String expiryDate = scan.nextLine().trim();

        int nextItemId = item_controller.getNextAvailableItemId();

        for (int i = 0; i < quantityToAdd; i++) {
            int currentItemId = nextItemId + i;
            item_controller.addItem(
                    currentItemId,
                    branchId,
                    catalogNumber,
                    storageLocation,
                    expiryDate
            );
        }

        System.out.println("\n-----------------------------------------");
        System.out.println(quantityToAdd + " items successfully added for Product Catalog Number " + catalogNumber + ".");
        System.out.println("-----------------------------------------\n");
    }



    private void populateProductData(Product product, int catalogNumber, String productName, String category, String subCategory,
                                     int demandLevel, String supplyDaysInWeek, String supplierName,
                                     double costPriceBefore, int supplierDiscount, int storeDiscount, int size) {

        product.setCatalogNumber(catalogNumber);
        product.setProductName(productName);
        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setProductDemandLevel(demandLevel);
        product.setSupplyDaysInTheWeek(supplyDaysInWeek);
        product.setSupplierName(supplierName);
        product.setSize(size);

        double costAfterSupplierDiscount = costPriceBefore * (1 - supplierDiscount / 100.0);
        double salePriceBeforeStoreDiscount = costAfterSupplierDiscount * 2;
        double salePriceAfterStoreDiscount = salePriceBeforeStoreDiscount * (1 - storeDiscount / 100.0);

        product.setSupplierDiscount(supplierDiscount);
        product.setCostPriceBeforeSupplierDiscount(costPriceBefore);
        product.setCostPriceAfterSupplierDiscount(costAfterSupplierDiscount);
        product.setStoreDiscount(storeDiscount);
        product.setSalePriceBeforeStoreDiscount(salePriceBeforeStoreDiscount);
        product.setSalePriceAfterStoreDiscount(salePriceAfterStoreDiscount);
        product.setQuantityInWarehouse(0);
        product.setQuantityInStore(0);

        int calculatedSupplyTime = DateUtils.calculateNextSupplyDayOffset(supplyDaysInWeek);
        int minRequired = (int) (0.5 * demandLevel + 0.5 * calculatedSupplyTime);
        product.setMinimumQuantityForAlert(minRequired);
    }}