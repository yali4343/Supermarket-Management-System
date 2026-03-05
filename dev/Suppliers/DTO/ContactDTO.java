package Suppliers.DTO;

public class ContactDTO {
    private int contactId;
    private int supplierId;
    private String name;
    private String phoneNumber;
    private String email;
    private String role;

    public ContactDTO(String name, String phoneNumber, String email, String role) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.contactId = 0; // Will be set by the database
    }

    // Getters
    public int getContactId() { return contactId; }
    public int getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    // Setters
    public void setContactId(int contactId) { this.contactId = contactId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
}
