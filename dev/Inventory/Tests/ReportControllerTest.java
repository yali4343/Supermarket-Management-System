package Inventory.Tests;

import Inventory.Domain.Branch;
import Inventory.Domain.Product;
import Inventory.Domain.ReportController;
import org.junit.Before;
import org.junit.Test;
import Inventory.DTO.ItemDTO;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit tests for the ReportController class.
 * These tests validate that the report generator correctly identifies defective and expired items.
 */
public class ReportControllerTest {

    private ReportController reportController;
    private HashMap<Integer, Branch> branches;
    private HashMap<Integer, Product> products;

    /**
     * Sets up a mock branch with a single defective and expired item,
     * and a matching product, for testing reports.
     */
    @Before
    public void setUp() {
        branches = new HashMap<>();
        products = new HashMap<>();

        Branch branch = new Branch(1);
        ItemDTO item = new ItemDTO();
        item.setItemId(1);
        item.setCatalogNumber(1001);
        item.setExpirationDate("01/01/2000");  // clearly expired
        item.setLocation("Warehouse");
        item.setIsDefective(true);
        branch.getItems().put(1, item);

        Product product = new Product();
        product.setCatalogNumber(1001);
        product.setProductName("Milk");
        product.setCategory("Dairy");
        product.setSubCategory("LowFat");
        product.setSize(2);

        branches.put(1, branch);
        products.put(1001, product);

        reportController = new ReportController(branches, products);
    }

    /**
     * Verifies that the generated report includes both "Defective Items" and "Expired Items" sections.
     */
    @Test
    public void testDefectAndExpiredReport() {
        String report = reportController.defectAndExpiredReport(1);

        assertTrue("Report should include section for defective items", report.contains("Defective Items"));
        assertTrue("Report should include section for expired items", report.contains("Expired Items"));
    }
}
