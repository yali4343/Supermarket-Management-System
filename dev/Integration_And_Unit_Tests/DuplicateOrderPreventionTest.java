package Integration_And_Unit_Tests;

import Inventory.DTO.ShortageOrderDTO;
import Inventory.Repository.IShortageOrderRepository;
import Inventory.Repository.ShortageOrderRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Test class to verify duplicate order prevention functionality in the shortage order system.
 * 
 * This class tests the system's ability to prevent duplicate shortage orders for the same product
 * at the same branch, which is a critical business rule to prevent unnecessary inventory ordering.
 * 
 * The test follows these steps:
 * 1. Verifies that initially there are no pending orders for a test product in a test branch
 * 2. Creates a new shortage order for the test product in the test branch
 * 3. Verifies that the system now correctly identifies a pending order exists
 * 4. Tests that the system correctly differentiates between different products
 * 5. Tests that the system correctly differentiates between different branches
 * 6. Cleans up test data by marking orders as processed
 * 
 * This test validates both positive cases (detecting duplicates when they should be detected)
 * and negative cases (not detecting duplicates when products or branches differ).
 * 
 * @author ADSS Group AJ
 * @version 1.0
 * @since 2025-06-06
 */
public class DuplicateOrderPreventionTest {
      /**
     * Main method to run the duplicate order prevention tests.
     * Executes all test cases and reports success or failure.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing Duplicate Order Prevention...");
        
        try {
            testDuplicateOrderPrevention();
            System.out.println("✅ All tests passed!");
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Core test method that validates the duplicate order prevention functionality.
     * This method tests various scenarios to ensure the system correctly identifies
     * duplicate orders and differentiates between different products and branches.
     * 
     * Test steps:
     * 1. Verify no initial pending orders
     * 2. Create a test shortage order
     * 3. Verify system detects the pending order
     * 4. Verify system doesn't detect pending orders for different products
     * 5. Verify system doesn't detect pending orders for different branches
     * 6. Clean up test data
     * 
     * @throws SQLException If a database access error occurs
     */
    private static void testDuplicateOrderPrevention() throws SQLException {
        IShortageOrderRepository repository = new ShortageOrderRepositoryImpl();
        
        // Test data
        int testProductId = 12345;
        int testBranchId = 1;
        String orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        System.out.println("1. Testing initial state - should have no pending orders");
        boolean hasPendingInitial = repository.hasPendingOrderForProduct(testProductId, testBranchId);
        System.out.println("   Has pending order initially: " + hasPendingInitial);
        assert !hasPendingInitial : "Should not have pending order initially";
        
        System.out.println("2. Creating first order");
        ShortageOrderDTO firstOrder = new ShortageOrderDTO(
            0, // Auto-incremented
            testProductId,
            10, // quantity
            15.50, // cost price
            0.10, // discount
            orderDate,
            testBranchId,
            "Monday, Tuesday", // days
            101, // supplier id
            "Test Supplier",
            10, // quantity needed
            0, // current stock
            "PENDING"
        );
        
        repository.insert(firstOrder);
        System.out.println("   First order created successfully");
        
        System.out.println("3. Checking if pending order exists now");
        boolean hasPendingAfterInsert = repository.hasPendingOrderForProduct(testProductId, testBranchId);
        System.out.println("   Has pending order after insert: " + hasPendingAfterInsert);
        assert hasPendingAfterInsert : "Should have pending order after insert";
        
        System.out.println("4. Testing with different product - should not find pending order");
        boolean hasPendingDifferentProduct = repository.hasPendingOrderForProduct(99999, testBranchId);
        System.out.println("   Has pending order for different product: " + hasPendingDifferentProduct);
        assert !hasPendingDifferentProduct : "Should not have pending order for different product";
        
        System.out.println("5. Testing with different branch - should not find pending order");
        boolean hasPendingDifferentBranch = repository.hasPendingOrderForProduct(testProductId, 999);
        System.out.println("   Has pending order for different branch: " + hasPendingDifferentBranch);
        assert !hasPendingDifferentBranch : "Should not have pending order for different branch";
        
        // Clean up: remove the test order
        System.out.println("6. Cleaning up test data...");
        // Note: In a real test, we'd clean up the test data
        // For now, we'll just mark it as delivered to avoid affecting other tests
        repository.markProcessedForToday(testBranchId);
        System.out.println("   Test data cleaned up");
    }
}
