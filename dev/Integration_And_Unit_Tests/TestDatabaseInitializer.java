package Integration_And_Unit_Tests;

import Suppliers.DAO.*;

/**
 * Utility class for initializing the test database environment.
 * 
 * <p>This class is responsible for setting up the database schema required for
 * integration and unit tests in the supplier module. It ensures all necessary
 * tables are created before tests are run, providing a consistent and reliable
 * database environment for testing.</p>
 * 
 * <p>The initializer creates the following tables if they don't already exist:</p>
 * <ul>
 *   <li>Suppliers - For storing supplier information</li>
 *   <li>Agreements - For storing contract agreements with suppliers</li>
 *   <li>Product-Supplier relationships - For storing which products can be sourced from which suppliers</li>
 *   <li>Discounts - For storing supplier discount information</li>
 *   <li>Orders - For storing order information</li>
 * </ul>
 * 
 * <p>This class should be called at the beginning of test suites that require
 * a properly initialized database. It uses the DAO (Data Access Object) classes
 * from the Suppliers module to create the necessary tables.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @BeforeAll
 * public static void setup() {
 *     TestDatabaseInitializer.initialize();
 * }
 * }
 * </pre>
 * 
 * @author ADSS Group AJ
 * @version 1.0
 * @since 2025-06-06
 */
public class TestDatabaseInitializer {    /**
     * Initializes the test database by creating all required tables.
     * 
     * <p>This method instantiates all necessary DAO objects and calls their
     * respective table creation methods. Each DAO is responsible for creating
     * its own table with the appropriate schema. The tables are created only
     * if they don't already exist, making this method safe to call multiple
     * times without side effects.</p>
     * 
     * <p>The method creates the following tables in sequence:</p>
     * <ol>
     *   <li>Suppliers table - Basic supplier information</li>
     *   <li>Agreements table - Supply agreements between the company and suppliers</li>
     *   <li>Product-Supplier table - Many-to-many relationship between products and suppliers</li>
     *   <li>Discounts table - Volume-based discount information from suppliers</li>
     *   <li>Orders table - Order history and pending orders</li>
     * </ol>
     * 
     * <p>Note: This method does not populate the tables with test data. 
     * For that purpose, additional methods or test setup code should be used.</p>
     */
    public static void initialize() {
        // Initialize all tables first
        JdbcSupplierDAO supplierDAO = new JdbcSupplierDAO();
        JdbcAgreementDAO agreementDAO = new JdbcAgreementDAO();
        JdbcProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        JdbcDiscountDAO discountDAO = new JdbcDiscountDAO();
        JdbcOrderDAO orderDAO = new JdbcOrderDAO();

        // Create tables
        supplierDAO.createTableIfNotExists();
        agreementDAO.createTableIfNotExists();
        productSupplierDAO.createProductSupplierTableIfNotExists();
        discountDAO.createTableIfNotExists();
        orderDAO.createTableIfNotExists();
    }
}
