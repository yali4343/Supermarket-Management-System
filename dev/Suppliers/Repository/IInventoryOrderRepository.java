package Suppliers.Repository;

import Suppliers.DTO.*;

import java.sql.SQLException;
import java.util.List;

public interface IInventoryOrderRepository {


     List<ProductSupplierDTO> getProductsByProductID(int productId) throws SQLException;

     List<DiscountDTO> getDiscountsForProductByID (int productId, int quantity) throws SQLException;

     ProductSupplierDTO getCheapestProductSupplier(int productId);

     SupplierDTO getSupplierByID(int supplierID) throws SQLException;

     ProductSupplierDTO getProductByProductIDSupplierID(int productId, int supplierId) throws SQLException;

     AgreementDTO getAgreementByID(int agreementID) throws SQLException;

    DiscountDTO getBestMatchingDiscount(int productId, int supplierId, int agreementId, int quantity) throws SQLException;

    void createOrder(OrderDTO order) throws SQLException;




}

