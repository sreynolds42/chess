package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }



    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if(request.username() == null || request.password() == null || request.email() == null){
            throw new DataAccessException("Error: bad request");
        }
        else if(request.username().isBlank() || request.password().isBlank() || request.email().isBlank()){
            throw new DataAccessException("Error: bad request");
        }
        else if(userDAO.getUser(request.username()) != null){
            throw new DataAccessException("Error: already taken");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.insertUser(newUser);

        String token = UUID.randomUUID().toString();
        AuthData data = new AuthData(token, request.username());
        authDAO.createAuth(data);

        return new RegisterResult(request.username(), token);

    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if(request.username() == null || request.password() == null){
            throw new DataAccessException("Error: bad request");
        }
        else if(request.username().isBlank() || request.password().isBlank()){
            throw new DataAccessException("Error: bad request");
        }

        UserData user = userDAO.getUser(request.username());

        if (user == null || !org.mindrot.jbcrypt.BCrypt.checkpw(request.password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData data = new AuthData(token, request.username());
        authDAO.createAuth(data);

        return new LoginResult(user.username(), token);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        AuthData data = authDAO.getAuth(request.authToken());
        if(data == null){
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(request.authToken());
    }
}