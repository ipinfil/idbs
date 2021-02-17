package rdg;

import main.DbContext;

import java.sql.*;

/**
 * @author Filip Lajcin
 * This class represents row in database in table applications.
 */
public class Application {
    Integer id;
    int yearId;
    String status;
    int accId;
    Integer contractId;

    /**
     * Inserts application into database with data from member variables.
     * @throws SQLException
     */
    public void insert() throws SQLException{
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("INSERT INTO applications (year_id, status, account_id, contract_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, yearId);
            ps.setString(2, status);
            ps.setInt(3, accId);
            if (contractId != null) {
                ps.setInt(4, contractId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.executeUpdate();

            try (ResultSet r = ps.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }
        }
    }

    /**
     * Updates application row in database according to data from member variables.
     * @throws SQLException
     */
    public void update() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Application ID is not set in this instance.");
        }
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("UPDATE applications SET year_id = ?, status = ?, account_id = ?, contract_id = ? WHERE id = ?")) {
            ps.setInt(1, yearId);
            ps.setString(2, status);
            ps.setInt(3, accId);
            if (contractId != null) {
                ps.setInt(4, contractId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, id);

            ps.executeUpdate();
        }
    }

    /**
     * Deletes application row with id from member variable from database.
     * @throws SQLException
     */
    public void delete() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Application ID is not set in this instance.");
        }

        try (PreparedStatement s = DbContext.getConnection().prepareStatement("DELETE FROM applications WHERE id = ?")) {
            s.setInt(1, id);

            s.executeUpdate();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYearId() {
        return yearId;
    }

    public void setYearId(int yearId) {
        this.yearId = yearId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAccId() {
        return accId;
    }

    public void setAccId(int accId) {
        this.accId = accId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }
}
