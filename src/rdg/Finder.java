package rdg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that simplifies loading instances of every set of entities. Every EntityFinder inherits from this class,
 * and overrides load(ResultSet rs) method where it loads one instance.
 */
public abstract class Finder<T> {
    protected T loadOne(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return load(rs);
            else return null;
        }
    }

    protected List<T> loadAll(PreparedStatement ps) throws SQLException {
        List<T> result = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(load(rs));
            }
        }
        return result;
    }

    abstract T load(ResultSet rs) throws SQLException;


}
