package dataaccess;

import model.AuthData;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        //initializes the sql command but as an empty thing to be filled in later
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sql)) {
                // fills in the data
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                // this runs it in mySQL
                preparedStatement.executeUpdate();
            }

        } catch (SQLException exception) {
            throw new DataAccessException("failed to create auth token", exception);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        //initializes the sql command but as an empty thing to be filled in later
        String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sql)) {
                // fills in the data
                preparedStatement.setString(1, authToken);
                // runs the filled in sql command
                try (var results = preparedStatement.executeQuery()) {
                    if (results.next()) {
                        String foundToken = results.getString("authToken");
                        String username = results.getString("username");
                        return new AuthData(foundToken, username);
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("failed to get auth token", exception);
        }

        return null;
    }


}
