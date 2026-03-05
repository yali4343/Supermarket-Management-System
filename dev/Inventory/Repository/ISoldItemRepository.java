package Inventory.Repository;

import Inventory.DTO.SoldItemDTO;
import java.util.List;

public interface ISoldItemRepository {
    void addSoldItem(SoldItemDTO item);
    List<SoldItemDTO> getSalesByCatalogAndBranch(int catalogNumber, int branchId);
}
