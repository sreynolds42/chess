package service;

import dataaccess.*;
import model.*;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest Request) throws DataAccessException {
        AuthData auth = authDAO.getAuth(Request.authToken());
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        AuthData auth = authDAO.getAuth(req.authToken());
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (req.gameName() == null || req.gameName().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        int gameID = gameDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest req) throws DataAccessException {
        AuthData auth = authDAO.getAuth(req.authToken());
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = gameDAO.getGame(req.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String color = req.playerColor();
        if (color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
            throw new DataAccessException("Error: bad request");
        }

        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();

        if (color.equals("WHITE")) {
            if (whiteUser != null) {
                throw new DataAccessException("Error: already taken");
            }
            whiteUser = auth.username();
        } else {
            if (blackUser != null) {
                throw new DataAccessException("Error: already taken");
            }
            blackUser = auth.username();
        }

        GameData updatedGame = new GameData(
                game.gameID(),
                whiteUser,
                blackUser,
                game.gameName(),
                game.game()
        );

        gameDAO.updateGame(updatedGame);
    }
}