package Suppliers.DAO;

import Suppliers.DTO.SupplierDTO;

import java.sql.SQLException;
import java.util.List;

public interface ISupplierDAO {
    void insert(SupplierDTO dto) throws SQLException;
    int insertAndGetID(SupplierDTO dto) throws SQLException;

    void update(SupplierDTO dto) throws SQLException;

    void deleteById(int supplierId) throws SQLException;

    SupplierDTO getById(int supplierId) throws SQLException;

    List<SupplierDTO> getAll() throws SQLException;
    int getIdByName(String name) throws SQLException;

    /**
     * Clears all records from the suppliers table
     */
    void clearTable();
}
