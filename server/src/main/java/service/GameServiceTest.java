package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;
    private String validAuthToken;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);

        AuthData authData = new AuthData("valid-token", "testUser");
        authDAO.createAuth(authData);
        validAuthToken = "valid-token";
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        ListGamesRequest req = new ListGamesRequest(validAuthToken);
        ListGamesResult result = gameService.listGames(req);
        assertNotNull(result);
        assertNotNull(result.games());
    }

    @Test
    void listGamesUnauthorized() {
        ListGamesRequest req = new ListGamesRequest("invalid-token");
        assertThrows(DataAccessException.class, () -> gameService.listGames(req));
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        CreateGameRequest req = new CreateGameRequest(validAuthToken, "My Game");
        CreateGameResult result = gameService.createGame(req);
        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameBadRequest() {
        CreateGameRequest req = new CreateGameRequest(validAuthToken, "");
        assertThrows(DataAccessException.class, () -> gameService.createGame(req));
    }

    @Test
    void createGameUnauthorized() {
        CreateGameRequest req = new CreateGameRequest("invalid-token", "My Game");
        assertThrows(DataAccessException.class, () -> gameService.createGame(req));
    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("My Game");
        JoinGameRequest req = new JoinGameRequest(validAuthToken, "WHITE", gameID);
        assertDoesNotThrow(() -> gameService.joinGame(req));
    }

    @Test
    void joinGameAlreadyTaken() throws DataAccessException {
        int gameID = gameDAO.createGame("My Game");
        JoinGameRequest req1 = new JoinGameRequest(validAuthToken, "WHITE", gameID);
        gameService.joinGame(req1);

        AuthData authData2 = new AuthData("valid-token-2", "otherUser");
        authDAO.createAuth(authData2);

        JoinGameRequest req2 = new JoinGameRequest("valid-token-2", "WHITE", gameID);
        assertThrows(DataAccessException.class, () -> gameService.joinGame(req2));
    }

    @Test
    void joinGameUnauthorized() throws DataAccessException {
        int gameID = gameDAO.createGame("My Game");
        JoinGameRequest req = new JoinGameRequest("invalid-token", "WHITE", gameID);
        assertThrows(DataAccessException.class, () -> gameService.joinGame(req));
    }

    @Test
    void joinGameBadRequest() throws DataAccessException {
        int gameID = gameDAO.createGame("My Game");
        JoinGameRequest req = new JoinGameRequest(validAuthToken, "INVALID_COLOR", gameID);
        assertThrows(DataAccessException.class, () -> gameService.joinGame(req));
    }
}