package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Lajcin
 * This class manages finding years in database table years.
 */

public class YearFinder extends Finder<Year> {

    private static final YearFinder INSTANCE = new YearFinder();

    public static YearFinder getInstance() {
        return INSTANCE;
    }

    private YearFinder() {
    }

    /**
     *
     * @param id - id of year to find
     * @return Year class instance with data of row from database inserted into member variables
     * @throws SQLException
     */
    public Year findById(int id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM years WHERE id = ?")) {
            s.setInt(1, id);

            return loadOne(s);
        }
    }

    /**
     *
     * @return Year instance of last year in database.
     * @throws SQLException
     */
    public Year findLast() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM years ORDER BY value DESC LIMIT 1")) {
            return loadOne(s);
        }
    }

    /**
     *
     * @return List of all years
     * @throws SQLException
     */
    public List<Year> findAll() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM years ORDER BY value")) {
            return loadAll(s);
        }
    }

    /**
     *
     * @param val - value of year (e. g. 2020)
     * @return Year instance
     * @throws SQLException
     */
    public Year findByValue(int val) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM years WHERE value = ?")) {
            s.setInt(1, val);
            return loadOne(s);
        }
    }

    @Override
    Year load(ResultSet r) throws SQLException {
        Year y = new Year();
        y.setId(r.getInt("id"));
        y.setValue(r.getInt("value"));

        return y;
    }
}
