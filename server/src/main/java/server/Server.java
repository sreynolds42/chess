package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import java.util.Map;
import io.javalin.*;
import service.*;
import dataaccess.*;
import model.*;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO, gameDAO);
    private final GameService gameService = new GameService(authDAO, gameDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    private void clear(Context ctx) {
        try {
            userService.clear();
            ctx.status(200);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void register(Context ctx) {
        try {
            RegisterRequest req = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            var result = userService.register(req);
            ctx.result(new Gson().toJson(result));
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void login(Context ctx) {
        try {
            LoginRequest req = new Gson().fromJson(ctx.body(), LoginRequest.class);
            var result = userService.login(req);
            ctx.result(new Gson().toJson(result));
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            LogoutRequest req = new LogoutRequest(authToken);
            userService.logout(req);
            ctx.status(200);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            ListGamesRequest req = new ListGamesRequest(authToken);
            var result = gameService.listGames(req);
            ctx.result(new Gson().toJson(result));
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            Map body = new Gson().fromJson(ctx.body(), Map.class);
            String gameName = body != null ? (String) body.get("gameName") : null;
            CreateGameRequest req = new CreateGameRequest(authToken, gameName);
            var result = gameService.createGame(req);
            ctx.result(new Gson().toJson(result));
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            Map body = new Gson().fromJson(ctx.body(), Map.class);
            String playerColor = body != null ? (String) body.get("playerColor") : null;
            int gameID = 0;
            if (body != null && body.get("gameID") != null) {
                gameID = ((Number) body.get("gameID")).intValue();
            }
            JoinGameRequest req = new JoinGameRequest(authToken, playerColor, gameID);
            gameService.joinGame(req);
            ctx.status(200);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void handleException(Context ctx, Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : "Unknown error";
        if (message.toLowerCase().contains("unauthorized")) {
            ctx.status(401);
        } else if (message.toLowerCase().contains("already taken")) {
            ctx.status(403);
        } else {
            ctx.status(400);
        }
        ctx.result(new Gson().toJson(Map.of("message", message)));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}