package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Filip Lajcin
 * This class represents row in database table years.
 */

public class Year {
    Integer id;
    Integer value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Insert new year into database table years.
     * @throws SQLException
     */
    public void insert() throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("INSERT INTO years (value) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, value);

            ps.executeUpdate();

            try (ResultSet r = ps.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }
        }
    }

}
