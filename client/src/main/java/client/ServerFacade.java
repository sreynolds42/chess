package client;

import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    //constructs with port
    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    //registers a user with username, pass and email, throwing an error if smth is wrong
    public AuthData register(String username, String password, String email) throws Exception {
        var path = "/user";
        var requestData = new RegisterRequest(username, password, email);
        return makeRequest("POST", path, requestData, AuthData.class, null);
    }

    //logs in with user and pass
    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var requestData = new LoginRequest(username, password);
        return makeRequest("POST", path, requestData, AuthData.class, null);
    }

    // function for making a game, called by postlogin, takes authToken and gamename to make the game
    public GameData createGame(String authToken, String gameName) throws Exception{
        var path = "/game";
        var requestData = new CreateGameRequest(authToken, gameName);
        return makeRequest("POST", path, requestData, GameData.class, authToken);
    }
    // function for deleting the authToken to log out
    public AuthData logout(String authToken) throws Exception{
        var path = "/session";
        return makeRequest("DELETE", path, null, null, authToken);
    }

    public ListGamesResult listGames(String authToken) throws Exception{
        var path = "/game";
        return makeRequest("GET", path, null, ListGamesResult.class, authToken);
    }

    public void joinGame(String authToken, int ID, String color) throws Exception{
        var path = "/game";
        var requestData = new JoinGameRequest(authToken, color, ID);
        makeRequest("PUT", path, requestData, null, authToken);
    }

    //makes http reqs and goes through Jason response
    // it takes in the method, PRE POST  DELETE, the path,
    private <T> T makeRequest(String method, String path, Object requestData, Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            //if theres an auth token, it adds it, if not it moves on
            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            //sends a request if there is data
            if (requestData != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String reqData = new Gson().toJson(requestData);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }

            http.connect();

            //Looks for messed up response codes and throws an error if so
            int status = http.getResponseCode();
            if (status < 200 || status > 299) {
                throw new Exception("Server error: " + status);
            }

            //plugs response into obj
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    return new Gson().fromJson(reader, responseClass);
                }
            }
            return null;
        } catch (Exception error) {
            throw new Exception(error.getMessage());
        }

    }
    // clear for tests
    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }
}