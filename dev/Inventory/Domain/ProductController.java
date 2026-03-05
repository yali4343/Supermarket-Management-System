package Inventory.Domain;

import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;
import Inventory.DTO.SoldItemDTO;
import Inventory.Repository.IProductRepository;
import Inventory.Repository.ItemRepositoryImpl;
import Inventory.Repository.IItemRepository;
import Inventory.Repository.ProductRepositoryImpl;
import Inventory.Repository.ISoldItemRepository;
import Inventory.Repository.SoldItemRepositoryImpl;


import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller responsible for managing products in the inventory system.
 * Provides operations for updating product pricing, supply details,
 * and tracking purchased items.
 */
public class ProductController {
    private final IItemRepository itemRepository;
    private final IProductRepository productRepository;
    private final ISoldItemRepository soldItemRepository;
    private final HashMap<Integer, Product> products; // Map of all products, keyed by catalog number
    private final HashMap<Integer, Branch> branches; // Map of all branches, keyed by branch ID

    /**
     * Constructs a ProductController with a given map of products.
     *
     * @param products A map of all products, keyed by catalog number.
     */
    public ProductController(HashMap<Integer, Product> products) {
        this.products = products;
        this.branches = new HashMap<>();
        this.productRepository = new ProductRepositoryImpl();
        this.itemRepository = new ItemRepositoryImpl();
        this.soldItemRepository = new SoldItemRepositoryImpl();
    }

    /**
     * Sets the branch mapping for the system. This method clears the existing branches
     * and replaces them with the provided mapping.
     *
     * @param branches a HashMap containing Branch objects, keyed by branch ID
     */
    public void setBranches(HashMap<Integer, Branch> branches) {
        this.branches.clear();
        this.branches.putAll(branches);
    }



    public boolean updateCostPriceByCatalogNumber(int catalog_number, double new_price) throws SQLException {
        ProductDTO product = productRepository.getProductByCatalogNumber(catalog_number);
        if (product == null) {
            return false;
        }

        // Update business logic prices
        product.setCostPriceBeforeSupplierDiscount(new_price);

        double costAfter = new_price * (1 - product.getSupplierDiscount() / 100.0);
        product.setCostPriceAfterSupplierDiscount(costAfter);

        double saleBefore = costAfter * 2;
        double saleAfter = saleBefore * (1 - product.getStoreDiscount() / 100.0);
        product.setSalePriceBeforeStoreDiscount(saleBefore);
        product.setSalePriceAfterStoreDiscount(saleAfter);

        // Update the database        productRepository.UpdateCostPrice(catalog_number, new_price);  // Update cost price only
        productRepository.UpdateCalculatedPrices(product);             // Update all derived prices

        return true;
    }


    public boolean updateProductSupplyDetails(int catalog_number, String supplyDaysInTheWeek, Integer demand) throws SQLException {
        Product product = products.get(catalog_number);
        if (product == null) return false;

        if (supplyDaysInTheWeek != null)
            product.setSupplyDaysInTheWeek(supplyDaysInTheWeek);
        if (demand != null)
            product.setProductDemandLevel(demand);

        ProductDTO dto = new ProductDTO(
                product.getCatalogNumber(),
                product.getProductName(),
                product.getCategory(),
                product.getSubCategory(),
                product.getSupplierName(),
                product.getSize(),
                product.getCostPriceBeforeSupplierDiscount(),
                product.getSupplierDiscount(),
                product.getStoreDiscount(),
                product.getSupplyDaysInTheWeek(),
                product.getProductDemandLevel()
        );

        productRepository.updateProduct(dto);
        return true;
    }

    /**
     * Checks whether at least one product exists with the given category.
     *
     * @param category the category name to check
     * @return true if a product with the specified category is found; false otherwise
     */
    public boolean hasCategory(String category) {
        return products.values().stream()
                .anyMatch(product -> product.getCategory().equalsIgnoreCase(category));
    }

    /**
     * Checks whether at least one product exists with the given sub-category.
     *
     * @param sub_category the sub-category name to check
     * @return true if a product with the specified sub-category is found; false otherwise
     */
    public boolean hasSubCategory(String sub_category) {
        return products.values().stream()
                .anyMatch(product -> product.getSubCategory().equalsIgnoreCase(sub_category));
    }

    /**
     * Determines if a given catalog number does not exist in the product inventory.
     *
     * @param catalog_number the catalog number to verify
     * @return true if the catalog number is not found; false if the product exists
     */
    public boolean isUnknownCatalogNumber(int catalog_number) {
        return !products.containsKey(catalog_number);
    }


    public String showProductQuantities(int catalog_number, int branch_id) {
        ProductDTO productDTO;
        try {
            productDTO = productRepository.getProductByCatalogNumber(catalog_number);
            if (productDTO == null) {
                return "Invalid Product Catalog Number: " + catalog_number + ". This product does not exist.";
            }
        } catch (Exception e) {
            return "❌ Failed to fetch product info from DB: " + e.getMessage();
        }

        // Use ItemRepository to fetch items from DB
        ItemRepositoryImpl itemRepository = new ItemRepositoryImpl();
        List<ItemDTO> allItems = itemRepository.getAllItems();

        int warehouse_quantity = 0;
        int store_quantity = 0;

        for (ItemDTO item : allItems) {
            if (item.getCatalogNumber() == catalog_number &&
                    item.getBranchId() == branch_id &&
                    !item.IsDefective()) {

                String location = item.getStorageLocation();
                if ("Warehouse".equalsIgnoreCase(location)) {
                    warehouse_quantity++;
                } else if ("InteriorStore".equalsIgnoreCase(location)) {
                    store_quantity++;
                }
            }
        }

        if (warehouse_quantity == 0 && store_quantity == 0) {
            return "No items found for Product Catalog Number: " + catalog_number + " in Branch " + branch_id;
        }

        return "Branch: " + branch_id + "\n"
                + "Product Catalog Number: " + catalog_number + "\n"
                + "Warehouse quantity: " + warehouse_quantity + "\n"
                + "Store quantity: " + store_quantity;
    }


    public String showProductPurchasesPrices(int catalogNumber, int branchId) {
        List<SoldItemDTO> sales = soldItemRepository.getSalesByCatalogAndBranch(catalogNumber, branchId);

        if (sales.isEmpty()) {
            return "No purchases found for Product Catalog Number " + catalogNumber + " in Branch " + branchId + ".";
        }

        StringBuilder result = new StringBuilder();
        result.append("Sale prices for Product Catalog Number ")
                .append(catalogNumber)
                .append(" (Branch ").append(branchId).append("):\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < sales.size(); i++) {
            SoldItemDTO sale = sales.get(i);
            result.append(i + 1)
                    .append(". ")
                    .append(String.format("%.2f", sale.getSalePrice()))
                    .append(" ₪ (Sale Date: ")
                    .append(sale.getSaleDate().format(formatter))
                    .append(")\n");
        }

        return result.toString();
    }

    /**
     * Adds a new product to the system's in-memory product collection.
     *
     * @param product the Product object to add, identified by its catalog number
     */
    public void addProduct(Product product) {
        products.put(product.getCatalogNumber(), product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public boolean productExists(int catalogNumber) {
        return products.containsKey(catalogNumber);
    }

    public void updateAllProductQuantities() {
        try {        // Fetch all items from DB
        List<ItemDTO> items = itemRepository.getAllItems();

        // Send the list to repository to calculate and update quantities in products table
            productRepository.updateQuantitiesFromItems(items);

            System.out.println("✅ Product quantities updated in database.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to update product quantities: " + e.getMessage());
        }
    }

}
