package Inventory.Tests;

import Inventory.Domain.DiscountType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the DiscountType enum.
 * Verifies that the enum contains the correct values and returns expected strings.
 */
public class DiscountTypeTest {

    /**
     * Verifies that DiscountType contains exactly two enum values.
     */
    @Test
    public void testEnumValuesCount() {
        DiscountType[] values = DiscountType.values();
        assertEquals("DiscountType should have 2 values", 2, values.length);
    }

    /**
     * Verifies that all enum values can be accessed by name using valueOf().
     */
    @Test
    public void testEnumValuesContent() {
        assertEquals("Enum value SUPPLIER should exist", DiscountType.SUPPLIER, DiscountType.valueOf("SUPPLIER"));
        assertEquals("Enum value STORE should exist", DiscountType.STORE, DiscountType.valueOf("STORE"));
    }

    /**
     * Verifies that calling name() on the enum constants returns their string names.
     */
    @Test
    public void testEnumNameMethod() {
        assertEquals("Calling name() should return 'SUPPLIER'", "SUPPLIER", DiscountType.SUPPLIER.name());
        assertEquals("Calling name() should return 'STORE'", "STORE", DiscountType.STORE.name());
    }

    /**
     * Verifies that ordinal values are as expected (order of declaration).
     */
    @Test
    public void testEnumOrdinals() {
        assertEquals("SUPPLIER should have ordinal 0", 0, DiscountType.SUPPLIER.ordinal());
        assertEquals("STORE should have ordinal 1", 1, DiscountType.STORE.ordinal());
    }
}
