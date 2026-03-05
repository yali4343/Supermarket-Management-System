package Inventory.Tests;

import Inventory.Domain.Item;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for the Item class.
 * Verifies correct behavior of all getters and setters, including default values.
 */
public class ItemTest {

    /**
     * Verifies that setting and getting the item ID works correctly.
     */
    @Test
    public void testSetAndGetItemId() {
        Item item = new Item();
        item.setItemId(123);
        assertEquals("Item ID should be 123", 123, item.getItemId());
    }

    /**
     * Verifies that setting and getting the catalog number works correctly.
     */
    @Test
    public void testSetAndGetCatalogNumber() {
        Item item = new Item();
        item.setCatalog_number(4567);
        assertEquals("Catalog number should be 4567", 4567, item.getCatalogNumber());
    }

    /**
     * Verifies that setting and getting the branch ID works correctly.
     */
    @Test
    public void testSetAndGetBranchId() {
        Item item = new Item();
        item.setBranchId(10);
        assertEquals("Branch ID should be 10", 10, item.getBranchId());
    }

    /**
     * Verifies that setting and getting the expiration date works correctly.
     */
    @Test
    public void testSetAndGetItemExpiringDate() {
        Item item = new Item();
        item.setItemExpiringDate("2025-12-31");
        assertEquals("Expiration date should be 2025-12-31", "2025-12-31", item.getItemExpiringDate());
    }

    /**
     * Verifies that setting and getting the storage location works correctly.
     */
    @Test
    public void testSetAndGetStorageLocation() {
        Item item = new Item();
        item.setStorageLocation("Warehouse");
        assertEquals("Storage location should be 'Warehouse'", "Warehouse", item.getStorageLocation());
    }

    /**
     * Verifies that setting and getting the section in the store works correctly.
     */
    @Test
    public void testSetAndGetSectionInStore() {
        Item item = new Item();
        item.setSectionInStore("C3");
        assertEquals("Section in store should be 'C3'", "C3", item.getSectionInStore());
    }

    /**
     * Verifies that an item can be marked as defective and queried for defect status.
     */
    @Test
    public void testSetAndIsDefect() {
        Item item = new Item();
        item.setDefect(true);
        assertTrue("Item should be marked as defective", item.isDefect());
    }

    /**
     * Verifies that setting and getting the sale date works correctly.
     */
    @Test
    public void testSetAndGetSaleDate() {
        Item item = new Item();
        LocalDate date = LocalDate.of(2025, 5, 5);
        item.setSaleDate(date);
        assertEquals("Sale date should be 2025-05-05", date, item.getSaleDate());
    }

    /**
     * Verifies that the default values of a newly created Item object are as expected.
     */
    @Test
    public void testDefaultValues() {
        Item item = new Item();
        assertEquals("Default item ID should be 0", 0, item.getItemId());
        assertEquals("Default catalog number should be 0", 0, item.getCatalogNumber());
        assertEquals("Default branch ID should be 0", 0, item.getBranchId());
        assertNull("Default expiration date should be null", item.getItemExpiringDate());
        assertNull("Default storage location should be null", item.getStorageLocation());
        assertNull("Default section in store should be null", item.getSectionInStore());
        assertFalse("Default defect status should be false", item.isDefect());
        assertNull("Default sale date should be null", item.getSaleDate());
    }
}
