package Inventory.Domain;

import Inventory.DTO.ItemDTO;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

import Inventory.DTO.ProductDTO;
import Inventory.DTO.SoldItemDTO;
import Inventory.Repository.*;

/**
 * Controller responsible for managing individual items, their relation to products,
 * and handling item creation and storage logic.
 */
public class ItemController {
    private final IItemRepository itemRepository;
    private final IProductRepository productRepository;
    private final ISoldItemRepository soldItemRepository;
    private final HashMap<Integer, Branch> branches;
    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, ItemDTO> purchased_items;

    /**
     * Constructs an ItemController with branches, product map, and purchased item records.
     *
     * @param branches A map of all store branches, keyed by branch ID.
     * @param products A map of existing products, keyed by catalog number.
     * @param purchased_items A map of purchased items, keyed by item ID.
     */
    public ItemController(HashMap<Integer, Branch> branches, HashMap<Integer, Product> products, HashMap<Integer, ItemDTO> purchased_items) {
        this.branches = branches;
        this.products = products;
        this.purchased_items = purchased_items;
        this.productRepository = new ProductRepositoryImpl();
        this.itemRepository = new ItemRepositoryImpl();
        this.soldItemRepository = new SoldItemRepositoryImpl();
    }

    /**
     * Returns the next available unique Item ID.
     *
     * <p>
     * This method scans all existing items and finds the highest ID,
     * then returns the next number (highest ID + 1).
     * Guarantees that no duplicate item IDs are generated.
     *
     * @return the next available Item ID
     */
    public int getNextAvailableItemId() {
        if (purchased_items.isEmpty()) {
            return 1; // Start from 1 if no items exist
        }
        int maxId = purchased_items.keySet().stream().max(Integer::compareTo).orElse(0);
        return maxId + 1;
    }

    public IItemRepository getItemRepository() {
        return this.itemRepository;
    }


    /**
     * Adds a single item directly to the inventory (without using CSV).
     *
     * @param itemId the unique ID of the new item
     * @param branchId the branch where the item is stored
     * @param catalogNumber the catalog number of the associated product
     * @param storageLocation the location where the item is stored (Warehouse or InteriorStore)
     * @param expiryDate the expiry date of the item in dd/MM/yyyy format
     */
    public void addItem(int itemId, int branchId, int catalogNumber, String storageLocation, String expiryDate) {
        ItemDTO newItem = new ItemDTO();
        newItem.setItemId(itemId);
        newItem.setBranchId(branchId);
        newItem.setCatalogNumber(catalogNumber);
        newItem.setLocation(storageLocation);
        newItem.setExpirationDate(expiryDate);
        newItem.setIsDefective(false); // new items are not defective by default

        // Insert into the branch
        Branch branch = branches.computeIfAbsent(branchId, k -> new Branch(branchId));
        branch.getItems().put(itemId, newItem);

        // Also insert into the general purchased items map
        purchased_items.put(itemId, newItem);
        IItemRepository a = new ItemRepositoryImpl();
        a.addItem(newItem);
    }





    public boolean itemExistsInBranch(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return false;
        return branch.getItems().containsKey(item_Id);
    }



    public void removeItemByDefect(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch != null && branch.getItems().containsKey(item_Id)) {
            ItemDTO item = branch.getItem(item_Id);
            if (item != null) {
                try {
                    itemRepository.markItemAsDefect(item_Id);

                    itemRepository.deleteItem(item_Id);

                    branch.removeItem(item_Id);
                } catch (Exception e) {
                    System.err.println("❌ Failed to remove item as defective from DB: " + e.getMessage());
                }
            }
        }
    }

    public void removeItemByPurchase(int itemId, int branchId) {
        Branch branch = branches.get(branchId);
        if (branch == null || !branch.getItems().containsKey(itemId)) {
            System.out.println("Item does not exist in Branch " + branchId + ".");
            return;
        }

        ItemDTO item = branch.getItem(itemId);
        if (item == null) return;

        ProductDTO product = null;
        try {
            product = productRepository.getProductByCatalogNumber(item.getCatalogNumber());
        } catch (Exception e) {
            System.out.println("❌ Failed to retrieve product: " + e.getMessage());
        }

        if (product != null) {
            double salePrice = product.getSalePriceAfterStoreDiscount();                // 1. Save sale details in sold_items table
            SoldItemDTO sold = new SoldItemDTO();
            sold.setCatalogNumber(item.getCatalogNumber());
            sold.setBranchId(branchId);
            sold.setSaleDate(LocalDate.now());
            sold.setSalePrice(salePrice);

            try {
                soldItemRepository.addSoldItem(sold);
            } catch (Exception e) {
                System.out.println("❌ Failed to record sale: " + e.getMessage());
                return;
            }

            // ✅ 2. Delete from items in DB
            try {
                itemRepository.deleteItem(itemId);
            } catch (Exception e) {
                System.out.println("❌ Failed to delete item from DB: " + e.getMessage());
                return;
            }

            branch.removeItem(itemId);

            // ✅ 4. Print
            System.out.println("\n-----------------------------------------");
            System.out.println("The item \"" + product.getProductName() + "\" has been marked as purchased and removed from Branch " + branchId + ".");
            System.out.printf("The item was sold for: %.2f ₪ (after store discount)%n", salePrice);
            if (branch.isCriticalStockLevel(item.getCatalogNumber(), product.getMinimumQuantityForAlert())) {
                System.out.println("ALERT: The product \"" + product.getProductName() + "\" in Branch " + branchId + " has reached a critical amount!");
                System.out.println("Please consider reordering.");
            }
            System.out.println("-----------------------------------------");
        }
    }


    /**
     * Retrieves the sale price of a product after applying the store discount,
     * based on the catalog number of the given item.
     *
     * @param item_Id The unique identifier of the item.
     * @return The sale price after store discount, or 0.0 if the item or product is not found.
     */
    public double getSalePriceAfterDiscount(int item_Id) {
        for (Branch branch : branches.values()) {
            ItemDTO item = branch.getItems().get(item_Id);
            if (item != null) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null) return product.getSalePriceAfterStoreDiscount();
            }
        }
        return 0.0;
    }





    public boolean markItemAsDefective(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch != null) {
            ItemDTO item = branch.getItems().get(item_Id);
            if (item != null) {
        item.setIsDefective(true);  // Update in memory
        try {
            itemRepository.markItemAsDefective(item_Id, branch_id);  // Update in database
                } catch (SQLException e) {
                    System.err.println("❌ Failed to mark item as defective in DB: " + e.getMessage());
                    return false;
                }
                return true;
            }
        }
        return false;
    }





    /**
     * Retrieves the product name associated with a specific item.
     *
     * @param item_Id The unique identifier of the item.
     * @return The name of the product the item belongs to, or an empty string if not found.
     */
    public String getItemName(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return "";
        ItemDTO item = branch.getItems().get(item_Id);
        if (item == null) return "";
        Product product = products.get(item.getCatalogNumber());
        return product != null ? product.getProductName() : "";
    }


    /**
     * Updates the location and/or section of a specific item in a specific branch.
     *
     * @param item_Id  The unique identifier of the item to update.
     * @param branch_Id The ID of the branch where the item is located.
     * @param location The new storage location (e.g., "Warehouse" or "InteriorStore"). If null, location remains unchanged.
     * @param section  The new section within the location (e.g., "A1"). If null, section remains unchanged.
     * @return true if the item exists in the specified branch and was updated; false otherwise.
     */    public boolean updateItemLocation(int item_Id, int branch_Id, String location, String section) {
        // Query the database directly to get the latest item data
        ItemDTO item = itemRepository.getItemById(item_Id);
        
        // Check if item exists and belongs to the specified branch
        if (item == null || item.getBranchId() != branch_Id) {
            return false;
        }
        
        // Update the item location and/or section
        if (location != null) item.setLocation(location);
        if (section != null) item.setSectionInStore(section);

        // Create a new DTO with updated values
        ItemDTO dto = new ItemDTO(
                item.getItemId(),
                item.getCatalogNumber(),
                item.getBranchId(),
                item.getStorageLocation(),
                item.getSectionInStore(),
                item.IsDefective(),
                item.getItemExpiringDate()
        );

        // Update in database
        itemRepository.updateItem(dto);
        
        // Also update in-memory structure if the branch exists
        Branch branch = branches.get(branch_Id);
        if (branch != null && branch.getItems().containsKey(item_Id)) {
            ItemDTO inMemoryItem = branch.getItems().get(item_Id);
            if (location != null) inMemoryItem.setLocation(location);
            if (section != null) inMemoryItem.setSectionInStore(section);
        }

        return true;
    }


    public String showItemDetails(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) {
            return "There is not item in branch id with ID " + branch_id + " does not exist.";
        }

        ItemDTO item = itemRepository.getItemById(item_Id);
        if (item == null || item.getBranchId() != branch_id) {
            return "Item with ID " + item_Id + " not found in branch " + branch_id + ".";
        }

        ProductDTO product;
        try {
            product = productRepository.getProductByCatalogNumber(item.getCatalogNumber());
            if (product == null) {
                return "Product with catalog number " + item.getCatalogNumber() + " not found.";
            }
        } catch (SQLException e) {
            return "❌ Error retrieving product from DB: " + e.getMessage();
        }

        DecimalFormat df = new DecimalFormat("#.00");

        // Compute prices
        double costAfter = product.getCostPriceBeforeSupplierDiscount() * (1 - product.getSupplierDiscount() / 100);
        double saleBefore = costAfter * 2;
        double saleAfter = saleBefore * (1 - product.getStoreDiscount() / 100);

        return "Item ID: " + item.getItemId() + "\n"
                + "Product name: " + product.getProductName() + "\n"
                + "Expiring Date: " + item.getItemExpiringDate() + "\n"
                + "Location: " + item.getStorageLocation() + ", Section: " + item.getSectionInStore() + "\n"
                + "Product Catalog Number: " + product.getCatalogNumber() + ", Category: "
                + product.getCategory() + ", Sub-Category: " + product.getSubCategory() + "\n"
                + "Size: " + product.getSize() + "\n"
                + "Supplier Discount: " + product.getSupplierDiscount() + "%\n"
                + "Cost price before supplier discount: " + df.format(product.getCostPriceBeforeSupplierDiscount()) + "\n"
                + "Cost price after supplier discount: " + df.format(costAfter) + "\n"
                + "Store Discount: " + product.getStoreDiscount() + "%\n"
                + "Sale price before store discount: " + df.format(saleBefore) + "\n"
                + "Sale price after store discount: " + df.format(saleAfter) + "\n"
                + "Product demand: " + product.getProductDemandLevel() + "\n"
                + "Supply time: " + product.getSupplyDaysInWeek() + " days\n"
                + "supplierName: " + product.getSupplierName() + "\n"
                + "Defective: " + (item.IsDefective() ? "Yes" : "No") + "\n";
    }

    /**
     * Retrieves the catalog number of a specific item in a branch.
     * <p>
     * Used for linking an item to its corresponding product.
     *
     * @param item_Id the ID of the item
     * @param branch_id the ID of the branch where the item is located
     * @return the catalog number if found, or -1 if not found
     */
    public int getCatalogNumber(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null || !branch.getItems().containsKey(item_Id)) return -1;
        return branch.getItems().get(item_Id).getCatalogNumber();
    }

    public ItemDTO getItem(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return null;
        return branch.getItems().get(item_Id);
    }

    public List<ItemDTO> getAllItemsByBranchId(int branchId) throws SQLException {
        return itemRepository.getItemsByBranch(branchId);
    }

}


