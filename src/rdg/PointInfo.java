package rdg;

import main.DbContext;

import java.sql.*;

/**
 * @author Filip Lajcin
 * This class represents a row in points table in database.
 */

public class PointInfo {
    Integer id;
    float amount;
    Integer yearId;
    Integer accountId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Integer getYearId() {
        return yearId;
    }

    public void setYearId(Integer yearId) {
        this.yearId = yearId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    /**
     * Inserts new row into points table in database with data from member variables.
     * @throws SQLException
     */
    public void insert() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("INSERT INTO points (amount, year_id, account_id) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setFloat(1, amount);
            ps.setInt(2, yearId);
            ps.setInt(3, accountId);

            ps.executeUpdate();
            try (ResultSet r = ps.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1); //v pripade ze je uspesny insert, do clenskej premennej vlozim id noveho konta
            }
        }
    }

    /**
     * Updates row in database according to info from member variables.
     * @throws SQLException
     */
    public void update() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("PointInfo ID is not set in this instance.");
        }
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("UPDATE points SET amount = ?, year_id = ?, account_id = ? WHERE id = ?")) {
            ps.setFloat(1, amount);
            ps.setInt(2, yearId);
            ps.setInt(3, accountId);
            ps.setInt(4, id);

            ps.executeUpdate();
        }
    }
}
