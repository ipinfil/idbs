package main;

import java.sql.Connection;

/**
 * Used to get Connection to execute queries in the app.
 */

public class DbContext {
    private static Connection connection;

    public static void setConnection(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection cannot be null");
        }

        DbContext.connection = connection;
    }

    public static Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("First, you have to set the connection.");
        }

        return connection;
    }

    /**
     * Deletes connection once the app is shut down.
     */
    public static void clear() {
        connection = null;
    }
    
    
}
