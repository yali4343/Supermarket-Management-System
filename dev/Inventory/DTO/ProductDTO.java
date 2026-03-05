package Inventory.DTO;

import Inventory.InventoryUtils.DateUtils;


public class ProductDTO {
    private int catalogNumber;
    private String productName;
    private String category;
    private String subCategory;
    private String supplierName;
    private int size;
    private int productDemandLevel;
    private int supplyTime;
    private String supplyDaysInWeek;
    private double costPriceBeforeSupplierDiscount;
    private double supplierDiscount;
    private double costPriceAfterSupplierDiscount;
    private double storeDiscount;
    private double salePriceBeforeStoreDiscount;
    private double salePriceAfterStoreDiscount;
    private int quantityInWarehouse;
    private int quantityInStore;
    private int minimumQuantityForAlert;
    private int branchId;




    // Constructor with parameters
    public ProductDTO(int catalogNumber, String productName, String category, String subCategory,
                      String supplierName, int size, double costPriceBeforeSupplierDiscount,
                      double supplierDiscount, double storeDiscount,
                      String supplyDaysInWeek, int productDemandLevel) {
        this.catalogNumber = catalogNumber;
        this.productName = productName;
        this.category = category;
        this.subCategory = subCategory;
        this.supplierName = supplierName;
        this.size = size;
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
        this.supplierDiscount = supplierDiscount;
        this.storeDiscount = storeDiscount;
        this.costPriceAfterSupplierDiscount = costPriceBeforeSupplierDiscount * (1 - supplierDiscount / 100);
        this.salePriceBeforeStoreDiscount = this.costPriceAfterSupplierDiscount * 2;
        this.salePriceAfterStoreDiscount = this.salePriceBeforeStoreDiscount * (1 - storeDiscount / 100);

        this.supplyDaysInWeek = supplyDaysInWeek;
        this.supplyTime = DateUtils.calculateNextSupplyDayOffset(supplyDaysInWeek);
        this.productDemandLevel = productDemandLevel;
        this.minimumQuantityForAlert = (int) (0.5 * supplyTime + 0.5 * productDemandLevel);
    }



    // Default constructor
    public ProductDTO() {}

    // Getters and Setters
    public int getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(int catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getProductDemandLevel() {
        return productDemandLevel;
    }

    public void setProductDemandLevel(int productDemandLevel) {
        this.productDemandLevel = productDemandLevel;
    }



    public String getSupplyDaysInWeek() {
        return supplyDaysInWeek;
    }

    public void setSupplyDaysInWeek(String supplyDaysInWeek) {
        this.supplyDaysInWeek = supplyDaysInWeek;
        if (supplyDaysInWeek != null) {
            this.supplyTime = DateUtils.calculateNextSupplyDayOffset(supplyDaysInWeek);
        } else {
            this.supplyTime = 7; // Default supply time if not defined
        }
    }

    public int getSupplyTime() {
        return supplyTime;
    }

    public void setSupplyTime(int supplyTime) {
        this.supplyTime = supplyTime;
    }



    public double getCostPriceBeforeSupplierDiscount() {
        return costPriceBeforeSupplierDiscount;
    }

    public void setCostPriceBeforeSupplierDiscount(double costPriceBeforeSupplierDiscount) {
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
    }

    public double getSupplierDiscount() {
        return supplierDiscount;
    }

    public void setSupplierDiscount(double supplierDiscount) {
        this.supplierDiscount = supplierDiscount;
    }

    public double getCostPriceAfterSupplierDiscount() {
        return costPriceAfterSupplierDiscount;
    }

    public void setCostPriceAfterSupplierDiscount(double costPriceAfterSupplierDiscount) {
        this.costPriceAfterSupplierDiscount = costPriceAfterSupplierDiscount;
    }

    public double getStoreDiscount() {
        return storeDiscount;
    }

    public void setStoreDiscount(double storeDiscount) {
        this.storeDiscount = storeDiscount;
    }

    public double getSalePriceBeforeStoreDiscount() {
        return salePriceBeforeStoreDiscount;
    }

    public void setSalePriceBeforeStoreDiscount(double salePriceBeforeStoreDiscount) {
        this.salePriceBeforeStoreDiscount = salePriceBeforeStoreDiscount;
    }

    public double getSalePriceAfterStoreDiscount() {
        return salePriceAfterStoreDiscount;
    }

    public void setSalePriceAfterStoreDiscount(double salePriceAfterStoreDiscount) {
        this.salePriceAfterStoreDiscount = salePriceAfterStoreDiscount;
    }

    public int getQuantityInWarehouse() {
        return quantityInWarehouse;
    }

    public void setQuantityInWarehouse(int quantityInWarehouse) {
        this.quantityInWarehouse = quantityInWarehouse;
    }

    public int getQuantityInStore() {
        return quantityInStore;
    }

    public void setQuantityInStore(int quantityInStore) {
        this.quantityInStore = quantityInStore;
    }

    public int getMinimumQuantityForAlert() {
        return minimumQuantityForAlert;
    }

    public void setMinimumQuantityForAlert(int minimumQuantityForAlert) {
        this.minimumQuantityForAlert = minimumQuantityForAlert;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
}
