package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;


    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }
    // making the SQL tables
    static public void createSQLTables() throws DataAccessException{
        // this one makes username stuff for SQL, usrname, password (hash), and email if there isnt already a table
        // username is the key so it has to be unique and is the main categorizer
        String userSQLTable = """
                CREATE TABLE IF NOT EXISTS user (
                    username VARCHAR(250) NOT NULL,
                    password VARCHAR(250) NOT NULL,
                    email VARCHAR(250) NOT NULL,
                    primary KEY (username)
                    )
                """;
        // this does the same thing as the last table, but for authentication
        // the email or password isnt included, and the categorizer is authToken
        String authSQLTable = """
                CREATE TABLE IF NOT EXISTS auth (
                    authToken VARCHAR(250) NOT NULL,
                    username VARCHAR(250) NOT NULL,
                    primary KEY (authToken)
                    )
                """;
        // makes game table with an incrementing id as the categorizer
        // stores the usernames of each side, names the game, and has game to store the actual game
        String gameSQLTable = """
                CREATE TABLE IF NOT EXISTS game (
                    id INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(250),
                    blackUsername VARCHAR(250),
                    gameName VARCHAR(250) NOT NULL,
                    game TEXT NOT NULL,
                    PRIMARY KEY (id)
                    )
                """;

        // initializes all three tables
        try (var conn = getConnection();
             var stmt = conn.createStatement()) {
            //table 1
            stmt.executeUpdate(userSQLTable);
            //second table
            stmt.executeUpdate(authSQLTable);
            //third table
            stmt.executeUpdate(gameSQLTable);

            //errors
        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to create tables", exception);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
