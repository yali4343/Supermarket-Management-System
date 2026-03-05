package Suppliers.Domain;

import Suppliers.DTO.AgreementDTO;
import Suppliers.Repository.IAgreementRepository;

import java.util.List;

public class AgreementManagementController {

    private final IAgreementRepository agreementRepository;

    public AgreementManagementController(IAgreementRepository agreementRepository) {
        this.agreementRepository = agreementRepository;
    }

    public void createAgreementWithSupplier(AgreementDTO agreementDTO) {
        agreementRepository.createAgreementWithSupplier(agreementDTO);
    }

    public void deleteAgreementWithSupplier(int agreementId, int supplierId) {
        agreementRepository.deleteAgreementWithSupplier(agreementId);
    }

    public Agreement getAgreementByID(int id) {
        return agreementRepository.getAgreementByID(id);
    }

    public void setDeliveryDays(int agreementID, String[] deliveryDays) {
        agreementRepository.updateDeliveryDays(agreementID, deliveryDays);
    }

    public void setSelfPickup(int agreementID, boolean selfPickup) {
        agreementRepository.updateSelfPickup(agreementID, selfPickup);
    }

    public List<AgreementDTO> getAgreementsBySupplierID(int supplierId) {
        return agreementRepository.getBySupplierId(supplierId);
    }
}
