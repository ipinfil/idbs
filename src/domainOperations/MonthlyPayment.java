package domainOperations;

import main.DbContext;
import rdg.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain operation
 * Withdraws credit from every current tenant's account.
 */
public class MonthlyPayment {
    private static final MonthlyPayment INSTANCE = new MonthlyPayment();

    public static MonthlyPayment getInstance() {
        return INSTANCE;
    }

    private MonthlyPayment() {
    }

    /**
     * Encapsulates needed instances to execute operation.
     * Lowered number of connections that had to be made.
     */
    class ContractRoomInfo {
        Contract c;
        Account acc;

        public Account getAcc() {
            return acc;
        }

        public void setAcc(Account acc) {
            this.acc = acc;
        }

        RoomType rt;

        public Contract getC() {
            return c;
        }

        public void setC(Contract c) {
            this.c = c;
        }

        public RoomType getRt() {
            return rt;
        }

        public void setRt(RoomType rt) {
            this.rt = rt;
        }
    }

    /**
     * Loads Account, Contract and RoomType info for tennant.
     * @return List of ContractRoomInfo with needed data to withdraw monthly payment.
     */
    @Deprecated
    private List<ContractRoomInfo> getValidInfo() throws SQLException {
        List<ContractRoomInfo> res = new ArrayList<>();
        String sql = "SELECT c.id as contract_id, c.status, c.year_id, c.account_id, c.room_id, c.valid_since, c.valid_until," +
                "                    rt.capacity, rt.id as room_type_id, rt.monthly_payment," +
                "                    a.credit, a.first_name, a.last_name" +
                " FROM contracts c JOIN rooms r ON c.room_id = r.id JOIN room_types rt ON r.room_type_id = rt.id" +
                "                             JOIN accounts a on c.account_id = a.id" +
                "           WHERE c.status = 'Valid'";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    ContractRoomInfo cri = new ContractRoomInfo();

                    Account acc = new Account();
                    Contract c = new Contract();
                    RoomType rt = new RoomType();
                    c.setId(r.getInt("contract_id"));
                    c.setStatus(r.getString("status"));
                    c.setYearId(r.getInt("year_id"));
                    c.setAccountId(r.getInt("account_id"));
                    c.setRoomId(r.getInt("room_id"));
                    c.setValidSince(LocalDate.parse(r.getObject("valid_since").toString()));
                    c.setValidUntil(LocalDate.parse(r.getObject("valid_until").toString()));

                    rt.setCapacity(r.getInt("capacity"));
                    rt.setId(r.getInt("room_type_id"));
                    rt.setMonthlyPayment(r.getBigDecimal("monthly_payment"));

                    acc.setId(r.getInt("account_id"));
                    acc.setFirstName(r.getString("first_name"));
                    acc.setLastName(r.getString("last_name"));
                    acc.setCredit(r.getBigDecimal("credit"));

                    cri.setAcc(acc);
                    cri.setC(c);
                    cri.setRt(rt);

                    res.add(cri);
                }

            }
        }
        return res;
    }

    /**
     * Withdraws monthly payment from tenants' account.
     * @return List.of(numOfSuccessfulPayments, numOfUnsuccessfulPayments, (estimate of) sumOfAllPayments.intValue())
     * for information purposes to print out some info about the status of monthly payments
     */
    public List<Integer> pay() throws SQLException {
        DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        DbContext.getConnection().setAutoCommit(false);
        try {

            int successfulPayments = 0;
            int unsuccessfulPayments = 0;
            BigDecimal sumOfAllPayments = BigDecimal.valueOf(0);

            try (PreparedStatement s = DbContext.getConnection().prepareStatement("SELECT * FROM monthly_payment()")) {
                try (ResultSet rs = s.executeQuery()) {
                    if (rs.next()) {
                        successfulPayments = rs.getInt("num_of_successful");
                        unsuccessfulPayments = rs.getInt("num_of_blocked");
                        sumOfAllPayments = rs.getBigDecimal("sum");
                    }
                }
            }

            var res = List.of(successfulPayments, unsuccessfulPayments, sumOfAllPayments.intValue());
            DbContext.getConnection().commit();
            return res;
        } catch (SQLException e) {
            DbContext.getConnection().rollback();
            throw e;
        } finally {
            DbContext.getConnection().setAutoCommit(true);
            DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }
    }

}
