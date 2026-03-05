package Suppliers.Domain;


/**
 * Represents a supplier in the system.
 * A supplier has basic contact and financial details and can be associated with multiple agreements.
 */
public class Supplier {
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    String paymentCondition;
    long phoneNumber;
    String email;

    /**
     * Constructs a new Supplier with all required details.
     *
     * @param supplierName      the name of the supplier
     * @param supplier_id       unique supplier ID
     * @param company_id        the ID of the associated company
     * @param bankAccount       supplier's bank account number
     * @param paymentMethod     payment method (e.g., Bank Transfer, Check)
     * @param phoneNumber       supplier contact phone number
     * @param email             supplier's email address
     * @param paymentCondition  payment condition (e.g., Prepaid, Pay at delivery)
     */
    public Supplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, long phoneNumber, String email, String paymentCondition){
        this.supplierName = supplierName;
        this.supplier_id = supplier_id;
        this.company_id = company_id;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.paymentCondition = paymentCondition;
    }

    /**
     * Returns the supplier's unique ID.
     *
     * @return the supplier ID
     */
    public int getSupplier_id() {
        return supplier_id;
    }

    public String getSupplierName() {
        return supplierName;
    }


}
