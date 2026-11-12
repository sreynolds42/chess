package client;

import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    // Constructor accepts the port so it can connect to the server (including test ports)
    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    // Registers a new user on the server
    public AuthData register(String username, String password, String email) throws Exception {
        var path = "/user";
        var requestData = new UserData(username, password, email);
        return makeRequest("POST", path, requestData, AuthData.class, null);
    }

    // Logs an existing user into the server
    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var requestData = new UserData(username, password, null);
        return makeRequest("POST", path, requestData, AuthData.class, null);
    }

    // Helper method to make HTTP requests and parse the JSON response
    private <T> T makeRequest(String method, String path, Object requestData, Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // Add authorization token if provided
            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            // Send JSON request body if data exists
            if (requestData != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String reqData = new Gson().toJson(requestData);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }

            http.connect();

            // Check for bad response codes
            int status = http.getResponseCode();
            if (status < 200 || status > 299) {
                throw new Exception("Server error: " + status);
            }

            // Read response body into object
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    return new Gson().fromJson(reader, responseClass);
                }
            }
            return null;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
}