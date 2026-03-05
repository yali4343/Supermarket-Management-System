package Inventory.Tests;

import Inventory.Domain.InventoryController;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the InventoryController class.
 * This class verifies initialization of controllers and basic method stability.
 */
public class InventoryControllerTest {

    private InventoryController inventory;

    /**
     * Initializes a new InventoryController before each test.
     */
    @Before
    public void setUp() {
        inventory = new InventoryController();
    }

    /**
     * Verifies that all internal controllers (Item, Product, Discount, Report) are properly initialized.
     */
    @Test
    public void testAllControllersAreInitialized() {
        assertNotNull("ItemController should not be null", inventory.getItemController());
        assertNotNull("ProductController should not be null", inventory.getProductController());
        assertNotNull("DiscountController should not be null", inventory.getDiscountController());
        assertNotNull("ReportController should not be null", inventory.getReportController());
    }


}
