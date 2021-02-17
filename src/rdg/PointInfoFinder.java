package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 * @author Filip Lajcin
 * This class manages finding row from points table in database and creating Contract instances.
 */
public class PointInfoFinder extends Finder<PointInfo> {

    private static final PointInfoFinder INSTANCE = new PointInfoFinder();

    public static PointInfoFinder getInstance() {
        return INSTANCE;
    }

    private PointInfoFinder() {
    }

    /**
     *
     * @param accId - id of account whom points to find
     * @param year_id - id of year of points listing
     * @return list of pointInfo from certain year of certain account
     * @throws SQLException
     */
    public List<PointInfo> findByAccYearId(int accId, int year_id) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM points WHERE year_id = ? AND account_id = ?")) {
            ps.setInt(1, year_id);
            ps.setInt(2, accId);
            return loadAll(ps);
        }
    }

    /**
     *
     * @param year_id - id of year of points listing
     * @return list of points of all students from certain year
     * @throws SQLException
     */
    public List<PointInfo> findByYearId(int year_id) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM points WHERE year_id = ?")) {
            ps.setInt(1, year_id);
            return loadAll(ps);
        }
    }

    /**
     * Paginated alternative to findByYear() method
     * @throws SQLException
     */
    public List<PointInfo> findPaginatedByYear(int year_id, Integer offset) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM points WHERE year_id = ? LIMIT 20 OFFSET ? * 20")) {
            ps.setInt(1, year_id);
            ps.setInt(2, offset - 1);
            return loadAll(ps);
        }
    }

    @Override
    PointInfo load(ResultSet rs) throws SQLException {
        PointInfo pi = new PointInfo();
        pi.setAccountId(rs.getInt("account_id"));
        pi.setAmount(rs.getFloat("amount"));
        pi.setId(rs.getInt("id"));
        pi.setYearId(rs.getInt("year_id"));

        return pi;
    }
}
