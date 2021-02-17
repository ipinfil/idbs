package domainOperations;

import exceptions.ContractNotFoundException;
import exceptions.NotEnoughCreditException;
import exceptions.WrongContractFormatException;
import main.DbContext;
import rdg.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Domain operation
 * Manages signing contracts.
 */
public class Accommodate {
    private static final Accommodate INSTANCE = new Accommodate();

    public static Accommodate getInstance() {
        return INSTANCE;
    }

    private Accommodate() {
    }

    /**
     * @param contractId - id of contract to sign
     * @throws SQLException - serialization error
     * @throws NotEnoughCreditException - low credit on owner of the contract
     * @throws ContractNotFoundException - non existing contract to sign
     * @throws WrongContractFormatException - you can only sign contracts which have not been validated yet, nor contracts with no room assigned to it
     */
    public void signContract(int contractId) throws SQLException, NotEnoughCreditException, ContractNotFoundException, WrongContractFormatException {
        DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        DbContext.getConnection().setAutoCommit(false);
        try {
            Contract c = ContractFinder.getInstance().findById(contractId);

            if (c == null) {
                throw new ContractNotFoundException();
            } else if (c.getStatus() != null || c.getRoomId() == null) {
                throw new WrongContractFormatException();
            }

            Account acc = AccountFinder.getInstance().findById(c.getAccountId());
            Room room = RoomFinder.getInstance().findById(c.getRoomId());
            RoomType rt = RoomTypeFinder.getInstance().findById(room.getRoomTypeId());

            if (acc.getCredit().compareTo(rt.getMonthlyPayment()) >= 0) { // account credit >= than monthly_payment
                Operation op = new Operation();
                op.setAccId(acc.getId());
                op.setAmount(rt.getMonthlyPayment().negate());
                op.insert();

                acc.setCredit(acc.getCredit().subtract(rt.getMonthlyPayment()));
                acc.update();

                c.setStatus("Valid");
                c.update();
                DbContext.getConnection().commit();
            } else {
                throw new NotEnoughCreditException();
            }
        } catch (SQLException | ContractNotFoundException | WrongContractFormatException | NotEnoughCreditException e) {
            DbContext.getConnection().rollback();
            throw e;
        } finally {
            DbContext.getConnection().setAutoCommit(true);
            DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }

    }

}
