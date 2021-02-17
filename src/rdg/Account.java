package rdg;

import java.math.BigDecimal;
import java.sql.*;

import main.DbContext;

/**
 * @author Filip Lajcin
 * Instance of class Account represent one row from database in table accounts.
 */
public class Account {

    private Integer id;
    private String firstName;
    private String lastName;
    private BigDecimal credit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    /**
     * Inserts a new row into database with data from member variables firstName, lastName, credit.
     * Changes the id member variable if insertion is successful.
     * @throws SQLException
     */
    public void insert() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("INSERT INTO accounts (first_name, last_name, credit) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {//vytvorime prepared statement
            s.setString(1, firstName); //vlozime udaje do statementu
            s.setString(2, lastName);
            s.setBigDecimal(3, credit);

            s.executeUpdate(); //vykoname query

            try (ResultSet r = s.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1); //v pripade ze je uspesny insert, do clenskej premennej vlozim id noveho konta
            }
        }
    }
    /**
     * Updates row in database with data from member variables firstName, lastName, credit.
     * @throws SQLException
     */
    public void update() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Account ID is not set in this instance.");
        }
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("UPDATE accounts SET first_name = ?, last_name = ?, credit = ? WHERE id = ?")) {//vytvorime prepared statement
            s.setString(1, firstName); //vlozime udaje do statementu
            s.setString(2, lastName);
            s.setBigDecimal(3, credit);
            s.setInt(4, id);

            s.executeUpdate(); //vykoname query
        }

    }

    /**
     * Deletes row in database with id from member variable id.
     * @throws SQLException
     */
    public void delete() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Account ID is not set in this instance.");
        }

        try (PreparedStatement s = DbContext.getConnection().prepareStatement("DELETE FROM accounts WHERE id = ?")) {
            s.setInt(1, id);

            int rowsAffected = s.executeUpdate();

        }
    }
}
