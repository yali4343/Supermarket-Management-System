package Suppliers.DAO;

import Suppliers.DTO.ContactDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcContactDAO implements IContactDAO {
    private static final String DB_URL = "jdbc:sqlite:suppliers.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS supplier_contacts (
                    contact_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    supplier_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    email TEXT NOT NULL,
                    role TEXT NOT NULL,
                    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE
                )
            """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Failed to create supplier_contacts table: " + e.getMessage());
        }
    }

    @Override
    public void insert(ContactDTO contact) throws SQLException {
        String sql = """
            INSERT INTO supplier_contacts (supplier_id, name, phone_number, email, role)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, contact.getSupplierId());
            pstmt.setString(2, contact.getName());
            pstmt.setString(3, contact.getPhoneNumber());
            pstmt.setString(4, contact.getEmail());
            pstmt.setString(5, contact.getRole());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    contact.setContactId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(ContactDTO contact) throws SQLException {
        String sql = """
            UPDATE supplier_contacts 
            SET name = ?, phone_number = ?, email = ?, role = ? 
            WHERE contact_id = ?
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getPhoneNumber());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getRole());
            pstmt.setInt(5, contact.getContactId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int contactId) throws SQLException {
        String sql = "DELETE FROM supplier_contacts WHERE contact_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public ContactDTO getById(int contactId) throws SQLException {
        String sql = "SELECT * FROM supplier_contacts WHERE contact_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ContactDTO contact = new ContactDTO(
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    contact.setContactId(rs.getInt("contact_id"));
                    contact.setSupplierId(rs.getInt("supplier_id"));
                    return contact;
                }
            }
        }
        return null;
    }

    @Override
    public List<ContactDTO> getContactsBySupplier(int supplierId) throws SQLException {
        List<ContactDTO> contacts = new ArrayList<>();
        String sql = "SELECT * FROM supplier_contacts WHERE supplier_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ContactDTO contact = new ContactDTO(
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    contact.setContactId(rs.getInt("contact_id"));
                    contact.setSupplierId(rs.getInt("supplier_id"));
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }
}
