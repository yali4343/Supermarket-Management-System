package Suppliers.Init;

import Suppliers.DAO.*;
import Suppliers.Repository.IInventoryOrderRepository;
import Suppliers.Repository.InventoryOrderRepositoryImpl;

/**
 * Responsible for initializing the supplier-side repository
 * used by inventory-supplier interactions (e.g., shortage orders).
 */
public class SupplierRepositoryInitializer {

    private final IInventoryOrderRepository orderRepository;

    public SupplierRepositoryInitializer() {
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();
        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IOrderDAO orderDAO = new JdbcOrderDAO();

        this.orderRepository = new InventoryOrderRepositoryImpl(
                productSupplierDAO,
                discountDAO,
                orderDAO,
                supplierDAO,
                agreementDAO
        );
    }

    /**
     * Returns the initialized repository that allows
     * inventory-side components to access supplier data.
     */
    public IInventoryOrderRepository getSupplierOrderRepository() {
        return orderRepository;
    }
}
