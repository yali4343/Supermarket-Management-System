package Suppliers.Domain;


/**
 * Represents a product offered by a supplier.
 * Each product has a unique catalog number, product ID, price, unit of measure,
 * and a list of discount rules based on quantity.
 */
public class Product {
    int supplierID;
    int Catalog_Number;
    int product_id;
    double Price;
    String unitsOfMeasure;


    /**
     * Constructs a new product with the given attributes.
     *
     * @param catalog_Number the catalog number of the product
     * @param product_id the unique product ID
     * @param price the price of the product
     * @param unitsOfMeasure the unit of measurement (e.g., kg, unit)
     * @param supplierID the supplier's ID who supplies this product
     */
    public Product(int catalog_Number, int product_id, double price, String unitsOfMeasure, int supplierID){
        this.supplierID = supplierID;
        this.Catalog_Number = catalog_Number;
        this.product_id = product_id;
        this.Price = price;
        this.unitsOfMeasure = unitsOfMeasure;
    }

    /**
     * Sets the unit of measurement for this product.
     *
     * @param unitsOfMeasure the new unit of measurement
     */
    public void setUnitsOfMeasure(String unitsOfMeasure) {
        this.unitsOfMeasure = unitsOfMeasure;
    }

    /**
     * Returns the product's ID.
     *
     * @return the product ID
     */
    public int getProduct_id() {
        return product_id;
    }

    /**
     * Returns the product's catalog number.
     *
     * @return the catalog number
     */
    public int getCatalog_Number() {
        return Catalog_Number;
    }

    /**
     * Returns the product's price.
     *
     * @return the price
     */
    public double getPrice() {
        return Price;
    }

    /**
     * Sets a new price for the product.
     *
     * @param new_price the new price
     */
    public void setPrice(double new_price){
        this.Price = new_price;
    }



    /**
     * Returns the supplier ID of the product.
     *
     * @return the supplier ID
     */
    public int getSupplierID() {
        return supplierID;
    }



}