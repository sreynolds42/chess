package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void positiveRegister() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("testUser", "password123", "test@mail.com");
        RegisterResult result = userService.register(req);

        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void negativeRegisterDuplicate() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("testUser", "password123", "test@mail.com");
        userService.register(req);

        // Trying to register the same username should throw an exception
        assertThrows(DataAccessException.class, () -> {
            userService.register(req);
        });
    }

    @Test
    public void positiveLogin() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("testUser", "password123", "test@mail.com");
        userService.register(regReq);

        LoginRequest loginReq = new LoginRequest("testUser", "password123");
        LoginResult result = userService.login(loginReq);

        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void negativeLoginUnauthorized() {
        LoginRequest loginReq = new LoginRequest("nonExistent", "wrongPassword");

        assertThrows(DataAccessException.class, () -> {
            userService.login(loginReq);
        });
    }

    @Test
    public void positiveLogout() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("testUser", "password123", "test@mail.com");
        RegisterResult regRes = userService.register(regReq);

        LogoutRequest logoutReq = new LogoutRequest(regRes.authToken());
        assertDoesNotThrow(() -> {
            userService.logout(logoutReq);
        });
    }

    @Test
    public void negativeLogoutUnauthorized() {
        LogoutRequest logoutReq = new LogoutRequest("invalidAuthToken");

        assertThrows(DataAccessException.class, () -> {
            userService.logout(logoutReq);
        });
    }
}