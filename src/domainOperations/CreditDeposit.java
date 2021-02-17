package domainOperations;

import exceptions.StudentNotFoundException;
import main.DbContext;
import rdg.Account;
import rdg.AccountFinder;
import rdg.Operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Domain operation
 */
public class CreditDeposit {

    private static final CreditDeposit INSTANCE = new CreditDeposit();
    public static CreditDeposit getInstance() { return INSTANCE; }
    private CreditDeposit() {}

    /**
     * Deposits credit to account if the amount is > 0
     */
    public void depositCreditToStudent(int accId, BigDecimal amount) throws SQLException, StudentNotFoundException {
        DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        DbContext.getConnection().setAutoCommit(false);
        try {
            if (amount.compareTo(new BigDecimal(0)) <= 0) throw new IllegalArgumentException("Nemozete vlozit zapornu alebo nulovu hodnotu.");


            Account acc = AccountFinder.getInstance().findById(accId);
            if (acc == null) {
                throw new StudentNotFoundException("Takyto student neexistuje.");
            }

            Operation op = new Operation();
            op.setAmount(amount);
            op.setAccId(accId);
            op.insert();

            BigDecimal sum = amount.add(acc.getCredit());
            acc.setCredit(sum);

            acc.update();
            DbContext.getConnection().commit();

        } catch (SQLException | StudentNotFoundException | IllegalArgumentException e) {
            DbContext.getConnection().rollback();
            throw e;
        }
        finally {
            DbContext.getConnection().setAutoCommit(true);
            DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }

    }
}
