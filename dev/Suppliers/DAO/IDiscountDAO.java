package Suppliers.DAO;

import Suppliers.DTO.DiscountDTO;

import java.sql.SQLException;
import java.util.List;

public interface IDiscountDAO {

    // הוספת הנחה חדשה
    void insert(DiscountDTO discount) throws SQLException;

    // עדכון הנחה קיימת
    void update(DiscountDTO discount) throws SQLException;

    // מחיקת הנחה לפי מזהים
    void deleteByAgreement(int agreementId) throws SQLException;
    void deleteBySupplier(int SupplierID) throws SQLException;
    void deleteDiscountForProduct(int productId, int supplierId, int agreementId) throws SQLException;

    // שליפת כל ההנחות למוצר מסוים בהסכם

    // שליפת הנחה מתאימה לפי כמות
    DiscountDTO getBestDiscount(int productId, int quantity) throws SQLException;
    List<DiscountDTO> getDiscountsForProductByID(int productId, int quantity) throws SQLException;

    DiscountDTO getBestMatchingDiscount(int productId, int supplierId, int agreementId, int quantity) throws SQLException;

    void clearTable();
}
