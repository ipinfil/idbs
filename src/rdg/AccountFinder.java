package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Lajcin
 * This class manages finding accounts in database and creating Account instances.
 */

public class AccountFinder extends Finder<Account> {
    private static final AccountFinder INSTANCE = new AccountFinder();

    public static AccountFinder getInstance() {
        return INSTANCE;
    }

    private AccountFinder() {
    }

    /**
     *
     * @param id - id of the Account to find
     * @return instance of Account representing a row in the database or null if there is no such row
     * @throws SQLException
     */
    public Account findById(int id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
            s.setInt(1, id);

            return loadOne(s);
        }
    }

    /**
     * @return list of Account instances representing all rows in database in table accounts
     * @throws SQLException
     */
    public List<Account> findAll() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM accounts ORDER BY id ASC")) {
            return loadAll(s);
        }
    }

    /**
     *
     * Paginated alternative to method findAll().
     * @param off - number of page to get from database
     */
    public List<Account> findAllPaginated(int off) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM accounts LIMIT 20 OFFSET ? * 20")) {
            s.setInt(1, off - 1);
            return loadAll(s);
        }
    }

    @Override
    Account load(ResultSet r) throws SQLException {
        Account acc = new Account();

        acc.setId(r.getInt("id"));
        acc.setFirstName(r.getString("first_name"));
        acc.setLastName(r.getString("last_name"));
        acc.setCredit(r.getBigDecimal("credit"));

        return acc;
    }
}
