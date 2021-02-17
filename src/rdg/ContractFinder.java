package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Filip Lajcin
 * This class manages finding contracts in database and creating Contract instances.
 */

public class ContractFinder extends Finder<Contract> {
    private static final ContractFinder INSTANCE = new ContractFinder();

    public static ContractFinder getInstance() {
        return INSTANCE;
    }

    private ContractFinder() {
    }

    /**
     *
     * @param id of contract to find
     * @return Contract instance representing row in database with certain id
     * @throws SQLException
     */
    public Contract findById(int id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM contracts WHERE id = ?")) {
            s.setInt(1, id);
            return loadOne(s);
        }
    }

    /**
     *
     * @param accId - id of account whom contracts to find
     * @return list of contracts of certain account
     * @throws SQLException
     */
    public List<Contract> findByAccId(int accId) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM contracts WHERE account_id = ?")) {
            s.setInt(1, accId);
            return loadAll(s);
        }
    }

    /**
     *
     * @return list of all contracts
     * @throws SQLException
     */
    public List<Contract> findAll() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM contracts")) {
            return loadAll(s);
        }
    }

    /**
     *
     * @param status - status of contracts to find
     * @return list of contracts that have certain status
     * @throws SQLException
     */
    public List<Contract> findByStatus(String status) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM contracts WHERE status IS ?")) {
            s.setString(1, status);
            return loadAll(s);
        }
    }

    /**
     *
     * @param year_id - year of contracts to find
     * @return list of contracts that have certain yearId
     * @throws SQLException
     */
    public List<Contract> findByYear(int year_id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM contracts WHERE year_id = ?")) {
            s.setInt(1, year_id);

            return loadAll(s);
        }
    }

    /**
     * Paginated alternative to findByAccId() method
     * @param offset - page to get from database
     * @return list of <= 20 contracts on certain page
     * @throws SQLException
     */
    public List<Contract> findByAccIdPaginated(int accId, int offset) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM contracts WHERE account_id = ? ORDER BY id LIMIT 20 OFFSET 20 * ?")) {
            s.setInt(1, accId);
            s.setInt(2, offset - 1);

            return loadAll(s);
        }
    }

    @Override
    Contract load(ResultSet r) throws SQLException {
        Contract c = new Contract();
        c.setId(r.getInt("id"));
        c.setStatus(r.getString("status"));
        c.setYearId(r.getInt("year_id"));
        c.setAccountId(r.getInt("account_id"));
        Integer room_id = r.getInt("room_id");
        if (room_id == 0) c.setRoomId(null);
        else c.setRoomId(r.getInt("room_id"));
        c.setValidSince(LocalDate.parse(r.getObject("valid_since").toString()));
        c.setValidUntil(LocalDate.parse(r.getObject("valid_until").toString()));
        return c;
    }
}
