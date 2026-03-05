package Inventory.Domain;

import java.time.LocalDate;

/**
 * Represents a single physical item in the inventory.
 * Each item is associated with a product (via catalog number) and contains details
 * such as size, location, expiration date, and defect status.
 */
public class Item {
    private int catalog_number;
    private int item_id;
    private int branch_id;
    private String item_expiring_date;
    private String storage_location; // warehouse or Interior store
    private String section_in_store; // for example: E7
    private boolean is_defect;
    private LocalDate sale_date;


    /**
     * @return The catalog number of the product this item belongs to.
     */
    public int getCatalogNumber() {
        return catalog_number;
    }

    /**
     * @param catalog_number The catalog number to associate with the item.
     */
    public void setCatalog_number(int catalog_number) {
        this.catalog_number = catalog_number;
    }

    /**
     * @return The unique ID of the item.
     */
    public int getItemId() {
        return item_id;
    }

    /**
     * @param product_id The ID to assign to the item.
     */
    public void setItemId(int product_id) {
        this.item_id = product_id;
    }

    /**
     * @return The branch ID where the item is stored.
     */
    public int getBranchId() {
        return branch_id;
    }

    /**
     * @param branch_id The branch ID to assign to the item.
     */
    public void setBranchId(int branch_id) {
        this.branch_id = branch_id;
    }

    /**
     * @return The expiration date of the item, as a string.
     */
    public String getItemExpiringDate() {
        return item_expiring_date;
    }

    /**
     * @param item_expiring_date The expiration date to assign to the item.
     */
    public void setItemExpiringDate(String item_expiring_date) {
        this.item_expiring_date = item_expiring_date;
    }



    /**
     * @return The section in the store or warehouse where the item is stored (e.g., "E7").
     */
    public String getSectionInStore() {
        return section_in_store;
    }

    public LocalDate getSaleDate() {
        return sale_date;
    }

    /**
     * @param section The section to assign to the item.
     */
    public void setSectionInStore(String section) {
        this.section_in_store = section;
    }

    /**
     * @return The location where the item is stored ("warehouse" or "interiorStore").
     */
    public String getStorageLocation() {
        return storage_location;
    }

    /**
     * @param storage_location The location where the item is stored.
     */
    public void setStorageLocation(String storage_location) {
        this.storage_location = storage_location;
    }

    /**
     * @return true if the item is marked as defective, false otherwise.
     */
    public boolean isDefect() {
        return is_defect;
    }

    /**
     * @param defect true to mark the item as defective, false otherwise.
     */
    public void setDefect(boolean defect) {
        is_defect = defect;
    }

    public void setSaleDate(LocalDate sale_date) {
        this.sale_date = sale_date;
    }
}