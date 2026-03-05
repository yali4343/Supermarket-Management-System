
package Suppliers.Test;

import Suppliers.DAO.*;
import Inventory.DTO.InventoryProductPeriodic;
import Suppliers.DTO.*;
import Suppliers.Domain.*;
import Suppliers.Repository.IInventoryOrderRepository;
import Suppliers.Repository.InventoryOrderRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrderFromInventoryControllersTest {

    private void clearDatabase() throws SQLException {
        String DB_URL = "jdbc:sqlite:suppliers.db";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM product_supplier");
            stmt.executeUpdate("DELETE FROM discounts");
            stmt.executeUpdate("DELETE FROM agreements");
            stmt.executeUpdate("DELETE FROM suppliers");
            stmt.executeUpdate("DELETE FROM orders");
        }
    }

    private TestContext setupContext() throws SQLException {
        clearDatabase();

        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();
        IOrderDAO orderDAO = new JdbcOrderDAO();

        IInventoryOrderRepository orderRepository = new InventoryOrderRepositoryImpl(productSupplierDAO, discountDAO, orderDAO, supplierDAO, agreementDAO);

        PeriodicOrderController periodicOrderController = new PeriodicOrderController(orderRepository);
        OrderByShortageController shortageController = new OrderByShortageController(orderRepository);

        return new TestContext(supplierDAO, agreementDAO, productSupplierDAO, discountDAO, periodicOrderController, shortageController);
    }

    static class TestContext {
        public final ISupplierDAO supplierDAO;
        public final IAgreementDAO agreementDAO;
        public final IProductSupplierDAO productSupplierDAO;
        public final IDiscountDAO discountDAO;
        public final PeriodicOrderController periodicOrderController;
        public final OrderByShortageController shortageController;

        public TestContext(ISupplierDAO supplierDAO, IAgreementDAO agreementDAO, IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO, PeriodicOrderController periodicOrderController, OrderByShortageController shortageController) {
            this.supplierDAO = supplierDAO;
            this.agreementDAO = agreementDAO;
            this.productSupplierDAO = productSupplierDAO;
            this.discountDAO = discountDAO;
            this.periodicOrderController = periodicOrderController;
            this.shortageController = shortageController;
        }
    }

    @Test
    public void givenValidData_whenGetPeriodicOrderProductDetails_thenReturnExpectedResults() throws SQLException {
        TestContext ctx = setupContext();

        int supplierID = ctx.supplierDAO.insertAndGetID(new SupplierDTO("MAOR", 1234, 0, "Cash", "Prepaid", 5551234, "mail@a.com"));
        int agreementID = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID, new String[]{"Mon", "Wed", "Fri"}, false));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO(1204,34, supplierID, agreementID, 6.5, "L"));
        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID, agreementID, 20, 10.0));

        InventoryProductPeriodic product = new InventoryProductPeriodic(supplierID, agreementID, 1204, 30);
        List<OrderProductDetailsDTO> result = ctx.periodicOrderController.getPeriodicOrderProductDetails(List.of(product), 50222819);

        assertEquals(1, result.size());
        OrderProductDetailsDTO detail = result.get(0);
        assertEquals(supplierID, detail.getSupplierId());
        assertEquals(6.5, detail.getPrice(), 0.0001);
        assertEquals("MAOR", detail.getSupplierName());
        assertArrayEquals(new String[]{"Mon", "Wed", "Fri"}, detail.getDeliveryDays());
        assertEquals(10.0, detail.getDiscount());

    }

    @Test
    public void givenSameProductWithTwoDifferentDiscounts_whenGetShortageOrderProductDetails_thenReturnBestDiscount() throws SQLException {
        TestContext ctx = setupContext();

        int supplierID1 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S1", 1000, 0, "Cash", "Prepaid", 1111, "s1@mail.com"));
        int supplierID2 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S2", 1001, 1, "Bank", "Order", 2222, "s2@mail.com"));

        int agreement1 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID1, new String[]{"Mon"}, false));
        int agreement2 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID2, new String[]{"Tue"}, false));

        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,11, supplierID1, agreement1, 6.5, "L"));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,22, supplierID2, agreement2, 6.5, "L"));

        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID1, agreement1, 10, 8.0));
        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID2, agreement2, 10, 12.0));

        HashMap<Integer, Integer> products = new HashMap<>();
        products.put(1204, 15);

        List<OrderProductDetailsDTO> result = ctx.shortageController.getShortageOrderProductDetails(products, 50222819);
        OrderProductDetailsDTO detail = result.get(0);
        assertEquals(supplierID2, detail.getSupplierId());
        assertEquals(6.5, detail.getPrice(), 0.0001);
        assertEquals("S2", detail.getSupplierName());
        assertArrayEquals(new String[]{"Tue"}, detail.getDeliveryDays());
        assertEquals(12.0, detail.getDiscount());
    }

    @Test
    public void givenCheapestSupplierHasNoDiscount_whenGetShortageOrderProductDetails_thenReturnCheapestPrice() throws SQLException {
        TestContext ctx = setupContext();

        int supplierID1 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S1", 1000, 0, "Cash", "Prepaid", 1111, "s1@mail.com"));
        int supplierID2 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S2", 1001, 1, "Bank", "Order", 2222, "s2@mail.com"));

        int agreement1 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID1, new String[]{"Mon"}, false));
        int agreement2 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID2, new String[]{"Tue"}, false));

        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,11, supplierID1, agreement1, 6.5, "L"));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,22, supplierID2, agreement2, 0.5, "L"));

        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID1, agreement1, 10, 10.0));

        HashMap<Integer, Integer> products = new HashMap<>();
        products.put(1204, 5);

        List<OrderProductDetailsDTO> result = ctx.shortageController.getShortageOrderProductDetails(products, 50222819);
        assertEquals(supplierID2, result.get(0).getSupplierId());
        assertEquals(0.0, result.get(0).getDiscount());
    }


    @Test
    public void givenValidShortage_whenSavingShortageOrder_thenInsertSucceeds() throws SQLException {
        TestContext ctx = setupContext();

        // יצירת ספק, הסכם, מוצר, הנחה
        int supplierId = ctx.supplierDAO.insertAndGetID(new SupplierDTO("SUP", 111, 0, "Cash", "Prepaid", 1234, "sup@mail.com"));
        int agreementId = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierId, new String[]{"Mon", "Thu"}, false));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO(1204, 777, supplierId, agreementId, 5.0, "kg"));
        ctx.discountDAO.insert(new DiscountDTO(1204, supplierId, agreementId, 10, 15.0));

        // יצירת קלט להזמנה לפי חוסרים
        HashMap<Integer, Integer> shortageProducts = new HashMap<>();
        shortageProducts.put(1204, 12); // כמות מעל סף ההנחה

        long phoneNumber = 500000000;        // קריאה לשירות שמחזיר פרטי הזמנה
        List<OrderProductDetailsDTO> orderDetails = ctx.shortageController.getShortageOrderProductDetails(shortageProducts, phoneNumber);

        // Verify the actual order details match what was provided
        assertEquals(1, orderDetails.size());
        OrderProductDetailsDTO detail = orderDetails.get(0);
        assertEquals(1204, detail.getProductId());
        assertEquals(12, detail.getQuantity());
        assertEquals(5.0, detail.getPrice(), 0.0001);
        assertEquals(15.0, detail.getDiscount());
        assertEquals("SUP", detail.getSupplierName());
        assertArrayEquals(new String[]{"Mon", "Thu"}, detail.getDeliveryDays());

        // שליפת ההזמנות שנשמרו ובדיקת נכונות
        List<OrderDTO> orders = new JdbcOrderDAO().getAll();
        assertEquals(1, orders.size());
        OrderDTO order = orders.get(0);
        assertEquals(phoneNumber, order.getPhoneNumber());
    }

}
