package domainOperations;

import exceptions.NotEnoughRoomsException;
import main.DbContext;
import rdg.*;

import java.net.CookieHandler;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomAssignment {
    private static final RoomAssignment INSTANCE = new RoomAssignment();

    public static RoomAssignment getInstance() {
        return INSTANCE;
    }

    private RoomAssignment() {
    }

    /**
     * Manages whole operation.
     * @param pointThreshold - how much points do students need to have room assigned in this accommodation round
     * @return List.of(number of cancelledContracts, number of students that have been assigned to a room);
     */
    public List<Integer> initiate(int pointThreshold) throws SQLException {
        DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        DbContext.getConnection().setAutoCommit(false);
        try {
            int cancelledContracts = cancelNotSignedContracts();
            generateContracts();
            int assignedRooms = roomAssignment(pointThreshold);

            return List.of(cancelledContracts, assignedRooms);
        } catch (SQLException | NotEnoughRoomsException e) {
            DbContext.getConnection().rollback();
            throw e;
        } finally {
            DbContext.getConnection().setAutoCommit(true);
            DbContext.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }
    }

    /**
     * Will change contracts, that have assigned rooms, but has not been signed for accommodation, to INVALID.
     */
    private int cancelNotSignedContracts() throws SQLException {
        int rowsUpdated = 0;

        String sql = "UPDATE rooms SET vacancy = vacancy + 1 * (SELECT count(*) FROM contracts WHERE contracts.room_id IS NOT NULL AND contracts.room_id = rooms.id AND contracts.status IS NULL);";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        }

        sql = "UPDATE applications SET status = 'Denied' WHERE contract_id IN (SELECT id FROM contracts WHERE status IS NULL AND room_id IS NOT NULL);";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        }

        sql = "UPDATE contracts SET status = 'Invalid' WHERE status IS NULL AND room_id IS NOT NULL;";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
            rowsUpdated = ps.executeUpdate();
        }

        return rowsUpdated;
    }
    /**
     * Generate contracts with room_id and status set to NULL.
     * It generates contracts to all of the applications, so it doesn't need to generate them in each round.
     */
    private void generateContracts() throws SQLException {

        //in order to optimize performance, I created functions that handle all of the generating contracts
        //and accepting applications in the database server
        String createContractsSQL = "SELECT generate_contracts();";
        String acceptApplicationsSQL = "SELECT accept_applications();";

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(createContractsSQL)) {
            ps.executeQuery();
        }

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(acceptApplicationsSQL)) {
            ps.executeQuery();
        }
    }
    /**
     * Looks for the highest ordered room priority and picks one thats free. If there is no free room in chosen room priorities,
     * pick random free one. Updates new contract with the roomId.
     */
    private int roomAssignment(int pointThreshold) throws SQLException {
        int result = 0;

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT room_assignment(?)")) {
            ps.setInt(1, pointThreshold);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            } catch (SQLException e) {
                if (e.getSQLState().equals("P0001")) {
                    throw new NotEnoughRoomsException();
                }
            }
        }
        DbContext.getConnection().commit();
        return result;
    }

    /**
     * Finds contracts from all of the applicants that have enough points for this accommodation round.
     * Contracts are ordered by number of points, since the more points one has, the higher is his priority
     * in room assignment.
     * @return list of helper class RoomAssignmentJoined Info that contains encapsulated Contract and application id
     * of student
     */
    @Deprecated
    private List<RoomAssignmentJoinedInfo> contractsOrderedByPoints(int pointThreshold) throws SQLException {
        List<RoomAssignmentJoinedInfo> info = new ArrayList<>();
        String sql = "SELECT c.id, c.year_id, c.account_id, c.room_id, c.valid_since, c.valid_until," +
                "            a.id as app_id " +
                "FROM contracts c" +
                "     JOIN points ON c.account_id = points.account_id  AND c.year_id = points.year_id" +
                "     JOIN applications a on c.id = a.contract_id" +
                "     WHERE c.status IS NULL AND c.year_id = ? AND points.amount >= ?" +
                "     ORDER BY points.amount DESC";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
            ps.setInt(1, YearFinder.getInstance().findLast().getId());
            ps.setInt(2, pointThreshold);
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {

                    Contract c = new Contract();
                    c.setId(r.getInt("id"));
                    c.setStatus(null);
                    c.setYearId(r.getInt("year_id"));
                    c.setAccountId(r.getInt("account_id"));
                    c.setRoomId(r.getInt("room_id"));
                    c.setValidSince(LocalDate.parse(r.getObject("valid_since").toString()));
                    c.setValidUntil(LocalDate.parse(r.getObject("valid_until").toString()));

                    int appId = r.getInt("app_id");

                    info.add(new RoomAssignmentJoinedInfo(c, appId));
                }
            }
        }
        return info;
    }

    /**
     * Helper class used to encapsulate Contract and application id.
     * Minimizes number of connections that had to be made.
     */
    private static class RoomAssignmentJoinedInfo {
        Contract c;
        int appId;

        public RoomAssignmentJoinedInfo(Contract c, int appId) {
            this.c = c;
            this.appId = appId;
        }

        public Contract getC() {
            return c;
        }

        public void setC(Contract c) {
            this.c = c;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }
    }


}
