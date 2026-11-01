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


}
