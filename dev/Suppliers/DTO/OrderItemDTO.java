package Suppliers.DTO;

public class OrderItemDTO {
    private int productId;     // מזהה מוצר
    private int quantity;      // כמות
    private int supplierId;    // מזהה ספק

    public OrderItemDTO(int productId, int quantity, int supplierId) {
        this.productId = productId;
        this.quantity = quantity;
        this.supplierId = supplierId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
}