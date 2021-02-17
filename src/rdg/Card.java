package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Filip Lajcin
 * This class represents row from database in table cards.
 */

public class Card {
    Integer id;
    boolean blocked;
    Integer accountId;

    /**
     * Inserts new card into database.
     * @throws SQLException
     */
    public void insert() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("INSERT INTO cards (blocked, account_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, blocked);
            ps.setInt(2, accountId);

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }

        }
    }

    /**
     * Updates card in database according to data in member variables id, blocked, accountId.
     * @throws SQLException
     */
    public void update() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("UPDATE cards SET blocked = ?, account_id = ? WHERE id = ?")) {
            ps.setBoolean(1, blocked);
            ps.setInt(2, accountId);
            ps.setInt(3, id);

            ps.executeUpdate();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
