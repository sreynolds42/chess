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
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {

    }

    public void logout(LogoutRequest req) throws DataAccessException {

    }
}