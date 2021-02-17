package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Filip Lajcin
 * This class manages finding operations in database.
 */

public class OperationFinder extends Finder<Operation> {
    private static final OperationFinder INSTANCE = new OperationFinder();

    public static OperationFinder getInstance() {
        return INSTANCE;
    }

    private OperationFinder() {
    }

    /**
     *
     * @param id - id of Operation to find
     * @return Operation instance with certain id
     * @throws SQLException
     */
    public Operation findById(int id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM operations WHERE id = ?")) {
            s.setInt(1, id);
            return loadOne(s);
        }
    }

    /**
     *
     * @param n - number of Operations to find
     * @return List of last n Operation instances.
     * @throws SQLException
     */
    public List<Operation> findLastN(int n) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM operations ORDER BY id DESC LIMIT ?")) {
            s.setInt(1, n);
            return loadAll(s);
        }
    }


    @Override
    Operation load(ResultSet r) throws SQLException {
        Operation op = new Operation();
        op.setId(r.getInt("id"));
        op.setAccId(r.getInt("account_id"));
        op.setAmount(r.getBigDecimal("amount"));
        return op;
    }
}
