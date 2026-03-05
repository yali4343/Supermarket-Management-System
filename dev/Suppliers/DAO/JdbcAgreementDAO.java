
package Suppliers.DAO;

import Suppliers.DTO.AgreementDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcAgreementDAO implements IAgreementDAO {

    private static final String DB_URL = "jdbc:sqlite:suppliers.db";    public void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // First, check if the table exists and if it has the unique constraint
            boolean needsRecreation = false;
            try {
                ResultSet rs = stmt.executeQuery("PRAGMA table_info(agreements)");
                if (!rs.next()) {
                    // Table doesn't exist, create it with unique constraint
                    needsRecreation = false;
                } else {
                    // Table exists, check if unique constraint on supplier_id, self_pickup, delivery_days exists
                    ResultSet indexRs = stmt.executeQuery("PRAGMA index_list(agreements)");
                    boolean hasUniqueConstraint = false;
                    while (indexRs.next()) {
                        String indexName = indexRs.getString("name");
                        boolean isUnique = indexRs.getBoolean("unique");
                        if (isUnique) {
                            // Check if this unique index covers all three columns
                            ResultSet indexInfoRs = stmt.executeQuery("PRAGMA index_info(" + indexName + ")");
                            int columnCount = 0;
                            boolean hasSupplierID = false, hasSelfPickup = false, hasDeliveryDays = false;
                            while (indexInfoRs.next()) {
                                String columnName = indexInfoRs.getString("name");
                                columnCount++;
                                if ("supplier_id".equals(columnName)) hasSupplierID = true;
                                if ("self_pickup".equals(columnName)) hasSelfPickup = true;
                                if ("delivery_days".equals(columnName)) hasDeliveryDays = true;
                            }
                            indexInfoRs.close();
                            if (columnCount == 3 && hasSupplierID && hasSelfPickup && hasDeliveryDays) {
                                hasUniqueConstraint = true;
                                break;
                            }
                        }
                    }
                    indexRs.close();
                    
                    if (!hasUniqueConstraint) {
                        needsRecreation = true;
                    }
                }
                rs.close();
            } catch (SQLException e) {
                // If we can't check, assume we need to create the table
                needsRecreation = false;
            }
            
            if (needsRecreation) {
                // Backup existing data
                List<AgreementDTO> existingAgreements = new ArrayList<>();
                try {
                    existingAgreements = getAllAgreement();
                } catch (SQLException e) {
                    System.err.println("Warning: Could not backup existing agreement data: " + e.getMessage());
                }
                
                // Drop and recreate table
                stmt.execute("DROP TABLE IF EXISTS agreements");
                
                String sql = "CREATE TABLE agreements (\n"
                        + " agreement_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + " supplier_id INTEGER NOT NULL,\n"
                        + " self_pickup BOOLEAN,\n"
                        + " delivery_days TEXT,\n"
                        + " UNIQUE(supplier_id, self_pickup, delivery_days)\n"
                        + ");";
                
                stmt.execute(sql);
                
                // Restore data (removing duplicates by supplier_id, self_pickup, delivery_days combination)
                if (!existingAgreements.isEmpty()) {
                    // Use a set to track agreement combinations we've already added
                    java.util.Set<String> addedCombinations = new java.util.HashSet<>();
                    for (AgreementDTO agreement : existingAgreements) {
                        String combination = agreement.getSupplier_ID() + "|" + 
                                           agreement.isSelfPickup() + "|" + 
                                           String.join(",", agreement.getDeliveryDays());
                        if (!addedCombinations.contains(combination)) {
                            try {
                                insert(agreement);
                                addedCombinations.add(combination);
                            } catch (SQLException e) {
                                System.err.println("Warning: Could not restore agreement for supplier " + 
                                                 agreement.getSupplier_ID() + ": " + e.getMessage());
                            }
                        } else {
                            System.out.println("Skipped duplicate agreement: Supplier ID " + 
                                             agreement.getSupplier_ID() + " with same terms");
                        }
                    }
                }
                System.out.println("✅ Agreements table recreated with unique constraint on (supplier_id, self_pickup, delivery_days).");
            } else {
                // Create table normally if it doesn't exist
                String sql = "CREATE TABLE IF NOT EXISTS agreements (\n"
                        + " agreement_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + " supplier_id INTEGER NOT NULL,\n"
                        + " self_pickup BOOLEAN,\n"
                        + " delivery_days TEXT,\n"
                        + " UNIQUE(supplier_id, self_pickup, delivery_days)\n"
                        + ");";

                stmt.execute(sql);
            }

        } catch (SQLException e) {
            System.err.println("Error while creating agreements table:");
            e.printStackTrace();
        }
    }



    @Override
    public void insert(AgreementDTO dto) throws SQLException {
        String sql = "INSERT INTO agreements (supplier_id, self_pickup, delivery_days) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db")) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, dto.getSupplier_ID());
                pstmt.setBoolean(2, dto.isSelfPickup());
                pstmt.setString(3, String.join(",", dto.getDeliveryDays()));
                pstmt.executeUpdate();
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    dto.setAgreement_ID(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public int insertAndGetID(AgreementDTO dto) throws SQLException {
        String sql = "INSERT INTO agreements (supplier_id, self_pickup, delivery_days) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db")) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, dto.getSupplier_ID());
                pstmt.setBoolean(2, dto.isSelfPickup());
                pstmt.setString(3, String.join(",", dto.getDeliveryDays()));
                pstmt.executeUpdate();
            }

            // קבל את ה-ID האחרון שהוזן
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    dto.setAgreement_ID(generatedId);
                    return generatedId;
                }
            }
        }

        return 0;
    }


    @Override
    public void update(AgreementDTO dto) throws SQLException {
        // מימוש בעתיד אם תרצה עדכון כולל
    }

    @Override
    public void deleteById(int agreementId) throws SQLException {
        String sql = "DELETE FROM agreements WHERE agreement_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteBySupplierID(int supplier_ID) throws SQLException {
        String sql = "DELETE FROM agreements WHERE supplier_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplier_ID);
            pstmt.executeUpdate();
        }
    }


    @Override
    public AgreementDTO getById(int agreementId) throws SQLException {
        String sql = "SELECT * FROM agreements WHERE agreement_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AgreementDTO dto = new AgreementDTO();
                    dto.setAgreement_ID(rs.getInt("agreement_id"));
                    dto.setSupplier_ID(rs.getInt("supplier_id"));
                    dto.setSelfPickup(rs.getBoolean("self_pickup"));
                    dto.setDeliveryDays(rs.getString("delivery_days").split(","));
                    return dto;
                }
            }
        }

        return null;
    }

    @Override
    public List<AgreementDTO> getBySupplierId(int supplierId) throws SQLException {
        String sql = "SELECT * FROM agreements WHERE supplier_id = ?";
        List<AgreementDTO> agreements = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AgreementDTO dto = new AgreementDTO();
                    dto.setAgreement_ID(rs.getInt("agreement_id"));
                    dto.setSupplier_ID(rs.getInt("supplier_id"));
                    dto.setSelfPickup(rs.getBoolean("self_pickup"));
                    dto.setDeliveryDays(rs.getString("delivery_days").split(","));
                    agreements.add(dto);
                }
            }
        }

        return agreements;
    }

    @Override
    public void updateDeliveryDays(int agreementId, String[] deliveryDays) throws SQLException {
        String sql = "UPDATE agreements SET delivery_days = ? WHERE agreement_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.join(",", deliveryDays));
            pstmt.setInt(2, agreementId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateSelfPickup(int agreementId, boolean selfPickup) throws SQLException {
        String sql = "UPDATE agreements SET self_pickup = ? WHERE agreement_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, selfPickup);
            pstmt.setInt(2, agreementId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<AgreementDTO> getAllAgreement() throws SQLException {
        String sql = "SELECT * FROM agreements";
        List<AgreementDTO> agreements = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AgreementDTO dto = new AgreementDTO();
                dto.setAgreement_ID(rs.getInt("agreement_id"));
                dto.setSupplier_ID(rs.getInt("supplier_id"));
                dto.setSelfPickup(rs.getBoolean("self_pickup"));
                dto.setDeliveryDays(rs.getString("delivery_days").split(","));
                agreements.add(dto);
            }
        }

        return agreements;
    }


    public void clearTable() {
        String sql = "DELETE FROM agreements";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Cleared all records from 'agreements' table.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear 'agreements' table: " + e.getMessage());
        }
    }

//    @Override
//    public List<Integer> getAgreementsIDBySupplierId(int supplierId) throws SQLException {
//        String sql = "SELECT agreement_id FROM agreements WHERE supplier_id = ?";
//        List<Integer> agreementsIDs = new ArrayList<>();
//
//        try (Connection conn = DriverManager.getConnection(DB_URL);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, supplierId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    agreementsIDs.add(rs.getInt("agreement_id"));
//                }
//            }
//        }
//
//        return agreementsIDs;
//    }

}
