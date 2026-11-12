package client;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(int port){
        this.serverURL = "http://localhost:" + port;
    }

    private <T> T makeRequest(String method, String path, Object dataReq, Class<T> classResp, String authToken) throws Exception{
        URL url = new URL(serverURL + path);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(true);
        // if the token isnt null and isnt empty then it gets the authorization
        if (authToken != null && !authToken.isEmpty()) {
            http.setRequestProperty("authorization", authToken);
        }
        if (dataReq != null) {
            http.setRequestProperty("Content-Type", "application/json");
            String body = new Gson().toJson(dataReq);
            try (OutputStream os = http.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
        http.connect();

        int status = http.getResponseCode();
        if (status < 200 || status > 299) {
            throw new Exception("Server returned code: " + status);
        }

        try (InputStreamReader reader = new InputStreamReader(http.getInputStream())) {
            if (classResp != null) {
                return new Gson().fromJson(reader, classResp);
            }
            return null;
        }
    }


}