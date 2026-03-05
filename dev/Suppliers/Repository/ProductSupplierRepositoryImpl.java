package Suppliers.Repository;

import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DAO.IDiscountDAO;
import Suppliers.DAO.IProductSupplierDAO;

import java.sql.SQLException;
import java.util.*;

public class ProductSupplierRepositoryImpl implements IProductSupplierRepository {

    private static final int MAX_CACHE_SIZE = 500;

    /** Identity Map: key = supplierID-productID-catalogNumber, value = DTO */
    private final Map<String, ProductSupplierDTO> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ProductSupplierDTO> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private final IProductSupplierDAO productSupplierDAO;
    private final IDiscountDAO discountDAO;

    public ProductSupplierRepositoryImpl(IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO) {
        this.productSupplierDAO = productSupplierDAO;
        this.discountDAO = discountDAO;
    }

    private String buildCacheKey(int supplierID, int productID, int catalogNumber) {
        return supplierID + "-" + productID + "-" + catalogNumber;
    }

    @Override
    public void createProductSupplier(ProductSupplierDTO productSupplierDTO) throws SQLException {
        productSupplierDAO.insert(productSupplierDTO);
        String key = buildCacheKey(productSupplierDTO.getSupplierID(), productSupplierDTO.getProduct_id(), productSupplierDTO.getCatalog_Number());
        cache.put(key, productSupplierDTO);
    }

    @Override
    public void updateProductSupplier(ProductSupplierDTO productSupplierDTO) throws SQLException {
        productSupplierDAO.update(productSupplierDTO);
        String key = buildCacheKey(productSupplierDTO.getSupplierID(), productSupplierDTO.getProduct_id(), productSupplierDTO.getCatalog_Number());
        cache.put(key, productSupplierDTO);
    }

    @Override
    public void deleteProductSupplier(int productID, int catalogNumber, int supplierID, int agreementID) throws SQLException {
        discountDAO.deleteDiscountForProduct(productID, supplierID, agreementID);
        productSupplierDAO.deleteOneProduct(productID, catalogNumber, supplierID);
        String key = buildCacheKey(supplierID, productID, catalogNumber);
        cache.remove(key);
    }


    @Override
    public List<ProductSupplierDTO> getProductDTOsFromAgreement(int supplierID, int agreementID) throws SQLException {
        // אין שימוש ב-cache כאן כי זו שליפה מרובה (אפשר לשפר בהמשך אם צריך)
        return productSupplierDAO.getProductsByAgreement(supplierID, agreementID);
    }

    @Override
    public ProductSupplierDTO getProductSupplier(int supplierID, int productID, int catalogNumber) throws SQLException {
        String key = buildCacheKey(supplierID, productID, catalogNumber);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        ProductSupplierDTO dto = productSupplierDAO.getOneProduct(productID, catalogNumber, supplierID);
        if (dto != null) {
            cache.put(key, dto);
        }
        return dto;
    }

    @Override
    public void setProductPrice(int supplierID, int productID, int catalogNumber, double  newPrice) {
        productSupplierDAO.SetPrice(catalogNumber, newPrice, productID);
        String key = buildCacheKey(supplierID, productID, catalogNumber);
        if (cache.containsKey(key)) {
            ProductSupplierDTO cached = cache.get(key);
            cached.setPrice(newPrice);
        }
    }

    @Override
    public void updateProductUnit(int catalogNumber, String newUnit, int agreementID){
        productSupplierDAO.updateProductUnit(catalogNumber, newUnit, agreementID);
    }

    @Override
    public void updateOrAddDiscountRule(DiscountDTO discountDTO) throws SQLException {
        discountDAO.insert(discountDTO);
    }
}
