package dataaccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    @Override
    public int createGame(String gameName) throws DataAccessException {
        // the empty command to make the game
        String sql = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";

        ChessGame chessGame = new ChessGame();
        String serializedGame = new Gson().toJson(chessGame);

        try (var connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(true);
            try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // fills in the game making comamnd
                preparedStatement.setString(1, null);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, gameName);
                preparedStatement.setString(4, serializedGame);
                //runs and returns id
                preparedStatement.executeUpdate();

                try (var rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException  exception) {
            throw new DataAccessException("Error: failed to create game",  exception);
        }
        throw new DataAccessException("Error: failed to create game, no ID generated");
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        String sql = "SELECT id, whiteUsername, blackUsername, gameName, game FROM game WHERE id = ?";

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        int foundID = rs.getInt("id");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame chessGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(foundID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to get game", exception);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT id, whiteUsername, blackUsername, gameName, game FROM game";
        Collection<GameData> games = new ArrayList<>();

        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sql);
                 var result = preparedStatement.executeQuery()){
                while (result.next()) {
                    int id = result.getInt("id");
                    String whiteUsername = result.getString("whiteUsername");
                    String blackUsername = result.getString("blackUsername");
                    String gameName = result.getString("gameName");
                    ChessGame chessGame = new Gson().fromJson(result.getString("game"), ChessGame.class);
                    games.add(new GameData(id, whiteUsername, blackUsername, gameName, chessGame));
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to list games", exception);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // empty query to be filled in
        String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE id = ?";
        String serializedGame = new Gson().toJson(game.game());
        // fills it in
        try (var connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(true);
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, serializedGame);
                preparedStatement.setInt(5, game.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException  exception) {
            throw new DataAccessException("Error: failed to update game",  exception);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        // nukes it
        String sql = "TRUNCATE game";

        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(true);
            try (var ps = conn.prepareStatement(sql)) {
                //  executes the nuke
                ps.executeUpdate();
            }
        } catch (SQLException  exception) {
            throw new DataAccessException("Error: failed to clear game table",  exception);
        }
    }
}