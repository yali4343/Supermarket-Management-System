package Inventory.Tests;

import Inventory.Domain.Discount;
import Inventory.Domain.DiscountController;
import Inventory.Domain.Product;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Unit tests for the DiscountController class.
 */
public class DiscountControllerTest {

    private DiscountController discountController;
    private Product product;

    @Before
    public void setUp() throws Exception {
        HashMap<Integer, Product> products = new HashMap<>();
        product = new Product();
        product.setCatalogNumber(101);
        product.setCategory("Snacks");
        product.setSubCategory("Chips");
        product.setSize(1);
        product.setCostPriceBeforeSupplierDiscount(50.0);
        product.setSupplierDiscount(0.0);
        product.setStoreDiscount(0.0);
        product.setBranchId(1);

        // 🟡 Set private field supplyDaysInTheWeek using reflection
        Field field = Product.class.getDeclaredField("supplyDaysInTheWeek");
        field.setAccessible(true);
        field.set(product, "Monday,Wednesday");

        products.put(101, product);

        discountController = new DiscountController(products);
    }

    @Test
    public void testApplyStoreDiscountToCatalog() {
        Discount discount = new Discount(0.2, LocalDate.now(), LocalDate.now().plusDays(5));
        boolean result = discountController.setStoreDiscountForCatalogNumber(101, discount);
        assertTrue(result);
        assertEquals(0.2, product.getStoreDiscount(), 0.001);
    }

    @Test
    public void testApplySupplierDiscountToCategory() {
        Discount discount = new Discount(0.15, LocalDate.now(), LocalDate.now().plusDays(5));
        boolean result = discountController.setSupplierDiscountForCategory("Snacks", discount);
        assertTrue(result);
        assertEquals(0.15, product.getSupplierDiscount(), 0.001);
    }
}
