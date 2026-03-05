package Inventory.Domain;

import Inventory.InventoryUtils.DateUtils;

/**
 * Represents a product in the inventory system.
 * Each product is uniquely identified by a catalog number and contains information
 * about category, sub-category, demand, pricing, quantity, discounts, and supplier name.
 */
public class Product {

    private int catalog_number;
    private String product_name;
    private String category;
    private String sub_category;
    private int size;
    private int product_demand_level;
    private String supplyDaysInTheWeek;
    private int total_quantity;
    private int minimum_quantity_for_alert;
    private int quantity_in_store;
    private int quantity_in_warehouse;
    private String supplier_name;
    private Discount discount;
    private double supplier_discount;
    private double cost_price_before_supplier_discount;
    private double cost_price_after_supplier_discount;
    private double store_discount;
    private double sale_price_before_store_discount;
    private double sale_price_after_store_discount;
    private int branchId;


    public int getCatalogNumber() {
        return catalog_number;
    }

    public void setCatalogNumber(int catalog_number) {
        this.catalog_number = catalog_number;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String name) {
        this.product_name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return sub_category;
    }

    public void setSubCategory(String sub_category) {
        this.sub_category = sub_category;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getProductDemandLevel() {
        return product_demand_level;
    }

    public void setProductDemandLevel(int product_demand_level) {
        this.product_demand_level = product_demand_level;
        recalculateMinimumQuantityForAlert();
    }

    public String getSupplyDaysInTheWeek() {
        return supplyDaysInTheWeek;
    }

    public void setSupplyDaysInTheWeek(String supplyDaysInTheWeek) {
        this.supplyDaysInTheWeek = supplyDaysInTheWeek;
        recalculateMinimumQuantityForAlert();
    }

    public int getSupplyTime() {
        if (supplyDaysInTheWeek == null) {
            return 7; // Default - assume one week delivery time
        }
        return DateUtils.calculateNextSupplyDayOffset(supplyDaysInTheWeek);
    }

    public void recalculateMinimumQuantityForAlert() {
        int supplyTime = getSupplyTime();
        this.minimum_quantity_for_alert = (int) (0.5 * product_demand_level + 0.5 * supplyTime);
    }

    public int getTotalQuantity() {
        return total_quantity;
    }

    public void setTotalQuantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }

    public int getMinimumQuantityForAlert() {
        return minimum_quantity_for_alert;
    }

    public void setMinimumQuantityForAlert(int minimum_quantity_for_alert) {
        this.minimum_quantity_for_alert = minimum_quantity_for_alert;
    }

    public int getQuantityInStore() {
        return quantity_in_store;
    }

    public void setQuantityInStore(int quantity_in_store) {
        this.quantity_in_store = quantity_in_store;
        updateTotalQuantity();
    }

    public int getQuantityInWarehouse() {
        return quantity_in_warehouse;
    }

    public void setQuantityInWarehouse(int quantity_in_warehouse) {
        this.quantity_in_warehouse = quantity_in_warehouse;
        updateTotalQuantity();
    }

    private void updateTotalQuantity() {
        this.total_quantity = this.quantity_in_store + this.quantity_in_warehouse;
    }

    public String getSupplierName() {
        return supplier_name;
    }

    public void setSupplierName(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public double getSupplierDiscount() {
        return supplier_discount;
    }

    public void setSupplierDiscount(double supplier_discount) {
        this.supplier_discount = supplier_discount;
        recalculatePrices();
    }

    public double getCostPriceBeforeSupplierDiscount() {
        return cost_price_before_supplier_discount;
    }

    public void setCostPriceBeforeSupplierDiscount(double cost_price_before_supplier_discount) {
        this.cost_price_before_supplier_discount = cost_price_before_supplier_discount;
        recalculatePrices();
    }

    public double getCostPriceAfterSupplierDiscount() {
        return cost_price_after_supplier_discount;
    }

    public void setCostPriceAfterSupplierDiscount(double cost_price_after_supplier_discount) {
        this.cost_price_after_supplier_discount = cost_price_after_supplier_discount;
    }

    public double getStoreDiscount() {
        return store_discount;
    }

    public void setStoreDiscount(double store_discount) {
        this.store_discount = store_discount;
        recalculatePrices();
    }

    public double getSalePriceBeforeStoreDiscount() {
        return sale_price_before_store_discount;
    }

    public void setSalePriceBeforeStoreDiscount(double sale_price_before_store_discount) {
        this.sale_price_before_store_discount = sale_price_before_store_discount;
    }

    public double getSalePriceAfterStoreDiscount() {
        return sale_price_after_store_discount;
    }

    public void setSalePriceAfterStoreDiscount(double sale_price_after_store_discount) {
        this.sale_price_after_store_discount = sale_price_after_store_discount;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    private void recalculatePrices() {
        this.cost_price_after_supplier_discount = cost_price_before_supplier_discount * (1 - supplier_discount / 100.0);
        this.sale_price_before_store_discount = cost_price_after_supplier_discount * 2;
        this.sale_price_after_store_discount = sale_price_before_store_discount * (1 - store_discount / 100.0);
    }
}
