package Suppliers.Domain;

import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.Repository.IProductSupplierRepository;

import java.sql.SQLException;
import java.util.List;

public class ProductSupplierManagementController {
    private final IProductSupplierRepository psRepository;

    public ProductSupplierManagementController(IProductSupplierRepository psRepository) {
        this.psRepository = psRepository;
    }

    public void createProductSupplier(ProductSupplierDTO productSupplierDTO) throws SQLException {
        psRepository.createProductSupplier(productSupplierDTO);
    }

    public void deleteProductFromAgreement(int productID, int catalogNumber, int supplierID, int agreementID ) throws SQLException {
        psRepository.deleteProductSupplier(productID, catalogNumber,  supplierID, agreementID );
    }

    public ProductSupplierDTO getProductSupplier(int productID, int catalogNumber, int supplierID) throws SQLException {
        return psRepository.getProductSupplier(productID, catalogNumber, supplierID);
    }

    public List<ProductSupplierDTO> getProductSuppliers( int supplierID , int agreementID) throws SQLException {
        return psRepository.getProductDTOsFromAgreement( supplierID, agreementID);
    }

    public void setProductPrice(int productID, int catalogNumber, int supplierID, double  newPrice) throws SQLException {
        psRepository.setProductPrice(productID, catalogNumber, supplierID, newPrice);
    }

    public void updateUnit(int productID, int catalogNumber, int agreementID, String newUnit) throws SQLException {
        psRepository.updateProductUnit( catalogNumber, newUnit, agreementID);
    }

    public void addOrUpdateDiscount(DiscountDTO discountDTO) throws SQLException {
        psRepository.updateOrAddDiscountRule(discountDTO);
    }

}

