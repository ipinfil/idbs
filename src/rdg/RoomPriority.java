package rdg;

import main.DbContext;

import java.sql.*;
/**
 * @author Filip Lajcin
 * This class represents row of a table room_priorities in database.
 */
public class RoomPriority {
    Integer id;
    int ord;
    Integer applicationId;
    Integer roomId;

    /**
     * inserts new room priority into db
     * @throws SQLException
     */
    public void insert() throws SQLException {
        String sql = "INSERT INTO room_priorities (ord, application_id, room_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ord);
            if (applicationId != null) {
                ps.setInt(2, applicationId);
            } else ps.setInt(2, Types.INTEGER);

            if (roomId != null) {
                ps.setInt(3, roomId);
            } else ps.setInt(3, Types.INTEGER);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        }
    }

    /**
     * updates existing room priority in db
     * @throws SQLException
     */
    public void update() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Room Priority ID is not set in this instance.");
        }
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("UPDATE room_priorities SET ord = ?, application_id = ?, room_id = ? WHERE id = ?")) {
            ps.setInt(1, ord);
            if (applicationId != null) {
                ps.setInt(2, applicationId);
            } else ps.setInt(2, Types.INTEGER);

            if (roomId != null) {
                ps.setInt(3, roomId);
            } else ps.setInt(3, Types.INTEGER);
            ps.setInt(4, id);


            ps.executeUpdate();
        }
    }

    /**
     * deletes room priority from db
     * @throws SQLException
     */
    public void delete() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Room Priority ID is not set in this instance.");
        }

        try (PreparedStatement s = DbContext.getConnection().prepareStatement("DELETE FROM room_priorities WHERE id = ?")) {
            s.setInt(1, id);

            s.executeUpdate();
        }
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
