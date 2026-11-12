package client;

import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static Server server;
    static ServerFacade facade;

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
    void clearDatabase() throws Exception {
        facade.clear();
    }

    @Test
    void registerPositive() throws Exception {
        var auth = facade.register("player1", "password", "p1@email.com");
        assertNotNull(auth.authToken());
    }

    @Test
    void registerNegative() {
        try {
            facade.register("player2", "password", "p2@email.com");
            facade.register("player2", "password", "p2@email.com"); // Duplicate
            fail();
        } catch (Exception ignored) {}
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("player3", "password", "p3@email.com");
        var auth = facade.login("player3", "password");
        assertNotNull(auth.authToken());
    }

    @Test
    void loginNegative() {
        try {
            facade.login("nonexistentUser", "wrongPassword");
            fail();
        } catch (Exception ignored) {}
    }
}