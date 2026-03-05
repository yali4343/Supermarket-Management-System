package Suppliers.DTO;

import java.util.ArrayList;
import java.util.List;

public class SupplierDTO {
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    String paymentCondition;
    long phoneNumber;
    String email;
    boolean isActive;
    List<ContactDTO> contacts;    public SupplierDTO(String supplierName, int company_id, int bankAccount, String paymentMethod, String paymentCondition, long phoneNumber, String email) {
        this.supplierName = supplierName;
        this.supplier_id = 0;
        this.company_id = company_id;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.paymentCondition = paymentCondition;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isActive = true; // New suppliers are active by default
        this.contacts = new ArrayList<>();
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public int getBankAccount() {
        return bankAccount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentCondition() {
        return paymentCondition;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return isActive;
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public void setBankAccount(int bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentCondition(String paymentCondition) {
        this.paymentCondition = paymentCondition;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }

    public void addContact(ContactDTO contact) {
        if (this.contacts == null) {
            this.contacts = new ArrayList<>();
        }
        contact.setSupplierId(this.supplier_id);
        this.contacts.add(contact);
    }

    public void removeContact(int contactId) {
        if (this.contacts != null) {
            this.contacts.removeIf(c -> c.getContactId() == contactId);
        }
    }
}
