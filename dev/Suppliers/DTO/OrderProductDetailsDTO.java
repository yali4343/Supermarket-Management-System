package Suppliers.DTO;

public class OrderProductDetailsDTO {
    private int supplierId;
    private String supplierName;
    private int agreementId;
    private int productId;
    private double price;
    private double discount;
    private int quantity;
    private String[] deliveryDays;


    public OrderProductDetailsDTO(int supplierId, String supplierName, String[] deliveryDays, int agreementId, int productId, double price, double discount, int quantity) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.deliveryDays = deliveryDays;
        this.agreementId = agreementId;
        this.productId = productId;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;

    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getProductId() {
        return productId;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public String[] getDeliveryDays() {
        return deliveryDays;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDeliveryDays(String[] deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public int getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }
}

