package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserService userService;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO, gameDAO);
    }

    @Test
    void registerSuccess() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("testUser", "password123", "test@email.com");
        RegisterResult result = userService.register(req);
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerBadRequest() {
        RegisterRequest req = new RegisterRequest("", "password", "email@email.com");
        assertThrows(DataAccessException.class, () -> userService.register(req));
    }

    @Test
    void registerAlreadyTaken() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("testUser", "password123", "test@email.com");
        userService.register(req);
        assertThrows(DataAccessException.class, () -> userService.register(req));
    }

    @Test
    void loginSuccess() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("testUser", "password123", "test@email.com");
        userService.register(regReq);

        LoginRequest loginReq = new LoginRequest("testUser", "password123");
        LoginResult result = userService.login(loginReq);
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginUnauthorized() {
        LoginRequest loginReq = new LoginRequest("nonexistent", "wrongpassword");
        assertThrows(DataAccessException.class, () -> userService.login(loginReq));
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("testUser", "password123", "test@email.com");
        RegisterResult regRes = userService.register(regReq);

        LogoutRequest logoutReq = new LogoutRequest(regRes.authToken());
        assertDoesNotThrow(() -> userService.logout(logoutReq));
    }

    @Test
    void logoutUnauthorized() {
        LogoutRequest logoutReq = new LogoutRequest("invalid-token");
        assertThrows(DataAccessException.class, () -> userService.logout(logoutReq));
    }

    @Test
    void clear() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("testUser", "password123", "test@email.com");
        userService.register(regReq);
        assertDoesNotThrow(() -> userService.clear());
    }
}