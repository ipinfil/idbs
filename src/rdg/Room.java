package rdg;

import main.DbContext;

import java.sql.*;
/**
 * @author Filip Lajcin
 * This class represents a row from rooms table in database.
 */
public class Room {
    Integer id;
    int floor;
    int vacancy;
    int building;
    Integer roomTypeId;

    /**
     * Inserts new room to database with data from member variables.
     * @throws SQLException
     */
    public void insert() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("INSERT INTO rooms (floor, vacancy, building, room_type_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, floor);
            ps.setInt(2, vacancy);
            ps.setInt(3, building);
            if (roomTypeId != null) {
                ps.setInt(4, roomTypeId);
            } else ps.setNull(4, Types.INTEGER);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt("id");
            }
        }
    }

    /**
     * Updates room according to data in member variables.
     * @throws SQLException
     */
    public void update() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Room ID is not set in this instance.");
        }
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("UPDATE rooms SET floor = ?, vacancy = ?, building = ?, room_type_id = ? WHERE id = ?")) {
            ps.setInt(1, floor);
            ps.setInt(2, vacancy);
            ps.setInt(3, building);
            ps.setInt(4, roomTypeId);
            ps.setInt(5, id);

            ps.executeUpdate();
        }
    }

    /**
     * Deletes room from database with id from member variable id.
     * @throws SQLException
     */
    public void delete() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Room ID is not set in this instance.");
        }
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("DELETE FROM rooms WHERE id = ?")) {

            ps.setInt(1, id);

            ps.executeUpdate();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getVacancy() {
        return vacancy;
    }

    public void setVacancy(int vacancy) {
        this.vacancy = vacancy;
    }

    public int getBuilding() {
        return building;
    }

    public void setBuilding(int building) {
        this.building = building;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }


}
