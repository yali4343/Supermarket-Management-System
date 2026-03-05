package Integration_And_Unit_Tests;

import Inventory.DTO.PeriodicOrderDTO;
import Inventory.Repository.*;
import InventorySupplier.SystemService.PeriodicOrderService;
import Suppliers.Repository.IInventoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link PeriodicOrderService} class.
 * 
 * <p>This test suite validates the behavior of the periodic order processing service,
 * which is responsible for automatically creating inventory items based on periodic
 * orders scheduled for delivery on the current day. The tests use Mockito to create
 * mock repositories, allowing isolated testing of the service logic without database
 * dependencies.</p>
 * 
 * <p>The test cases specifically verify:</p>
 * <ul>
 *   <li>That periodic orders scheduled for today are properly processed</li>
 *   <li>That periodic orders scheduled for other days are not processed</li>
 *   <li>That the correct number of items are added to inventory based on order quantities</li>
 *   <li>That the service correctly reports whether any orders were processed</li>
 * </ul>
 * 
 * <p>These tests are essential for ensuring that the automated ordering system works
 * correctly, as periodic orders are a critical component of the inventory management
 * system that helps maintain proper stock levels without manual intervention.</p>
 * 
 * @author ADSS Group AJ
 * @version 1.0
 * @since 2025-06-06 
 */
public class PeriodicOrderServiceTest {    /** Mock repository for periodic orders */
    private IPeriodicOrderRepository periodicOrderRepository;
    
    /** Mock repository for inventory items */
    private IItemRepository itemRepository;
    
    /** Mock repository for inventory orders */
    private IInventoryOrderRepository orderRepository;
    
    /** The service under test */
    private PeriodicOrderService service;

    /**
     * Set up test environment before each test execution.
     * 
     * <p>This method creates mock objects for all dependencies of the PeriodicOrderService
     * and injects them into a new instance of the service. Using mocks allows us to:
     * <ul>
     *   <li>Control the behavior of dependencies during tests</li>
     *   <li>Verify interactions between the service and its dependencies</li>
     *   <li>Isolate the service's logic from external systems</li>
     * </ul></p>
     */
    @BeforeEach
    public void setUp() {
        periodicOrderRepository = mock(IPeriodicOrderRepository.class);
        itemRepository = mock(IItemRepository.class);
        orderRepository = mock(IInventoryOrderRepository.class);

        service = new PeriodicOrderService(
                orderRepository,
                periodicOrderRepository,
                itemRepository
        );
    }    /**
     * Tests that periodic orders scheduled for the current day are properly processed.
     * 
     * <p>This test verifies that when a periodic order is scheduled for today:
     * <ul>
     *   <li>The service correctly identifies it as needing processing</li>
     *   <li>The appropriate number of inventory items are created (quantity = 2)</li>
     *   <li>The service returns true to indicate that orders were processed</li>
     * </ul></p>
     * 
     * <p>The test uses the current day of the week to ensure that the ordering logic
     * is properly based on the current date, mirroring the real-world behavior.</p>
     * 
     * @throws Exception If an error occurs during test execution
     */
    @Test
    public void givenPeriodicOrders_whenStart_thenProcessOrdersForToday() throws Exception {
        String today = LocalDate.now().getDayOfWeek().name();

        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "Supplier A", today, 300, 1,
                today, null, null, 0
        );
        when(periodicOrderRepository.getAllPeriodicOrders())
                .thenReturn(List.of(periodicOrder));

        boolean result = service.start(1);

        assert(result);
        verify(itemRepository, times(2)).addItem(any());
    }    /**
     * Tests that periodic orders scheduled for future days are not processed today.
     * 
     * <p>This test verifies that when a periodic order is scheduled for a day other than today:
     * <ul>
     *   <li>The service correctly identifies it as NOT needing processing</li>
     *   <li>No inventory items are added to the system</li>
     *   <li>The service returns false to indicate that no orders were processed</li>
     * </ul></p>
     * 
     * <p>This test is important for ensuring the service doesn't prematurely process
     * orders that are scheduled for future dates, which would lead to inventory 
     * discrepancies and potentially unnecessary stock levels.</p>
     * 
     * @throws Exception If an error occurs during test execution
     */
    @Test
    public void givenNoOrdersForToday_whenStart_thenNoProcessing() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).getDayOfWeek().name();

        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "Supplier A", tomorrow, 300, 1,
                tomorrow, null, null, 0
        );
        when(periodicOrderRepository.getAllPeriodicOrders())
                .thenReturn(List.of(periodicOrder));

        boolean result = service.start(1);

        assert(!result);
        verify(itemRepository, never()).addItem(any());
    }
}
