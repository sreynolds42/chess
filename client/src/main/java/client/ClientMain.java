package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        var port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        ServerFacade facade = new ServerFacade(port);
        PreloginClient preloginClient = new PreloginClient(facade);
        preloginClient.run();
    }
}
