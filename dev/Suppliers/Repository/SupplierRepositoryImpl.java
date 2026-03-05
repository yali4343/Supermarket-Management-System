package Suppliers.Repository;

import Suppliers.DTO.SupplierDTO;
import Suppliers.Domain.Supplier;
import Suppliers.DAO.IAgreementDAO;
import Suppliers.DAO.IDiscountDAO;
import Suppliers.DAO.IProductSupplierDAO;
import Suppliers.DAO.ISupplierDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SupplierRepositoryImpl implements ISupplierRepository {

    private static final int MAX_CACHE_SIZE = 500;
    /** Identity Map: key = ID, value = Entity */
    private final HashMap<Integer, Supplier> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(HashMap.Entry<Integer, Supplier> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private final ISupplierDAO supplierDAO;
    private final IAgreementDAO agreementDAO;
    private final IProductSupplierDAO productSupplierDAO;
    private final IDiscountDAO discountDAO;

    public SupplierRepositoryImpl(ISupplierDAO supplierDAO, IAgreementDAO agreementDAO, IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO) {
        this.supplierDAO = supplierDAO;
        this.agreementDAO = agreementDAO;
        this.productSupplierDAO = productSupplierDAO;
        this.discountDAO = discountDAO;
    }


    @Override
    public void createSupplier(SupplierDTO supplierDTO) throws SQLException {
        this.supplierDAO.insert(supplierDTO);
    }

    @Override
    public void deleteSupplier(int supplier_ID) {
        try {
            // Delete dependencies first
            discountDAO.deleteBySupplier(supplier_ID);
            productSupplierDAO.deleteAllProductsFromSupplier(supplier_ID);
            agreementDAO.deleteBySupplierID(supplier_ID);

            // Finally delete the supplier itself
            supplierDAO.deleteById(supplier_ID);
            cache.remove(supplier_ID);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete supplier with ID: " + supplier_ID, e);
        }
    }
    @Override
    public SupplierDTO getSupplierById(int id) throws SQLException {
        return this.supplierDAO.getById(id);
    }

    @Override
    public void updateSupplier(SupplierDTO supplierDTO) throws SQLException {
        this.supplierDAO.update(supplierDTO);
        cache.remove(supplierDTO.getSupplier_id()); // Remove from cache since entity state changed
    }


    @Override
    public void deleteAllAgreementFromSupplier(int supplier_ID) throws SQLException {
        agreementDAO.deleteBySupplierID(supplier_ID);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();

        try {
            List<SupplierDTO> dtos = supplierDAO.getAll();

            for (SupplierDTO dto : dtos) {
                int id = dto.getSupplier_id();

                Supplier supplier = cache.get(id);
                if (supplier == null) {
                    supplier = new Supplier(
                            dto.getSupplierName(),
                            dto.getSupplier_id(),
                            dto.getCompany_id(),
                            dto.getBankAccount(),
                            dto.getPaymentMethod(),
                            dto.getPhoneNumber(),
                            dto.getPaymentCondition(),
                            dto.getEmail()
                    );
                    cache.put(id, supplier); // Add to cache if not exists
                }

                suppliers.add(supplier);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch suppliers from database", e);
        }

        return suppliers;
    }

    @Override
    public List<SupplierDTO> getAllSuppliersDTOs() throws SQLException {
        return supplierDAO.getAll();
    }
}
