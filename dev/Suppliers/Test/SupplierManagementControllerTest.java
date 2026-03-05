package Suppliers.Test;

import Suppliers.DAO.*;
import Suppliers.DTO.AgreementDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DTO.SupplierDTO;
import Suppliers.Domain.*;
import Suppliers.Repository.ISupplierRepository;
import Suppliers.Repository.SupplierRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SupplierManagementControllerTest {
    private void clearDatabase() throws SQLException {
        String DB_URL = "jdbc:sqlite:suppliers.db";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM product_supplier");
            stmt.executeUpdate("DELETE FROM discounts");
            stmt.executeUpdate("DELETE FROM agreements");
            stmt.executeUpdate("DELETE FROM suppliers");
        }
    }

    private TestContext setupContext() throws SQLException {
        clearDatabase();

        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();

        ISupplierRepository supplierRepository = new SupplierRepositoryImpl(supplierDAO, agreementDAO,productSupplierDAO, discountDAO);

        SupplierManagementController supplierManagementController = new SupplierManagementController(supplierRepository);

        return new TestContext(supplierDAO, agreementDAO, productSupplierDAO, discountDAO, supplierManagementController);
    }

    static class TestContext {
        public final ISupplierDAO supplierDAO;
        public final IAgreementDAO agreementDAO;
        public final IProductSupplierDAO productSupplierDAO;
        public final IDiscountDAO discountDAO;
        public final SupplierManagementController supplierManagementController;

        public TestContext(ISupplierDAO supplierDAO, IAgreementDAO agreementDAO, IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO,SupplierManagementController supplierManagementController) {
            this.supplierDAO = supplierDAO;
            this.agreementDAO = agreementDAO;
            this.productSupplierDAO = productSupplierDAO;
            this.discountDAO = discountDAO;
            this.supplierManagementController = supplierManagementController;
        }
    }

    @Test
    public void givenNewSupplier_whenCreate_thenCanBeRetrievedFromDAO() throws SQLException {
        TestContext ctx = setupContext();

        SupplierDTO supplierDTO = new SupplierDTO("MAOR", 1234, 0, "Cash", "Prepaid", 5551234, "data@mail.com");
        ctx.supplierManagementController.createSupplier(supplierDTO);


        int supplierID = ctx.supplierDAO.getIdByName("MAOR");
        SupplierDTO retrievedSupplier = ctx.supplierDAO.getById(supplierID);

        assertNotNull(retrievedSupplier);
        assertEquals("MAOR", retrievedSupplier.getSupplierName());
        assertEquals("Cash", retrievedSupplier.getPaymentMethod());
        assertEquals("Prepaid" , retrievedSupplier.getPaymentCondition());
        assertEquals("data@mail.com", retrievedSupplier.getEmail());
        assertEquals(supplierID, retrievedSupplier.getSupplier_id());
    }

    @Test
    public void givenSupplierExists_whenDelete_thenSupplierRemoved() throws SQLException {
        TestContext ctx = setupContext();

        SupplierDTO supplier = new SupplierDTO("TO_DELETE", 9999, 1, "Bank", "Net", 1111111, "del@mail.com");
        ctx.supplierManagementController.createSupplier(supplier);

        int supplierID = ctx.supplierDAO.getIdByName("MAOR");

        ctx.supplierManagementController.deleteSupplier(supplierID);

        SupplierDTO retrieved = ctx.supplierDAO.getById(supplierID);
        assertNull(retrieved);
    }

    @Test
    public void givenSupplierAndAgreementAndProductExists_whenDelete_thenAllSupplierDataRemoved() throws SQLException {
        TestContext ctx = setupContext();

        int supplierID = ctx.supplierDAO.insertAndGetID(new SupplierDTO("MAOR", 1234, 0, "Cash", "Prepaid", 5551234, "mail@a.com"));
        int agreementID = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID, new String[]{"Mon", "Wed", "Fri"}, false));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,34, supplierID, agreementID, 6.5, "L"));

        ctx.supplierManagementController.deleteSupplier(supplierID);

        SupplierDTO retrieved = ctx.supplierDAO.getById(supplierID);
        assertNull(retrieved);

        List<AgreementDTO> agreementRetrieved = ctx.agreementDAO.getBySupplierId(supplierID);
        assertTrue(agreementRetrieved.isEmpty()); //

        ProductSupplierDTO productRetrieved = ctx.productSupplierDAO.getOneProductByProductID(1204, supplierID);
        assertNull(productRetrieved);
    }

}
