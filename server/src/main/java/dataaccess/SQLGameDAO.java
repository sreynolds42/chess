package dataaccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    @Override
    public int createGame(GameData game) throws DataAccessException {
        // the empty command to make the game
        String sql = "INSERT INTO game (id, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";

        String serializedGame = new Gson().toJson(game.game());

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sql)) {
                // fills in the game making comamnd
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(5, serializedGame);
                //runs and returns id
                preparedStatement.executeUpdate();
                return game.gameID();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // empty query to be filled in
        String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE id = ?";
        String serializedGame = new Gson().toJson(game.game());
        // fills it in
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, serializedGame);
                preparedStatement.setInt(5, game.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to update game", ex);
        }
    }
}
