package Suppliers.Domain;

public class Discount {
    int productID;
    int supplierID;
    int agreementID;
    int amount;
    double DiscountPercentage;

    public Discount(int product_id, int supplier_id, int agreement_id, int amount, double discount_percentage){
        this.productID = product_id;
        this.supplierID = supplier_id;
        this.agreementID = agreement_id;
        this.amount = amount;
        this.DiscountPercentage = discount_percentage;
    }

    public int getProductID() {
        return productID;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public int getAmount() {
        return amount;
    }

    public double getDiscountPercentage() {
        return DiscountPercentage;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getAgreementID() {
        return agreementID;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.DiscountPercentage = discountPercentage;
    }
}
