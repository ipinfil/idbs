package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGSimpleDataSource;
import ui.UserInterface;

/**
 * Connection to database and setting a Connection in DbContext instance.
 * Source: example project by Alexander Simko.
 */
public class Main {

    public static void main(String[] args) throws SQLException, IOException {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setServerName("db.dai.fmph.uniba.sk");
        dataSource.setPortNumber(5432);
        dataSource.setDatabaseName("playground");
        dataSource.setUser("lajcin3@uniba.sk");
        dataSource.setPassword("baterka");

        try (Connection connection = dataSource.getConnection()) {
            DbContext.setConnection(connection);

            UserInterface ui = new UserInterface();
            ui.start();

        } finally {
            DbContext.clear();
        }
    }
}