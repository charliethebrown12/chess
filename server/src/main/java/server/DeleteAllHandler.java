package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.GameMemoryDataAccess;
import dataAccess.UserMemoryDataAccess;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class DeleteAllHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());
    private final GameService gameService = new GameService(new GameMemoryDataAccess());
    private final UserService userService = new UserService(new UserMemoryDataAccess());

    Object deleteAll(Request req, Response res) {
        try {
            authService.deleteAll();
            gameService.deleteAll();
            userService.deleteAll();
            return new Gson().toJson(new Object());

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson("Error: " + e.getMessage());
        }
    }
}
