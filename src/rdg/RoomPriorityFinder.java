package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Filip Lajcin
 * This class manages finding room priority listings in database.
 */

public class RoomPriorityFinder extends Finder<RoomPriority> {

    private static final RoomPriorityFinder INSTANCE = new RoomPriorityFinder();

    public static RoomPriorityFinder getInstance() {
        return INSTANCE;
    }

    private RoomPriorityFinder() {
    }

    /**
     * Finds room priority with highest priority (lowest ord) with linked room that is free (room.vacancy > 0).
     * @param app_id - id of application which room priorities we filter
     * @return RoomPriority instance or null if such room priority is not found
     * @throws SQLException
     */
    public RoomPriority findHighestRoomPriorityWithFreeRoom(int app_id) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM room_priorities JOIN rooms r on room_priorities.room_id = r.id WHERE application_id = ? AND r.vacancy != 0 ORDER BY ord ASC LIMIT 1")) {
            ps.setInt(1, app_id);
            return loadOne(ps);
        }
    }

    /**
     *
     * @param id - id of application which room priorities to find
     * @return list of room priorities with certain application_id
     * @throws SQLException
     */
    public List<RoomPriority> findPrioritiesForApplication(int id) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM room_priorities WHERE application_id = ? ORDER BY ord ASC")) { //izby s najvyssou prioritou budu prve
            ps.setInt(1, id);
            return loadAll(ps);
        }
    }

    /**
     * @return List of all room priorities
     * @throws SQLException
     */
    public List<RoomPriority> findAll() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM room_priorities")) { //izby s najvyssou prioritou budu prve
            return loadAll(ps);
        }
    }

    /**
     *
     * @param id - id of year to find room priorities for
     * @return list of room priorities with certain year_id
     * @throws SQLException
     */
    public List<RoomPriority> findPrioritiesForThisYear(Integer id) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT room_priorities.id, application_id, room_id, ord FROM room_priorities JOIN applications ON room_priorities.application_id = applications.id WHERE year_id = ?")) { //izby s najvyssou prioritou budu prve
            ps.setInt(1, id);
            return loadAll(ps);
        }
    }

    /**
     *
     * @param rpId - id of room priority row in database we find
     * @return RoomPriority instance with data inserted into member variables
     * @throws SQLException
     */
    public RoomPriority findById(int rpId) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM room_priorities WHERE id = ?")) {
            ps.setInt(1, rpId);
            return loadOne(ps);
        }
    }

    /**
     * Paginated alternative to findPrioritiesForApplication() method
     * @throws SQLException
     */
    public List<RoomPriority> findPrioritiesForApplicationPaginated(int appId, int response) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM room_priorities WHERE application_id = ? ORDER BY ord ASC LIMIT 20 OFFSET ? * 20")) { //izby s najvyssou prioritou budu prve
            ps.setInt(1, appId);
            ps.setInt(2, response - 1);
            return loadAll(ps);
        }
    }

    @Override
    RoomPriority load(ResultSet rs) throws SQLException {
        RoomPriority rp = new RoomPriority();
        rp.setId(rs.getInt("id"));
        Integer app_id = rs.getInt("application_id");

        if (app_id != 0) {
            rp.setApplicationId(app_id);
        } else rp.setApplicationId(null);

        Integer room_id = rs.getInt("room_id");
        if (room_id != 0) {
            rp.setRoomId(room_id);
        } else rp.setRoomId(null);

        rp.setOrd(rs.getInt("ord"));

        return rp;
    }
}
