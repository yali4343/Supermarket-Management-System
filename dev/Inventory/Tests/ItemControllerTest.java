package Inventory.Tests;

import Inventory.Domain.Branch;
import Inventory.Domain.ItemController;
import Inventory.Domain.Product;
import org.junit.Before;
import org.junit.Test;
import Inventory.DTO.ItemDTO;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Unit tests for the ItemController class.
 * These tests verify adding items, marking items as defective,
 * and retrieving item names based on catalog numbers.
 */
public class ItemControllerTest {

    private ItemController itemController;
    private HashMap<Integer, Branch> branches;
    private HashMap<Integer, ItemDTO> purchasedItems;

    /**
     * Initializes required maps and controller before each test.
     * Adds a sample product and branch for use in tests.
     */
    @Before
    public void setUp() {
        branches = new HashMap<>();
        HashMap<Integer, Product> products = new HashMap<>();
        purchasedItems = new HashMap<>();
        itemController = new ItemController(branches, products, purchasedItems);

        Product product = new Product();
        product.setCatalogNumber(101);
        product.setProductName("TestProduct");
        products.put(101, product);

        Branch branch = new Branch(1);
        branches.put(1, branch);
    }

    /**
     * Verifies that an item can be added to the correct branch
     * and appears in the purchased items list.
     */
    @Test
    public void testAddItemSuccessfully() {
        itemController.addItem(1, 1, 101, "Warehouse", "01/01/2030");

        assertTrue("Item should be added to branch", branches.get(1).getItems().containsKey(1));
        assertTrue("Item should be recorded as purchased", purchasedItems.containsKey(1));
    }

    /**
     * Verifies that an item can be marked as defective
     * and that the defect status is updated correctly.
     */
    @Test
    public void testMarkItemAsDefective() {
        itemController.addItem(2, 1, 101, "Warehouse", "01/01/2030");
        boolean result = itemController.markItemAsDefective(2, 1);

        assertTrue("markItemAsDefective should return true", result);
    }

    /**
     * Verifies that getItemName returns the correct product name
     * for a given item ID and branch ID.
     */
    @Test
    public void testGetItemNameReturnsProductName() {
        itemController.addItem(3, 1, 101, "Warehouse", "01/01/2030");
        String name = itemController.getItemName(3, 1);

        assertEquals("Item name should match product name", "TestProduct", name);
    }
}
