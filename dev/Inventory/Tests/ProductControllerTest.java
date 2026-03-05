package Inventory.Tests;

import Inventory.Domain.ProductController;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Minimal tests for ProductController that do not access internal state or supplyDaysInTheWeek.
 */
public class ProductControllerTest {

    private ProductController productController;    @Before
    public void setUp() {
        HashMap<Integer, Inventory.Domain.Product> products = new HashMap<>();
        productController = new ProductController(products);
    }

    @Test
    public void testUpdateFailsWhenProductNotInRepository() throws Exception {
        // Attempt to update a product that does not exist in the repository
        boolean result = productController.updateCostPriceByCatalogNumber(101, 50.0);
        assertFalse(result);  // Expected to fail because the product doesn't exist
    }
}
