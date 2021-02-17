package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Filip Lajcin
 * Class manages creating report about average daily room occupation.
 */
public class AverageDailyRoomOccupationFinder {
    private static final AverageDailyRoomOccupationFinder INSTANCE = new AverageDailyRoomOccupationFinder();

    public static AverageDailyRoomOccupationFinder getInstance() {
        return INSTANCE;
    }

    private AverageDailyRoomOccupationFinder() {
    }

    /**
     * Executes query which gets average daily room occupation grouped by capacity.
     * For example:
     *         Capacity       |         Value
     *             1          |         0.745
     *             2          |         1.487
     *             3          |         2.740
     * @return list of AverageDailyRoomOccupation instances.
     * @throws SQLException
     */
    public List<AverageDailyRoomOccupation> findAll() throws SQLException {
        String sql = "select capacity, --tu uz pocita priemernu dennu obsadenost pre vsetky izby urcitej kapacity - vid group by capacity\n" +
                "       (sum(average_daily_occupancy_of_room)::float / (SELECT count(*) FROM rooms WHERE room_type_id IN (SELECT id from room_types rt2 where rt2.capacity = rt3.capacity))) * rt3.capacity::float as result\n" +
                "from\n" +
                "    (select room_id,\n" +
                "           sum(valid_until - valid_since + 1) / -- pocet zazmluvnenych dni izby / kapacita izby * pocet vsetkych dni = priemerna denna obsadenost konkretnej izby\n" +
                "                                             (  (SELECT capacity from room_types where room_types.id = (select room_type_id from rooms where rooms.id = room_id)) --kapacita izby\n" +
                "                                              * ((SELECT max(valid_until) FROM contracts) - (SELECT min(valid_since) from contracts) + 1)::float) AS average_daily_occupancy_of_room --pocet vsetkych dni\n" +
                "    FROM contracts\n" +
                "    group by room_id) as calculation\n" +
                "join rooms on room_id = rooms.id\n" +
                "join room_types rt3 on rooms.room_type_id = rt3.id\n" +
                "group by capacity;";

        List<AverageDailyRoomOccupation> result = new ArrayList<>();
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AverageDailyRoomOccupation element = new AverageDailyRoomOccupation();

                    element.setAvg(rs.getFloat("result"));
                    element.setCapacity(rs.getInt("capacity"));

                    result.add(element);
                }
            }
        }
        return result;
    }
}
