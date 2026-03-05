package Suppliers.DAO;

import Suppliers.DTO.ContactDTO;
import java.sql.SQLException;
import java.util.List;

public interface IContactDAO {
    void insert(ContactDTO contact) throws SQLException;
    void update(ContactDTO contact) throws SQLException;
    void delete(int contactId) throws SQLException;
    ContactDTO getById(int contactId) throws SQLException;
    List<ContactDTO> getContactsBySupplier(int supplierId) throws SQLException;
}
