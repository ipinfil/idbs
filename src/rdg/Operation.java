package rdg;

import main.DbContext;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Filip Lajcin
 * This class reresents row of operations table in database.
 */

public class Operation {
    private Integer id;
    private BigDecimal amount;
    private Integer accId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getAccId() {
        return accId;
    }

    public void setAccId(Integer accId) {
        this.accId = accId;
    }

    /**
     * Inserts operation into database with data from member variables.
     * @throws SQLException
     */
    public void insert() throws SQLException {
        try (PreparedStatement s = DbContext.getConnection().prepareStatement("INSERT INTO operations (amount, account_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {//vytvorime prepared statement
            s.setBigDecimal(1, amount); //vlozime udaje do statementu
            s.setInt(2, accId);

            s.executeUpdate(); //vykoname query

            try (ResultSet r = s.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1); //v pripade ze je uspesny insert, do clenskej premennej vlozim id noveho konta
            }
        }
    }
}
