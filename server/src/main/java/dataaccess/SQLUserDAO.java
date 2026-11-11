package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        // hashes the password
        String passwordToStore = user.password();
        if (!passwordToStore.startsWith("$2")) {
            passwordToStore = BCrypt.hashpw(passwordToStore, BCrypt.gensalt());
        }
        // makes the empty sql command
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        // connects to database
        try (var connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(true);
            try (var preparedStatement = connection.prepareStatement(sql)) {
                // completes and runs the sql command from berore
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, passwordToStore);
                preparedStatement.setString(3, user.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // writes empty command, this one searches based on username
        String sql = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                // fills in command details
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {

                    // cchecks if matching user is found and returns it
                    if (rs.next()) {
                        String foundUsername = rs.getString("username");
                        String hashedPassword = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(foundUsername, hashedPassword, email);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to get user", ex);
        }
        return null;
    }
    @Override
    public void clear() throws DataAccessException {
        // nukes table
        String sql = "DELETE FROM user";
        try (var connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(true);
            try (var preparedStatement = connection.prepareStatement(sql)) {
                // runs the nuke
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to clear user table", exception);
        }
    }
}