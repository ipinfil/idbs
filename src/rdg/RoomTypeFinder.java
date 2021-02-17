package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Filip Lajcin
 * This class manages finding types of room in database.
 */

public class RoomTypeFinder extends Finder<RoomType> {
    private static final RoomTypeFinder INSTANCE = new RoomTypeFinder();

    public static RoomTypeFinder getInstance() {
        return INSTANCE;
    }

    private RoomTypeFinder() {
    }

    /**
     *
     * @param id - id of room type to find
     * @return RoomType instance with data inserted into member variables or null if there is no such room type
     * @throws SQLException
     */
    public RoomType findById(int id) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM room_types WHERE id = ?")) {
            ps.setInt(1, id);
            return loadOne(ps);
        }
    }

    @Override
    RoomType load(ResultSet rs) throws SQLException {
        RoomType rt = new RoomType();
        rt.setCapacity(rs.getInt("capacity"));
        rt.setId(rs.getInt("id"));
        rt.setMonthlyPayment(rs.getBigDecimal("monthly_payment"));

        return rt;
    }
}
