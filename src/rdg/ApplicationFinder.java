package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 * @author Filip Lajcin
 * This class manages finding applications in database and creating Application instances.
 */
public class ApplicationFinder extends Finder<Application> {
    private static final ApplicationFinder INSTANCE = new ApplicationFinder();

    public static ApplicationFinder getInstance() {
        return INSTANCE;
    }
    private ApplicationFinder() { }

    /**
     *
     * @param id - id of row in database to find
     * @return instance of Application representing row with this id in database.
     * @throws SQLException
     */
    public Application findById(int id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications WHERE id = ?")) {
            s.setInt(1, id);
            return loadOne(s);
        }
    }


    /**
     *
     * @param acc_id - id of account whom contracts this method finds
     * @return list of all contracts of account with acc_id
     * @throws SQLException
     */
    public List<Application> findByAccId(int acc_id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications WHERE account_id = ?")) {
            s.setInt(1, acc_id);
            return loadAll(s);
        }
    }

    /**
     *
     * @return all applications in database
     * @throws SQLException
     */
    public List<Application> findAll() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications ORDER BY id ASC")) {
            return loadAll(s);
        }
    }

    /**
     *
     * @param yearId - id of the year which we find applications from
     * @return list of all aplications with certain yearId
     * @throws SQLException
     */
    public List<Application> findByYear(Integer yearId) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications WHERE year_id = ? ORDER BY id")) {
            s.setInt(1, yearId);
            return loadAll(s);
        }
    }

    /**
     * Paginated alterntive to findByYear() method
     * @param page - number of page to get from database
     * @return list of <= 20 applications on certain page
     * @throws SQLException
     */
    public List<Application> findByYearPaginated(Integer yearId, int page) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications WHERE year_id = ? ORDER BY id ASC LIMIT 20 OFFSET 20 * ?")) {
            s.setInt(1, yearId);
            s.setInt(2, page - 1);
            return loadAll(s);
        }
    }

    /**
     *
     * Paginated alterntive to findByAccId() method
     * @param offset - number of page to get from database
     * @return list of <= 20 applications on certain page
     * @throws SQLException
     */
    public List<Application> findByAccIdPaginated(int accId, int offset) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications WHERE account_id = ? ORDER BY id LIMIT 20 OFFSET 20 * ?")) {
            s.setInt(1, accId);
            s.setInt(2, offset - 1);
            return loadAll(s);
        }
    }

    /**
     *
     * @param contractId - id of the contract which we find application for
     * @return application for certain contract
     * @throws SQLException
     */
    public Application findByContractId(Integer contractId) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications WHERE contract_id = ? AND contract_id IS NOT NULL")) {
            s.setInt(1, contractId);
            return loadOne(s);
        }
    }

    /**
     *
     * @param yearId - id of the year which we find applications from
     * @param status - status of applications we find
     * @return list of all applications with certain year and status
     * @throws SQLException
     */
    public List<Application> findByYearAndStatus(Integer yearId, String status) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM applications WHERE year_id = ? AND status = ?")) {
            s.setInt(1, yearId);
            s.setString(2, status);
            return loadAll(s);
        }
    }

    @Override
    Application load(ResultSet r) throws SQLException {
        Application app = new Application();

        app.setId(r.getInt("id"));
        app.setAccId(r.getInt("account_id"));
        Integer contract_id_temp = r.getInt("contract_id");
        if (contract_id_temp == 0) {
            app.setContractId(null);
        } else app.setContractId(contract_id_temp);
        app.setStatus(r.getString("status"));
        app.setYearId(r.getInt("year_id"));
        return app;
    }
}
