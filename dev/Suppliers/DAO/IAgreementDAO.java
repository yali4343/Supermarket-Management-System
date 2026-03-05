package Suppliers.DAO;

import Suppliers.DTO.AgreementDTO;

import java.sql.SQLException;
import java.util.List;

public interface IAgreementDAO {

    void insert(AgreementDTO dto) throws SQLException;

    int insertAndGetID(AgreementDTO dto) throws SQLException;

    void update(AgreementDTO dto) throws SQLException;

    void deleteById(int agreementId) throws SQLException;

    AgreementDTO getById(int agreementId) throws SQLException;

    List<AgreementDTO> getBySupplierId(int supplierId) throws SQLException;

    void updateDeliveryDays(int agreementId, String[] deliveryDays) throws SQLException;

    void updateSelfPickup(int agreementId, boolean selfPickup) throws SQLException;

    List<AgreementDTO> getAllAgreement() throws SQLException;

    void deleteBySupplierID(int supplier_ID) throws SQLException;

    /**
     * Clears all records from the agreements table
     */
    void clearTable();
}
