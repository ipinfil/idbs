package domainOperations;

import exceptions.*;
import javafx.util.Pair;
import main.DbContext;
import rdg.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Reaccommodate {

    private static final Reaccommodate INSTANCE = new Reaccommodate();

    public static Reaccommodate getInstance() {
        return INSTANCE;
    }

    private Reaccommodate() {
    }

    /**
     * Method that manages the whole operation.
     * @param accountId - id of account that wishes to be reaccommodated
     * @param newRoomId - id of new room
     * @return number that has been deposited/withdrawn to/from account
     * @throws SQLException - serialization error
     * @throws RoomNotFreeException - room has no vacancy
     * @throws StudentNotAccommodatedException - student is not current tenant
     * @throws ReaccomodationNotPossibleException - new room is the same as the current one
     */
    public BigDecimal initialize(int accountId, int newRoomId) throws SQLException, RoomNotFoundException, RoomNotFreeException, StudentNotAccommodatedException, ReaccomodationNotPossibleException, NotEnoughCreditException, StudentNotFoundException {
        DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        DbContext.getConnection().setAutoCommit(false);
        try {
            if (checkFreeRoom(newRoomId)) {
                Contract oldContract;
                if ((oldContract = isAccommodated(accountId)) != null) {
                    if (oldContract.getRoomId() == newRoomId)
                        throw new ReaccomodationNotPossibleException("Nemozno sa preubytovat na rovnaku izbu!");
                    Pair<Contract, Integer> newContract = manageContracts(oldContract, newRoomId);
                    var result = manageRepayment(oldContract, newContract.getKey(), newContract.getValue());
                    DbContext.getConnection().commit();
                    return result;
                } else {
                    throw new StudentNotAccommodatedException();
                }
            } else {
                throw new RoomNotFreeException();
            }
        } catch (SQLException | StudentNotFoundException | RoomNotFoundException | NotEnoughCreditException | RoomNotFreeException | StudentNotAccommodatedException |  ReaccomodationNotPossibleException e) {
            DbContext.getConnection().rollback();
            throw e;
        } finally {
            DbContext.getConnection().setAutoCommit(true);
            DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }
    }

    /**
     *counts how much to withdraw from/deposit to account accoridng to number of days that
     *@param numOfDaysToRepay - number of days that student will be accommodate in new room
     * abs((oldRoomPerDayPrice * numOfDays) - (newRoomPerDayPrice * numOfDays))
     * if the new room is more expensive, we will withdraw this value from account, otherwise we will deposit it to the account
     */

    private BigDecimal manageRepayment(Contract oldContract, Contract newContract, int numOfDaysToRepay) throws SQLException, NotEnoughCreditException {
        if (numOfDaysToRepay != 0) {
            Room oldRoom = RoomFinder.getInstance().findById(oldContract.getRoomId());
            Room newRoom = RoomFinder.getInstance().findById(newContract.getRoomId());

            RoomType oldRt = RoomTypeFinder.getInstance().findById(oldRoom.getRoomTypeId());
            RoomType newRt = RoomTypeFinder.getInstance().findById(newRoom.getRoomTypeId());

            BigDecimal newRoompricePerDay = newRt.getMonthlyPayment().divide(BigDecimal.valueOf(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear())));
            BigDecimal oldRoompricePerDay = oldRt.getMonthlyPayment().divide(BigDecimal.valueOf(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear())));

            BigDecimal newRoomPriceforNumOfDays = BigDecimal.valueOf(numOfDaysToRepay).multiply(newRoompricePerDay);
            BigDecimal oldRoomPriceforNumOfDays = BigDecimal.valueOf(numOfDaysToRepay).multiply(oldRoompricePerDay);

            BigDecimal difference = newRoomPriceforNumOfDays.subtract(oldRoomPriceforNumOfDays).abs();
            //difference contains value how much we need to deposit to/withdraw from account's credit


            if (newRt.getMonthlyPayment().compareTo(oldRt.getMonthlyPayment()) > 0) { //new room is more expensive
                                                                                      //so we will subtract the difference from account
                Account acc = AccountFinder.getInstance().findById(newContract.getAccountId());
                if (acc.getCredit().compareTo(difference) < 0) {
                    throw new NotEnoughCreditException();
                }

                Operation op = new Operation();
                op.setAccId(newContract.getAccountId());
                op.setAmount(difference.negate());

                acc.setCredit(acc.getCredit().subtract(difference));
                op.insert();
                acc.update();
                return difference.negate();

            } else if (newRt.getMonthlyPayment().compareTo(oldRt.getMonthlyPayment()) < 0) {
                Account acc = AccountFinder.getInstance().findById(newContract.getAccountId());

                Operation op = new Operation();
                op.setAccId(newContract.getAccountId());
                op.setAmount(difference);

                acc.setCredit(acc.getCredit().add(difference));
                op.insert();
                acc.update();
                return difference;
            }

        }
        return BigDecimal.valueOf(0); //new room costs the same as the previous one
    }

    /**
     * creates new contract, updates old contract, old room and new room
     *@return Pair<newContract, numberOfDaysToRepay>
     */
    private Pair<Contract, Integer> manageContracts(Contract oldContract, int newRoomId) throws SQLException {
        Contract newContract = new Contract();
        LocalDate now = LocalDate.now();
        LocalDate validUntil = oldContract.getValidUntil();

        oldContract.setValidUntil(now);
        oldContract.setStatus("Prematurely canceled");

        newContract.setYearId(YearFinder.getInstance().findLast().getId());
        newContract.setStatus("Valid");
        newContract.setRoomId(newRoomId);
        newContract.setValidSince(now.plusDays(1));
        newContract.setValidUntil(validUntil);
        newContract.setAccountId(oldContract.getAccountId());

        Room newRoom = RoomFinder.getInstance().findById(newRoomId);
        newRoom.setVacancy(newRoom.getVacancy() - 1);

        Room oldRoom = RoomFinder.getInstance().findById(oldContract.getRoomId());
        oldRoom.setVacancy(oldRoom.getVacancy() + 1);

        oldContract.update();
        newContract.insert();
        oldRoom.update();
        newRoom.update();

        if (LocalDate.now().plusDays(1).getDayOfMonth() == 1) { // reaccommodation at the end of the month
            return new Pair<>(newContract, 0);
        } else {
            return new Pair<>(newContract,
                    LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()) - LocalDate.now().getDayOfMonth() + 1);
                    //number of days of month - number of days on the old room + 1
        }
    }

    private Contract isAccommodated(int accountId) throws SQLException, StudentNotFoundException {
        Account acc = AccountFinder.getInstance().findById(accountId);
        if (acc == null) {
            throw new StudentNotFoundException();
        }
        List<Contract> c = ContractFinder.getInstance().findByAccId(accountId);
        System.out.println(c.stream().anyMatch(contract -> contract.getStatus().equals("Valid")));
        System.out.println(c.stream().anyMatch(contract -> contract.getValidSince().isBefore(LocalDate.now())));
        //student is accommodated if there is a valid contract which time_since property is before now

        if (c.stream().anyMatch(contract -> contract.getStatus().equals("Valid") && contract.getValidSince().isBefore(LocalDate.now()))) {
            for (Contract contract : c) {
                if (contract.getStatus().equals("Valid") && contract.getValidSince().isBefore(LocalDate.now())) {
                    return contract;
                }
            }
        }
        return null;
    }

    private boolean checkFreeRoom(int roomId) throws SQLException, RoomNotFoundException {
        Room r = RoomFinder.getInstance().findById(roomId);
        if (r == null) {
            throw new RoomNotFoundException();
        }
        return r.getVacancy() > 0;
    }

}
