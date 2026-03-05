package Inventory.Tests;

import Inventory.Domain.Discount;
import org.junit.Test;
import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for the Discount class.
 * This test class verifies the proper creation, validation, and behavior of discount attributes.
 */
public class DiscountTest {

    /**
     * Verifies that a Discount object is initialized correctly with valid input values.
     */
    @Test
    public void testValidDiscountCreation() {
        Discount discount = new Discount(20.0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertEquals("Discount rate should be 20%", 20.0, discount.getDiscountRate(), 0.01);
        assertEquals("Start date should match", LocalDate.of(2024, 1, 1), discount.getStartDate());
        assertEquals("End date should match", LocalDate.of(2024, 12, 31), discount.getEndDate());
    }

    /**
     * Verifies that setting a negative discount rate is ignored and the original rate remains unchanged.
     */
    @Test
    public void testInvalidDiscountRate() {
        Discount discount = new Discount(15.0, LocalDate.now(), LocalDate.now().plusDays(10));
        discount.setDiscountRate(-5.0);  // Invalid input
        assertEquals("Invalid discount rate should not be set", 15.0, discount.getDiscountRate(), 0.01);
    }

    /**
     * Verifies that setting a null start date is ignored and the original date is preserved.
     */
    @Test
    public void testSetNullStartDate() {
        Discount discount = new Discount(10.0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        discount.setStartDate(null);
        assertEquals("Start date should remain unchanged when null is set", LocalDate.of(2024, 1, 1), discount.getStartDate());
    }

    /**
     * Verifies that setting a null end date is ignored and the original date is preserved.
     */
    @Test
    public void testSetNullEndDate() {
        Discount discount = new Discount(10.0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        discount.setEndDate(null);
        assertEquals("End date should remain unchanged when null is set", LocalDate.of(2024, 12, 31), discount.getEndDate());
    }

    /**
     * Verifies that isActive() returns true when today's date is within the discount's valid range.
     */
    @Test
    public void testIsActiveTrue() {
        LocalDate today = LocalDate.now();
        Discount discount = new Discount(10.0, today.minusDays(1), today.plusDays(1));
        assertTrue("Discount should be active today", discount.isActive());
    }

    /**
     * Verifies that isActive() returns false when today's date is outside the discount range.
     */
    @Test
    public void testIsActiveFalse() {
        LocalDate today = LocalDate.now();
        Discount discount = new Discount(10.0, today.minusDays(10), today.minusDays(1));
        assertFalse("Discount should be inactive today", discount.isActive());
    }
}
