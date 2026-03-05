package Suppliers.Repository;

import Suppliers.DAO.*;
import Suppliers.DTO.*;

import java.sql.SQLException;
import java.util.List;

public class InventoryOrderRepositoryImpl implements IInventoryOrderRepository {
    private final IProductSupplierDAO productSupplierDAO;
    private final IDiscountDAO discountDAO;
    private final IOrderDAO orderDAO;
    private final ISupplierDAO supplierDAO;
    private final IAgreementDAO agreementDAO;

    public InventoryOrderRepositoryImpl(IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO, IOrderDAO orderDAO, ISupplierDAO supplierDAO, IAgreementDAO agreementDAO){
        this.productSupplierDAO = productSupplierDAO;
        this.discountDAO = discountDAO;
        this.orderDAO = orderDAO;
        this.supplierDAO = supplierDAO;
        this.agreementDAO = agreementDAO;
    }

    @Override
    public List<ProductSupplierDTO> getProductsByProductID(int productId) throws SQLException {
        return productSupplierDAO.getProductsByProductID(productId);
    }

    @Override
    public List<DiscountDTO> getDiscountsForProductByID (int productId, int quantity) throws SQLException {
        return discountDAO.getDiscountsForProductByID(productId, quantity);
    }

    @Override
    public ProductSupplierDTO getCheapestProductSupplier(int productId){
        return productSupplierDAO.getCheapestProductSupplier(productId);
    }

    @Override
    public SupplierDTO getSupplierByID(int supplierID) throws SQLException {
        return supplierDAO.getById(supplierID);
    }

    @Override
    public ProductSupplierDTO getProductByProductIDSupplierID(int productId, int supplierId) throws SQLException {
        return productSupplierDAO.getOneProductByProductIDAndSupplierID(productId, supplierId);
    }

    @Override
    public AgreementDTO getAgreementByID(int agreementID) throws SQLException {
        return agreementDAO.getById(agreementID);
    }

    @Override
    public DiscountDTO getBestMatchingDiscount(int productId, int supplierId, int agreementId, int quantity) throws SQLException {
        return discountDAO.getBestMatchingDiscount(productId, supplierId, agreementId, quantity);
    }

    @Override
    public void createOrder(OrderDTO order) throws SQLException {
        orderDAO.insert(order);
    }
}
