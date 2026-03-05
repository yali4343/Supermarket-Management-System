package Suppliers.Repository;

import Suppliers.DTO.AgreementDTO;
import Suppliers.Domain.Agreement;
import Suppliers.DAO.IAgreementDAO;
import Suppliers.DAO.IDiscountDAO;
import Suppliers.DAO.IProductSupplierDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AgreementRepositoryImpl implements IAgreementRepository {

    private static final int MAX_CACHE_SIZE = 500;
    /** Identity Map: key = ID, value = Entity */
    private final HashMap<Integer, Agreement> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(HashMap.Entry<Integer, Agreement> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private final IAgreementDAO agreementDao;
    private final IProductSupplierDAO productSupplierDao;
    private final IDiscountDAO discountDao;



    public AgreementRepositoryImpl(IAgreementDAO dao, IProductSupplierDAO productSupplierDao, IDiscountDAO discountDao ) {
        this.agreementDao = dao;
        this.productSupplierDao = productSupplierDao;
        this.discountDao = discountDao;
    }

    @Override
    public void createAgreementWithSupplier(AgreementDTO agreementDTO) {
        try {
            agreementDao.insert(agreementDTO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Agreement getAgreementByID(int id) {
        // 1. First search in cache
        Agreement cached = cache.get(id);
        if (cached != null) return cached;

        // 2. Not found? Get from DB via DAO
        try {
            AgreementDTO dto = this.agreementDao.getById(id);
            if (dto == null) return null;                    // Not found in DB
            Agreement entity = new Agreement(dto.getAgreement_ID(), dto.getSupplier_ID(), dto.getDeliveryDays(), dto.isSelfPickup());
            cache.put(id, entity);                           // Add to cache
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    @Override
    public void deleteAgreementWithSupplier(int agreementId) {
        try {
            // Delete all discounts associated with the agreement
            discountDao.deleteByAgreement(agreementId);

            // Delete all supplier products associated with the agreement
            productSupplierDao.deleteAllProductsFromAgreement(agreementId);

            // Delete the agreement itself
            agreementDao.deleteById(agreementId);

            // Clear from cache
            cache.remove(agreementId);
        } catch (SQLException e) {
            throw new RuntimeException(" Failed to delete agreement with ID: " + agreementId, e);
        }
    }


    @Override
    public List<AgreementDTO> getBySupplierId(int supplierId) {
        List<AgreementDTO> agreementDTOList;
        try {
            agreementDTOList = agreementDao.getBySupplierId(supplierId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return agreementDTOList;
    }

    @Override
    public void updateDeliveryDays(int agreementId, String[] deliveryDays) {
        try {
            agreementDao.updateDeliveryDays(agreementId, deliveryDays);
            if (cache.containsKey(agreementId)) {
                cache.get(agreementId).setDeliveryDays(deliveryDays);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    @Override
    public void updateSelfPickup(int agreementId, boolean selfPickup) {
        try {
            agreementDao.updateSelfPickup(agreementId, selfPickup);
            if (cache.containsKey(agreementId)) {
                cache.get(agreementId).setSelfPickup(selfPickup);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }

    }

    @Override
    public List<Agreement> getAllAgreement() {
        try {
            List<AgreementDTO> dtos = agreementDao.getAllAgreement();
            List<Agreement> agreements = new ArrayList<>();

            for (AgreementDTO dto : dtos) {
                Agreement agreement = new Agreement(dto.getAgreement_ID(), dto.getSupplier_ID(), dto.getDeliveryDays(), dto.isSelfPickup());
                agreements.add(agreement);

                // Save in Cache
                cache.put(agreement.getAgreement_ID(), agreement);
            }

            return agreements;
        } catch (SQLException e) {
            throw new RuntimeException("error", e);
        }
    }
}
