package Inventory.Repository;

import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface defining the contract for product-related database operations.
 */
public interface IProductRepository {

    /**
     * Adds a new product to the database.
     *
     * @param product The product to add.
     * @throws SQLException If a database access error occurs.
     */
    void addProduct(ProductDTO product) throws SQLException;

    /**
     * Updates an existing product's details in the database.
     *
     * @param product The updated product information.
     * @throws SQLException If a database access error occurs.
     */
    void updateProduct(ProductDTO product) throws SQLException;

    /**
     * Deletes a product from the database by its catalog number.
     *
     * @param catalogNumber The catalog number of the product to delete.
     * @throws SQLException If a database access error occurs.
     */
    void deleteProduct(int catalogNumber) throws SQLException;

    /**
     * Retrieves a product from the database by its catalog number.
     *
     * @param catalogNumber The catalog number of the desired product.
     * @return The corresponding ProductDTO if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    ProductDTO getProductByCatalogNumber(int catalogNumber) throws SQLException;

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all ProductDTO objects.
     * @throws SQLException If a database access error occurs.
     */
    List<ProductDTO> getAllProducts() throws SQLException;

    /**
     * Checks if a product with the specified catalog number exists in the database.
     *
     * @param catalogNumber The catalog number to check.
     * @return True if the product exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean productExists(int catalogNumber) throws SQLException;

    /**
     * Updates the quantity of a product in the store.
     *
     * @param catalogNumber The catalog number of the product.
     * @param quantity      The new store quantity.
     * @throws SQLException If a database access error occurs.
     */
    void updateQuantityInStore(int catalogNumber, int quantity) throws SQLException;

    /**
     * Updates the quantity of a product in the warehouse.
     *
     * @param catalogNumber The catalog number of the product.
     * @param quantity      The new warehouse quantity.
     * @throws SQLException If a database access error occurs.
     */
    void updateQuantityInWarehouse(int catalogNumber, int quantity) throws SQLException;

    /**
     * Updates both the store and warehouse quantities for a product.
     *
     * @param catalogNumber     The catalog number of the product.
     * @param storeQuantity     The new store quantity.
     * @param warehouseQuantity The new warehouse quantity.
     * @throws SQLException If a database access error occurs.
     */
    void updateQuantities(int catalogNumber, int storeQuantity, int warehouseQuantity) throws SQLException;

    /**
     * Updates product quantities based on a list of item entries.
     *
     * @param items List of ItemDTOs used to calculate store and warehouse quantities.
     * @throws SQLException If a database access error occurs.
     */
    void updateQuantitiesFromItems(List<ItemDTO> items) throws SQLException;

    void UpdateCostPrice(int catalogNumber, double newCostPrice) throws SQLException;

    void UpdateCalculatedPrices(ProductDTO product) throws SQLException;

    List<ProductDTO> getProductsBySizes(List<Integer> sizes) throws SQLException;


}