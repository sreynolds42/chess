package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        // hashes the password
        String hash = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        // makes the empty sql command
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        // connects to database
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sql)) {
                // completes and runs the sql command from berore
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, hash);
                preparedStatement.setString(3, user.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create user", ex);
        }
    }
}