package Inventory.Domain;

import java.util.HashMap;
import java.util.HashSet;
import Inventory.Repository.IItemRepository;
import java.util.List;
import Inventory.DTO.ItemDTO;
import Inventory.Repository.ItemRepositoryImpl;

/**
 * Represents a single branch in the inventory system.
 * Each branch holds its own collection of items.
 */
public class Branch {

    private final int branchId;
    private final HashMap<Integer, ItemDTO> items; // Key: item_id, Value: Item
    private final HashSet<Integer> catalog_numbers;

    /**
     * Constructs a Branch with a given branch ID.
     *
     * @param branchId the unique ID of the branch
     */
    public Branch(int branchId) {
        this.branchId = branchId;
        this.items = new HashMap<>();
        this.catalog_numbers = new HashSet<>();

    }

    /**
     * Returns the ID of the branch.
     *
     * @return the branch ID
     */
    public int getBranchId() {
        return branchId;
    }

    /**
     * Adds an item to the branch.
     *
     * @param item the item to add
     */
    public void addItem(ItemDTO item) {
        items.put(item.getItemId(), item);
        catalog_numbers.add(item.getCatalogNumber());
    }

    /**
     * Removes an item from the branch by its ID.
     *
     * @param itemId the ID of the item to remove
     */
    public void removeItem(int itemId) {
        ItemDTO removedItem = items.remove(itemId);
        if (removedItem != null) {
            // Ensure the catalog number remains in the catalog_numbers set
            catalog_numbers.add(removedItem.getCatalogNumber());
        }
    }

    /**
     * Retrieves an item by its ID.
     *
     * @param itemId the ID of the item to retrieve
     * @return the item if found, null otherwise
     */
    public ItemDTO getItem(int itemId) {
        return items.get(itemId);
    }

    /**
     * Returns all items in this branch.
     *
     * @return the map of item IDs to items
     */
    public HashMap<Integer, ItemDTO> getItems() {
        return items;
    }

    /**
     * Returns all catalog numbers associated with this branch.
     *
     * @return a set of catalog numbers
     */
    public HashSet<Integer> getCatalogNumbers() {
        return catalog_numbers;
    }

    public void loadAllItemsFromDB() {
            IItemRepository a = new ItemRepositoryImpl();
            List<ItemDTO> itemsFromDB = a.getAllItems();
            for (ItemDTO item : itemsFromDB) {
                this.items.put(item.getItemId(), item);
            }
            System.out.println("Loaded " + itemsFromDB.size() + " items into branch.");
    }

    public boolean isCriticalStockLevel(int catalogNumber, int minRequired) {
        int total = 0;
        for (ItemDTO item : items.values()) {
            if (item.getCatalogNumber() == catalogNumber && !item.IsDefective()) {
                total++;
            }
        }
        return total < minRequired;
    }
}
