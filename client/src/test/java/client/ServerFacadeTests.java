package java.client;

import client.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDb() throws Exception {
        facade.clear();
    }

    @Test
    public void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData.authToken());
        assertEquals("player1", authData.username());
    }

    @Test
    public void registerNegative() {
        assertThrows(Exception.class, () -> {
            facade.register(null, "password", "p1@email.com");
        });
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        assertNotNull(authData.authToken());
    }

    @Test
    public void loginNegative() {
        assertThrows(Exception.class, () -> {
            facade.login("nonexistent", "wrongpass");
        });
    }

    @Test
    public void createGamePositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame(auth.authToken(), "TestGame");
        assertTrue(game.gameID() > 0);
    }

    @Test
    public void createGameNegative() {
        assertThrows(Exception.class, () -> {
            facade.createGame("invalidAuth", "TestGame");
        });
    }

    @Test
    public void listGamesPositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        facade.createGame(auth.authToken(), "Game1");
        var listResult = facade.listGames(auth.authToken());
        assertFalse(listResult.games().isEmpty());
    }

    @Test
    public void listGamesNegative() {
        assertThrows(Exception.class, () -> {
            facade.listGames("invalidAuth");
        });
    }

    @Test
    public void joinGamePositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame(auth.authToken(), "Game1");
        assertDoesNotThrow(() -> {
            facade.joinGame(auth.authToken(), game.gameID(), "WHITE");
        });
    }

    @Test
    public void joinGameNegative() {
        assertThrows(Exception.class, () -> {
            facade.joinGame("invalidAuth", 9999, "WHITE");
        });
    }

    @Test
    public void logoutPositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> {
            facade.logout(auth.authToken());
        });
    }

    @Test
    public void logoutNegative() {
        assertThrows(Exception.class, () -> {
            facade.logout("invalidAuth");
        });
    }
}