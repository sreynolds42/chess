package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", Server::register);

        // Register your endpoints and exception handlers here.

    }

    private static void register(Context ctx){
        var result = Map.of("username", "", "authToken", "");
        ctx.result(new Gson().toJson(result));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
