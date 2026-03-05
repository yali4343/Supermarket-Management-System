package Inventory.DTO;


public class ItemDTO {
    private int catalog_number;
    private int item_id;
    private int branch_id;
    private String item_expiring_date;
    private String storage_location;
    private String section_in_store;
    private boolean is_defect;



    public ItemDTO() {
    }

    public ItemDTO(int catalog_number, int branch_id, String storage_location,
                   String section_in_store, boolean is_defect, String item_expiring_date) {
        this.catalog_number = catalog_number;
        this.branch_id = branch_id;
        this.storage_location = storage_location;
        this.section_in_store = section_in_store;
        this.is_defect = is_defect;
        this.item_expiring_date = item_expiring_date;
        this.item_id = 0; // Represents no ID yet, will be auto-generated
    }

    public ItemDTO(int item_id, int catalog_number, int branch_id, String storage_location,
                   String section_in_store, boolean is_defect, String item_expiring_date) {
        this.item_id = item_id;
        this.catalog_number = catalog_number;
        this.branch_id = branch_id;
        this.storage_location = storage_location;
        this.section_in_store = section_in_store;
        this.is_defect = is_defect;
        this.item_expiring_date = item_expiring_date;
    }

    public int getItemId() {
        return item_id;
    }

    public int getCatalogNumber() {
        return catalog_number;
    }

    public int getBranchId() {
        return branch_id;
    }

    public String getItemExpiringDate() {
        return item_expiring_date;
    }

    public boolean IsDefective() {
        return is_defect;
    }

    public String getStorageLocation() {
        return storage_location;
    }

    public String getSectionInStore() {
        return section_in_store;
    }

    public void setItemId(int item_id) {
        this.item_id = item_id;
    }

    public void setCatalogNumber(int catalog_number) {
        this.catalog_number = catalog_number;
    }

    public void setBranchId(int branch_id) {
        this.branch_id = branch_id;
    }

    public void setExpirationDate(String item_expiring_date) {
        this.item_expiring_date = item_expiring_date;
    }    public void setIsDefective(boolean is_defect) {
        this.is_defect = is_defect;
    }

    public void setLocation(String location) {
        this.storage_location = location;
    }


    public void setSectionInStore(String section_in_store) {
        this.section_in_store = section_in_store;
    }

}
