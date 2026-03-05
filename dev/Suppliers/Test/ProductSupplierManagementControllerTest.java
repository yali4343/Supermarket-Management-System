package Suppliers.Test;

import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.Domain.*;
import Suppliers.DAO.*;
import Suppliers.Repository.IProductSupplierRepository;
import Suppliers.Repository.ProductSupplierRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductSupplierManagementControllerTest {
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

        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();

        IProductSupplierRepository productSupplierRepository = new ProductSupplierRepositoryImpl(productSupplierDAO, discountDAO);

        ProductSupplierManagementController productSupplierManagementController = new ProductSupplierManagementController(productSupplierRepository);

        return new TestContext( productSupplierDAO, discountDAO, productSupplierManagementController);
    }

    static class TestContext {

        public final IProductSupplierDAO productSupplierDAO;
        public final IDiscountDAO discountDAO;
        public final ProductSupplierManagementController productSupplierManagementController;

        public TestContext(IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO,ProductSupplierManagementController productSupplierManagementController) {

            this.productSupplierDAO = productSupplierDAO;
            this.discountDAO = discountDAO;
            this.productSupplierManagementController = productSupplierManagementController;
        }
    }

    @Test
    public void givenValidProductSupplier_whenAddAndGet_thenProductSupplierExists() throws SQLException {
        TestContext ctx = setupContext();

        ProductSupplierDTO dto = new ProductSupplierDTO( 1001,1, 1, 1, 5.5, "Unit");
        ctx.productSupplierManagementController.createProductSupplier(dto);

        ProductSupplierDTO retrieved = ctx.productSupplierDAO.getOneProduct(1001, 1, 1);

        assertNotNull(retrieved);
        assertEquals(5.5, retrieved.getPrice());
        assertEquals("Unit", retrieved.getUnitsOfMeasure());
    }

    @Test
    public void givenExistingProductSupplier_whenUpdate_thenUpdatedCorrectly() throws SQLException {
        TestContext ctx = setupContext();

        // הוספת מוצר ספק חדש
        ProductSupplierDTO dto = new ProductSupplierDTO( 1002, 2,2, 2, 4.75, "Box");
        ctx.productSupplierManagementController.createProductSupplier(dto);

        // עדכון היחידה
        ctx.productSupplierManagementController.updateUnit(1002,2, 2,"Kg" );

        // שליפה מחדש
        ProductSupplierDTO retrieved = ctx.productSupplierDAO.getOneProduct(1002, 2, 2);

        assertNotNull(retrieved);
        assertEquals("Kg", retrieved.getUnitsOfMeasure());
    }


}
