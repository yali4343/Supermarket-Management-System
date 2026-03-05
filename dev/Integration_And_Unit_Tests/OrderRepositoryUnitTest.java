package Integration_And_Unit_Tests;

import Suppliers.DTO.OrderDTO;
import Suppliers.DTO.OrderItemDTO;
import Suppliers.DAO.IOrderDAO;
import Suppliers.DAO.JdbcOrderDAO;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit test class for Order Repository functionality.
 * Tests CRUD operations and search capabilities of the order data access layer.
 */
public class OrderRepositoryUnitTest {
    private static IOrderDAO orderDAO;
    private static OrderDTO testOrder;

    /**
     * Sets up the test environment before all tests.
     * Initializes the order DAO.
     */
    @BeforeAll
    public static void setUp() {
        orderDAO = new JdbcOrderDAO();
    }

    /**
     * Sets up test data before each test.
     * Creates a test order with multiple items.
     */
    @BeforeEach
    void setUpEach() throws SQLException {
        // Create a test order before each test
        testOrder = new OrderDTO(9876543210L, LocalDateTime.now(), List.of(
            new OrderItemDTO(1, 2, 1),
            new OrderItemDTO(2, 3, 1)
        ));
        orderDAO.insert(testOrder);
    }

    /**
     * Tests the retrieval of an order by its ID.
     * Verifies that order details match the inserted data.
     */
    @Test
    void testGetById() throws SQLException {
        OrderDTO retrieved = orderDAO.getById(testOrder.getOrderID());
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals(testOrder.getPhoneNumber(), retrieved.getPhoneNumber());
    }

    /**
     * Tests retrieving orders by supplier ID.
     * Verifies that all orders for a specific supplier can be found.
     */
    @Test
    void testGetBySupplierId() throws SQLException {
        List<OrderDTO> orders = orderDAO.getBySupplierId(1);
        Assertions.assertFalse(orders.isEmpty());
    }

    /**
     * Tests searching orders within a date range.
     * Verifies the search functionality returns appropriate results.
     */
    @Test
    void testSearchOrders() throws SQLException {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<OrderDTO> orders = orderDAO.searchOrders(startDate, endDate, 1);
        Assertions.assertFalse(orders.isEmpty());
    }

    /**
     * Cleans up test data after each test.
     * Removes the test order from the database.
     */
    @AfterEach
    void tearDownEach() throws SQLException {
        // Clean up the test order after each test
        orderDAO.deleteById(testOrder.getOrderID());
    }
}
