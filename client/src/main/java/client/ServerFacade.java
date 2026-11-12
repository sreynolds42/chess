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
        var requestData = new UserData(username, password, email);
        return makeRequest("POST", path, requestData, AuthData.class, null);
    }

    //logs in with user and pass
    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var requestData = new UserData(username, password, null);
        return makeRequest("POST", path, requestData, AuthData.class, null);
    }

    //makes http reqs and goes through Jason response
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
}