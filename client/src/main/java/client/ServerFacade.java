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
    }


}