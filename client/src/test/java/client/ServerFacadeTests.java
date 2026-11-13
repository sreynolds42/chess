package client;

import org.junit.jupiter.api.*; import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests { private static Server server; private static ServerFacade serverFacade;

    // starts server before tests
    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        serverFacade = new ServerFacade(port);
    }

    // stops server after tests
    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    // clears database before every test
    @BeforeEach
    public void clearDb() throws Exception {
        serverFacade.clear();
    }

    // tests registering a user successfully
    @Test
    public void registerPositive() throws Exception {
        var auth = serverFacade.register("player1", "password", "p1@email.com");

        assertNotNull(auth);
        assertEquals("player1", auth.username());
    }

    // tests registering with missing information
    @Test
    public void registerNegative() {
        assertThrows(Exception.class, () -> {
            serverFacade.register(null, "password", "p1@email.com");
        });
    }

    // tests logging in successfully
    @Test
    public void loginPositive() throws Exception {
        serverFacade.register("player1", "password", "p1@email.com");

        var auth = serverFacade.login("player1", "password");

        assertNotNull(auth.authToken());
        assertEquals("player1", auth.username());
    }

    // tests logging in with incorrect information
    @Test
    public void loginNegative() {
        assertThrows(Exception.class, () -> {
            serverFacade.login("wronguser", "wrongpassword");
        });
    }

    // tests creating a game successfully
    @Test
    public void createGamePositive() throws Exception {
        var auth = serverFacade.register("player1", "password", "p1@email.com");

        var game = serverFacade.createGame(auth.authToken(), "TestGame");

        assertTrue(game.gameID() > 0);
    }

    // tests creating a game without a valid auth token
    @Test
    public void createGameNegative() {
        assertThrows(Exception.class, () -> {
            serverFacade.createGame("wrongToken", "TestGame");
        });
    }

    // tests getting the list of games successfully
    @Test
    public void listGamesPositive() throws Exception {
        var auth = serverFacade.register("player1", "password", "p1@email.com");
        serverFacade.createGame(auth.authToken(), "TestGame");

        var games = serverFacade.listGames(auth.authToken());

        assertFalse(games.games().isEmpty());
    }

    // tests getting games without a valid auth token
    @Test
    public void listGamesNegative() {
        assertThrows(Exception.class, () -> {
            serverFacade.listGames("wrongToken");
        });
    }

    // tests joining a game successfully
    @Test
    public void joinGamePositive() throws Exception {
        var auth = serverFacade.register("player1", "password", "p1@email.com");
        var game = serverFacade.createGame(auth.authToken(), "TestGame");

        assertDoesNotThrow(() -> {
            serverFacade.joinGame(auth.authToken(), game.gameID(), "WHITE");
        });
    }

    // tests joining a game without a valid auth token
    @Test
    public void joinGameNegative() {
        assertThrows(Exception.class, () -> {
            serverFacade.joinGame("wrongToken", 9999, "WHITE");
        });
    }

    // tests logging out successfully
    @Test
    public void logoutPositive() throws Exception {
        var auth = serverFacade.register("player1", "password", "p1@email.com");

        assertDoesNotThrow(() -> {
            serverFacade.logout(auth.authToken());
        });
    }

    // tests logging out without a valid auth token
    @Test
    public void logoutNegative() {
        assertThrows(Exception.class, () -> {
            serverFacade.logout("wrongToken");
        });
    }

}