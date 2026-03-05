package Suppliers.DTO;

    public class ProductSupplierDTO {
        int supplierID;
        int Catalog_Number;
        int product_id;
        int agreement_id;
        double Price;
        String unitsOfMeasure;


    public ProductSupplierDTO(int product_id, int catalog_Number,int supplierID, int  agreement_ID,  double price, String unitsOfMeasure) {
        this.supplierID = supplierID;
        Catalog_Number = catalog_Number;
        this.product_id = product_id;
        this.agreement_id = agreement_ID;
        Price = price;
        this.unitsOfMeasure = unitsOfMeasure;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public int getCatalog_Number() {
        return Catalog_Number;
    }

    public int getAgreement_id() {
        return agreement_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public double getPrice() {
        return Price;
    }

    public String getUnitsOfMeasure() {
        return unitsOfMeasure;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public void setCatalogNumber(int catalog_Number) {
        Catalog_Number = catalog_Number;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public void setUnitsOfMeasure(String unitsOfMeasure) {
        this.unitsOfMeasure = unitsOfMeasure;
    }
}
