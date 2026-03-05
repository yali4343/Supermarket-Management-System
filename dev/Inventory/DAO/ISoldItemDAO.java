package Inventory.DAO;

import Inventory.DTO.SoldItemDTO;

import java.sql.SQLException;
import java.util.List;

public interface ISoldItemDAO {
    void insert(SoldItemDTO item) throws SQLException;
    List<SoldItemDTO> getSalesByCatalogAndBranch(int catalogNumber, int branchId) throws SQLException;
}