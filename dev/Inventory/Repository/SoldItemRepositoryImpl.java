package Inventory.Repository;

import Inventory.DAO.JdbcSoldItemDAO;
import Inventory.DAO.ISoldItemDAO;
import Inventory.DTO.SoldItemDTO;

import java.util.List;

public class SoldItemRepositoryImpl implements ISoldItemRepository {

    private final ISoldItemDAO soldItemDAO;

    public SoldItemRepositoryImpl() {
        this.soldItemDAO = new JdbcSoldItemDAO();
    }

    @Override
    public void addSoldItem(SoldItemDTO item) {
        try {
            soldItemDAO.insert(item);
        } catch (Exception e) {
            System.err.println("❌ Failed to insert sold item: " + e.getMessage());
        }
    }

    @Override
    public List<SoldItemDTO> getSalesByCatalogAndBranch(int catalogNumber, int branchId) {
        try {
            return soldItemDAO.getSalesByCatalogAndBranch(catalogNumber, branchId);
        } catch (Exception e) {
            System.err.println("❌ Failed to retrieve sold items: " + e.getMessage());
            return List.of();
        }
    }
}
