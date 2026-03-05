package Inventory.DTO;

public class InventoryProductPeriodic {
    int supplierId;
    int agreementID;
    int catalogNumber;
    int quantity;

    public InventoryProductPeriodic(int supplierId, int agreementID, int catalogNumber, int quantity) {
        this.supplierId = supplierId;
        this.agreementID = agreementID;
        this.catalogNumber = catalogNumber;
        this.quantity = quantity;
    }

    public int getAgreementID() {
        return agreementID;
    }

    public void setAgreementID(int agreementID) {
        this.agreementID = agreementID;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(int catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
