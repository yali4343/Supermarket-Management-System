package Inventory.DTO;

public class ShortageOrderDTO {
    private int orderId;
    private int productCatalogNumber;
    private int quantity;
    private double costPriceBeforeSupplierDiscount;
    private double supplierDiscount;
    private String orderDate;
    private int branchId;
    private String daysInTheWeek; 
    private int supplierId;       
    private String supplierName; 
    private int quantityNeeded;
    private int currentStock;
    private String status;  // PENDING, ORDERED, DELIVERED    // Full constructor
    public ShortageOrderDTO(int orderId, int productCatalogNumber, int quantity,
                            double costPriceBeforeSupplierDiscount, double supplierDiscount,
                            String orderDate, int branchId, String daysInTheWeek,
                            int supplierId, String supplierName,
                            int quantityNeeded, int currentStock, String status) {
        this.orderId = orderId;
        this.productCatalogNumber = productCatalogNumber;
        this.quantity = quantity;
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
        this.supplierDiscount = supplierDiscount;
        this.orderDate = orderDate;
        this.branchId = branchId;
        this.daysInTheWeek = daysInTheWeek;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.quantityNeeded = quantityNeeded;
        this.currentStock = currentStock;
        this.status = status;
    }    // Getters
    public int getOrderId() {
        return orderId;
    }

    public int getProductCatalogNumber() {
        return productCatalogNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCostPriceBeforeSupplierDiscount() {
        return costPriceBeforeSupplierDiscount;
    }

    public double getSupplierDiscount() {
        return supplierDiscount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public int getBranchId() {
        return branchId;
    }

    public String getDaysInTheWeek() {
        return daysInTheWeek;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getQuantityNeeded() {
        return quantityNeeded;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public String getStatus() {
        return status;
    }    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductCatalogNumber(int productCatalogNumber) {
        this.productCatalogNumber = productCatalogNumber;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCostPriceBeforeSupplierDiscount(double costPriceBeforeSupplierDiscount) {
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
    }

    public void setSupplierDiscount(double supplierDiscount) {
        this.supplierDiscount = supplierDiscount;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public void setDaysInTheWeek(String daysInTheWeek) {
        this.daysInTheWeek = daysInTheWeek;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setQuantityNeeded(int quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
