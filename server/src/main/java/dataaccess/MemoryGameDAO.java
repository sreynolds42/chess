package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public void clear() {
        games.clear();
        nextGameID = 1;
    }

    @Override
    public int createGame(String gameName) {
        int gameID = nextGameID++;
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Error: game not found");
        }
        games.put(game.gameID(), game);
    }
}