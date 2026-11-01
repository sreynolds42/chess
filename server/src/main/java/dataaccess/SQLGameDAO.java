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

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                // fills in the game making comamnd
                ps.setInt(1, game.gameID());
                ps.setString(2, game.whiteUsername());
                ps.setString(3, game.blackUsername());
                ps.setString(4, game.gameName());
                ps.setString(5, serializedGame);
                //runs and returns id
                ps.executeUpdate();
                return game.gameID();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
    }
}
