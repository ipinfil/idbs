package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Lajcin
 * This class manages creating unpopularity statistics for rooms.
 */

public class UnpopularityFinder{
    private static final UnpopularityFinder INSTANCE = new UnpopularityFinder();

    public static UnpopularityFinder getInstance() {
        return INSTANCE;
    }

    private UnpopularityFinder() {
    }

    /**
     * Selects rooms from contracts table from rows, which status is Prematurely canceled (status that means tenant has reacommodated from this room to another one).
     * Result is ordered by count (number of reacomodations for room).
     * For example:
     *      ROOM_ID     |       COUNT
     *        12        |        38
     *       134        |        31
     * @param n - number of rooms to create statistics for
     * @return list of Unpopularity instances with data inserted into member variables
     * @throws SQLException
     */
    public List<Unpopularity> findN(int n) throws SQLException {
        String sql = "SELECT room_id, count(*) AS count" +
                "     FROM contracts" +
                "     WHERE status = 'Prematurely canceled'" +
                "     GROUP BY room_id" +
                "     ORDER BY count DESC" +
                "     LIMIT ?";
        List<Unpopularity> res = new ArrayList<>();
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
            ps.setInt(1, n);
            int order = 1;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Unpopularity x = new Unpopularity();
                    x.setNumberOfReaccommodations(rs.getInt("count"));
                    x.setOrder(order);
                    x.setRoom_id(rs.getInt("room_id"));

                    res.add(x);
                    order++;
                }

            }
        }
        return res;
    }
}
