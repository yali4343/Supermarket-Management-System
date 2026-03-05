package Suppliers.Test;

import Suppliers.DTO.AgreementDTO;
import Suppliers.Domain.*;
import Suppliers.DAO.*;
import Suppliers.Repository.AgreementRepositoryImpl;
import Suppliers.Repository.IAgreementRepository;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgreementManagementControllerTest {
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

        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();

        IAgreementRepository agreementRepository = new AgreementRepositoryImpl(agreementDAO,productSupplierDAO, discountDAO);

        AgreementManagementController agreementManagementController = new AgreementManagementController(agreementRepository);

        return new TestContext(agreementDAO, productSupplierDAO, discountDAO, agreementManagementController);
    }

    static class TestContext {

        public final IAgreementDAO agreementDAO;
        public final IProductSupplierDAO productSupplierDAO;
        public final IDiscountDAO discountDAO;
        public final AgreementManagementController agreementManagementController;

        public TestContext(IAgreementDAO agreementDAO, IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO,AgreementManagementController agreementManagementController) {

            this.agreementDAO = agreementDAO;
            this.productSupplierDAO = productSupplierDAO;
            this.discountDAO = discountDAO;
            this.agreementManagementController = agreementManagementController;
        }
    }


    @Test
    public void givenValidAgreement_whenCreateAndFetch_thenAgreementExists() throws SQLException {
        TestContext ctx = setupContext();
        // נניח ש-ID של ספק 1 כבר קיים או מוסיפים אותו ידנית אם צריך
        int supplierId = 1;

        AgreementDTO agreementDTO = new AgreementDTO(supplierId, new String[]{"Mon", "Wed", "Fri"}, false);

        ctx.agreementManagementController.createAgreementWithSupplier(agreementDTO);

        List<AgreementDTO> agreements = ctx.agreementDAO.getBySupplierId(supplierId);

        assertNotNull(agreements);
        assertEquals(1, agreements.size());
        assertEquals(supplierId, agreements.get(0).getSupplier_ID());
        assertFalse(agreements.get(0).isSelfPickup());
    }

    @Test
    public void givenAgreementExists_whenDelete_thenAgreementIsRemoved() throws SQLException {
        TestContext ctx = setupContext();

        // נניח ש-ID של ספק 1 כבר קיים
        int supplierId = 1;

        AgreementDTO agreementDTO = new AgreementDTO(supplierId, new String[]{"Sun", "Tue"}, true);

        int agreementId = ctx.agreementDAO.insertAndGetID(agreementDTO);

        // ודא שנשמר במסד
        List<AgreementDTO> beforeDelete = ctx.agreementDAO.getBySupplierId(supplierId);
        assertEquals(1, beforeDelete.size());

        // מחיקת ההסכם
        ctx.agreementManagementController.deleteAgreementWithSupplier(agreementId,1);

        // בדיקה לאחר מחיקה
        List<AgreementDTO> afterDelete = ctx.agreementDAO.getBySupplierId(supplierId);
        assertNotNull(afterDelete);
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void givenAgreementExists_whenEditDeliveryDays_thenAgreementIsUpdated() throws SQLException {
        TestContext ctx = setupContext();

        int supplierId = 1;
        AgreementDTO agreementDTO = new AgreementDTO(supplierId, new String[]{"Sun", "Tue"}, true);
        int agreementId = ctx.agreementDAO.insertAndGetID(agreementDTO);

        ctx.agreementManagementController.setDeliveryDays(agreementId, new String[]{"Yom Shbat"});

        Agreement agreement = ctx.agreementManagementController.getAgreementByID(agreementId);
        assertNotNull(agreement);
        assertArrayEquals(new String[]{"Yom Shbat"}, agreement.getDeliveryDays());
    }




}
