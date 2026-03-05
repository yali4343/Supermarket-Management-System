package Integration_And_Unit_Tests;

import Inventory.DTO.ShortageOrderDTO;
import Inventory.Repository.IShortageOrderRepository;
import InventorySupplier.SystemService.ShortageOrderService;
import Suppliers.DTO.OrderProductDetailsDTO;
import Suppliers.Domain.OrderByShortageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for the {@link ShortageOrderService} class.
 * 
 * <p>This test class validates the core functionality of the ShortageOrderService, which is responsible
 * for creating and processing shortage orders when inventory levels fall below required thresholds. The
 * service ensures that appropriate supplier orders are created based on product shortages.</p>
 * 
 * <p>The tests validate several critical aspects of the shortage order process:</p>
 * <ul>
 *   <li>Creation of shortage orders based on valid product shortages</li>
 *   <li>Proper handling of edge cases such as empty shortage lists or zero quantities</li>
 *   <li>Correct data transfer from shortage input to order repository</li>
 *   <li>Appropriate interaction between the service, controller, and repository layers</li>
 * </ul>
 * 
 * <p>These tests use Mockito to mock dependencies, allowing isolated testing of the service's
 * logic without relying on external components like databases or real controllers. The tests also
 * use argument captors to verify the exact values being passed between components.</p>
 * 
 * <p>The ShortageOrderService plays a critical role in the inventory management system by automating
 * the process of replenishing stock when inventory runs low, helping to prevent stockouts while
 * maintaining optimal inventory levels.</p>
 * 
 * @author ADSS Group AJ
 * @version 1.0
 * @since 2025-06-06
 */
public class ShortageOrderServiceTest {

    /** Mock repository for storing and retrieving shortage orders */
    private IShortageOrderRepository shortageOrderRepository;
    
    /** Mock controller for handling shortage-based ordering logic */
    private OrderByShortageController mockController;
    
    /** The service under test */
    private ShortageOrderService service;

    /**
     * Sets up the test environment before each test execution.
     * 
     * <p>This method is executed before each test case and creates fresh mock objects
     * for all dependencies of the ShortageOrderService. Using mock objects allows us to:</p>
     * <ul>
     *   <li>Control the behavior of the dependencies during test execution</li>
     *   <li>Verify that the service interacts correctly with its dependencies</li>
     *   <li>Test the service in isolation without affecting or relying on external systems</li>
     * </ul>
     * 
     * <p>The test uses constructor injection to provide the mocked dependencies to the service,
     * which follows good design principles and makes the tests more maintainable.</p>
     */
    @BeforeEach
    public void setup() {
        shortageOrderRepository = mock(IShortageOrderRepository.class);
        mockController = mock(OrderByShortageController.class);
        // Constructor that injects the Controller and repository
        service = new ShortageOrderService(mockController, shortageOrderRepository);
    }    /**
     * Tests that a valid shortage triggers order creation and repository insertion.
     * 
     * <p>This test verifies the core functionality of the ShortageOrderService by ensuring that:</p>
     * <ul>
     *   <li>The service correctly passes shortage information to the controller</li>
     *   <li>The service receives product details from the controller</li>
     *   <li>The service creates a ShortageOrderDTO with the correct information</li>
     *   <li>The service correctly inserts the order into the repository</li>
     * </ul>
     * 
     * <p>This represents the happy path flow where a product is in shortage and needs to be ordered.</p>
     * 
     * @throws Exception If an error occurs during test execution
     */
    @Test
    public void givenValidShortage_whenOnWakeUp_thenShortageOrderInserted() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1001, 4);

        OrderProductDetailsDTO dto = new OrderProductDetailsDTO(
                201, "Fast Supplier", new String[]{"TUESDAY"}, 301,
                1001, 12.0, 0.15, 4
        );

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of(dto));

        service.onWakeUp(shortage, 2);

        verify(mockController).getShortageOrderProductDetails(eq(shortage), anyLong());
        verify(shortageOrderRepository).insert(any(ShortageOrderDTO.class));
    }

    /**
     * Tests that no order is inserted when the shortage list is empty.
     * 
     * <p>This test validates the system's behavior when there are no shortages to process.
     * It ensures that:</p>
     * <ul>
     *   <li>The service correctly handles empty shortage maps</li>
     *   <li>No unnecessary interactions with the repository occur</li>
     *   <li>The system doesn't create orders when there's nothing to order</li>
     * </ul>
     * 
     * <p>This is an important edge case to test as it prevents creating invalid or unnecessary orders.</p>
     * 
     * @throws Exception If an error occurs during test execution
     */    @Test
    public void givenEmptyShortage_whenOnWakeUp_thenNoInsert() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of());

        service.onWakeUp(shortage, 1);

        verify(shortageOrderRepository, never()).insert(any());
    }

    /**
     * Tests that no order is created when the shortage quantity is zero.
     * 
     * <p>This test validates that the system correctly handles cases where a product is
     * technically in the shortage map but with a quantity of zero. It verifies:</p>
     * <ul>
     *   <li>The service properly handles zero-quantity shortages</li>
     *   <li>No unnecessary processing or database operations occur</li>
     *   <li>No order is created for products that don't actually need replenishment</li>
     * </ul>
     * 
     * <p>This test is important for preventing unnecessary orders and ensuring data integrity
     * in cases where the input data might contain zero values.</p>
     * 
     * @throws Exception If an error occurs during test execution
     */
    @Test
    public void givenShortageWithZeroQuantity_whenOnWakeUp_thenNoInsertHappens() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1002, 0);  // zero quantity

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of()); // Returns empty list since there's nothing to order

        service.onWakeUp(shortage, 3);

        verify(shortageOrderRepository, never()).insert(any());
    }

    /**
     * Tests that the correct values are passed to the repository when creating a shortage order.
     * 
     * <p>This test goes beyond verifying that an order is created and validates the actual content
     * of the created order. It ensures that:</p>
     * <ul>
     *   <li>The correct product catalog number is used in the order</li>
     *   <li>The order quantity matches the shortage quantity</li>
     *   <li>The correct supplier ID is assigned to the order</li>
     *   <li>The branch ID is correctly passed from the service to the repository</li>
     * </ul>
     * 
     * <p>This test uses Mockito's ArgumentCaptor to capture the actual ShortageOrderDTO object
     * passed to the repository, allowing detailed inspection of all its properties. This verifies
     * that data transformation between DTOs is handled correctly by the service.</p>
     * 
     * <p>Data integrity in this transformation is critical to ensure that orders are placed with
     * the correct suppliers for the right products and quantities.</p>
     * 
     * @throws Exception If an error occurs during test execution
     */
    @Test
    public void givenValidShortage_whenOnWakeUp_thenInsertCorrectValues() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1004, 7);

        OrderProductDetailsDTO dto = new OrderProductDetailsDTO(
                202, "Supplier A", new String[]{"WEDNESDAY"}, 302,
                1004, 25.0, 0.1, 7
        );

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of(dto));

        service.onWakeUp(shortage, 5);
        
        // Verify which values were actually sent to the repository
        ArgumentCaptor<ShortageOrderDTO> captor = ArgumentCaptor.forClass(ShortageOrderDTO.class);
        verify(shortageOrderRepository).insert(captor.capture());

        ShortageOrderDTO captured = captor.getValue();
        assertEquals(1004, captured.getProductCatalogNumber());
        assertEquals(7, captured.getQuantity());
        assertEquals(202, captured.getSupplierId());
//        assertEquals(302, captured.getAgreementId());
        assertEquals(5, captured.getBranchId());
    }


}
