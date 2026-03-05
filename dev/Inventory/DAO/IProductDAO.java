package Inventory.DAO;

import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for managing product-related operations in the database.
 */
public interface IProductDAO {

    /**
     * Inserts a new product into the database.
     * @param dto the product data to insert
     * @throws SQLException if a database access error occurs
     */
    void Insert(ProductDTO dto) throws SQLException;

    /**
     * Updates an existing product in the database.
     * @param dto the updated product data
     * @throws SQLException if the product doesn't exist or update fails
     */
    void Update(ProductDTO dto) throws SQLException;

    /**
     * Deletes a product from the database by its catalog number.
     * @param catalogNumber the product's catalog number
     * @throws SQLException if deletion fails
     */
    void DeleteByCatalogNumber(int catalogNumber) throws SQLException;

    /**
     * Retrieves a product from the database by its catalog number.
     * @param catalogNumber the product's catalog number
     * @return the matching ProductDTO, or null if not found
     * @throws SQLException if a query error occurs
     */
    ProductDTO GetProductByCatalogNumber(int catalogNumber) throws SQLException;

    /**
     * Retrieves all products stored in the database.
     * @return a list of all ProductDTOs
     */
    List<ProductDTO> getAllProducts();

    void UpdateCostPrice(int catalogNumber, double newCostPrice) throws SQLException;

    void UpdateCalculatedPrices(ProductDTO product) throws SQLException;

    public List<ProductDTO> getProductsBySizes(List<Integer> sizes) throws SQLException;

}
