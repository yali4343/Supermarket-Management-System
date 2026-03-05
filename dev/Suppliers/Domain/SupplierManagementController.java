package Suppliers.Domain;

import Suppliers.DTO.SupplierDTO;
import Suppliers.Repository.ISupplierRepository;

import java.sql.SQLException;
import java.util.List;

public class SupplierManagementController {

    private final ISupplierRepository supplierRepository;

    public SupplierManagementController(ISupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public void createSupplier(SupplierDTO supplierDTO) throws SQLException {
        supplierRepository.createSupplier(supplierDTO);
    }

    public void deleteSupplier(int supplierId) throws SQLException {
        supplierRepository.deleteSupplier(supplierId);
    }

    public List<SupplierDTO> getAllSuppliersDTOs() throws SQLException {
        List<SupplierDTO> supplierDTOList =  supplierRepository.getAllSuppliersDTOs();
        return supplierDTOList;
    }

    public SupplierDTO getSupplierById(int supplierId) throws SQLException {
        return supplierRepository.getSupplierById(supplierId);
    }

    public void updateSupplier(SupplierDTO supplierDTO) throws SQLException {
        supplierRepository.updateSupplier(supplierDTO);
    }
}
