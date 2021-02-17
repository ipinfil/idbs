package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomFinder extends Finder<Room> {
    private static final RoomFinder INSTANCE = new RoomFinder();

    public static RoomFinder getInstance() {
        return INSTANCE;
    }

    private RoomFinder() {
    }

    public Room findRandomFree() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM rooms WHERE vacancy != 0")) {
            return loadOne(s);
        }
    }

    public Room findById(Integer room_id) throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM rooms WHERE id = ?")) {
            s.setInt(1, room_id);
            return loadOne(s);
        }
    }

    public List<Room> findAll() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM rooms")) { //izby s najvyssou prioritou budu prve
            return loadAll(ps);
        }
    }

    @Override
    Room load(ResultSet r) throws SQLException {
        Room room = new Room();
        room.setId(r.getInt("id"));
        room.setBuilding(r.getInt("building"));
        room.setFloor(r.getInt("floor"));
        room.setRoomTypeId(r.getInt("room_type_id"));
        room.setVacancy(r.getInt("vacancy"));

        return room;
    }
}
