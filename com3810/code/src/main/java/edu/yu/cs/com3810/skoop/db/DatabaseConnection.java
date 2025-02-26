package edu.yu.cs.com3810.skoop.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections for PostgreSQL.
 */
public class DatabaseConnection {
    private static final String URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";

    /**
     * Gets a database connection.
     *
     * @return Connection object for database operations
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
       //implementation needed
        return null;
    }

    /**
     * Closes the given database connection.
     *
     * @param connection connection to close
     */
    public void closeConnection(Connection connection) {
        //implementation needed
    }

}