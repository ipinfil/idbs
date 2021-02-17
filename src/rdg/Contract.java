package rdg;

import main.DbContext;

import java.sql.*;
import java.time.LocalDate;

/**
 * @author Filip Lajcin
 * This class represents row from contracts table in database.
 */

public class Contract {

    Integer id;
    String status;
    Integer yearId;
    LocalDate validSince;
    LocalDate validUntil;
    Integer accountId;
    Integer roomId;

    /**
     * Inserts contract into database with data from member variables.
     * @throws SQLException
     */
    public void insert() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("INSERT INTO contracts (status, year_id, valid_since, valid_until, account_id, room_id) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, status);
            ps.setInt(2, yearId);
            ps.setObject(3, validSince);
            ps.setObject(4, validUntil);
            ps.setInt(5, accountId);
            if (roomId != null) {
                ps.setInt(6, roomId);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();

            try (ResultSet r = ps.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }
        }
    }

    /**
     * Updates contract in database according to data in member variables.
     * @throws SQLException
     */
    public void update() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Contract ID is not set in this instance.");
        }
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("UPDATE contracts SET status = ?, year_id = ?, valid_since = ?, valid_until = ?, account_id = ?, room_id = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, yearId);
            ps.setObject(3, validSince);
            ps.setObject(4, validUntil);
            ps.setInt(5, accountId);
            ps.setInt(6, roomId);
            ps.setInt(7, id);

            ps.executeUpdate();
        }
    }

    /**
     * Deletes contract from database with id from member variable id.
     * @throws SQLException
     */
    public void delete() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Contract ID is not set in this instance.");
        }

        try (PreparedStatement s = DbContext.getConnection().prepareStatement("DELETE FROM contracts WHERE id = ?")) {
            s.setInt(1, id);

            s.executeUpdate();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getYearId() {
        return yearId;
    }

    public void setYearId(Integer yearId) {
        this.yearId = yearId;
    }

    public LocalDate getValidSince() {
        return validSince;
    }

    public void setValidSince(LocalDate validSince) {
        this.validSince = validSince;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
