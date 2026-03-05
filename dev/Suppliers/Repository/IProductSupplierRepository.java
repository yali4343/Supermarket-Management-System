package Suppliers.Repository;

import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;

import java.sql.SQLException;
import java.util.List;

public interface IProductSupplierRepository {
    void createProductSupplier(ProductSupplierDTO productSupplierDTO) throws SQLException;
    void updateProductSupplier(ProductSupplierDTO productSupplierDTO) throws SQLException;
    void deleteProductSupplier(int productID, int catalogNumber, int supplierID, int agreementID) throws SQLException;
    List<ProductSupplierDTO> getProductDTOsFromAgreement(int supplier_ID, int agreement_ID) throws SQLException;
    ProductSupplierDTO getProductSupplier(int supplierID, int productID, int CatalogNumber) throws SQLException;
    void setProductPrice(int supplierID, int productID, int CatalogNumber, double  newPrice);
    void updateProductUnit( int catalogNumber, String newUnit, int agreementID);
    void updateOrAddDiscountRule(DiscountDTO discountDTO) throws SQLException;


}
