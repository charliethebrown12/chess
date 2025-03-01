package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.GameMemoryDataAccess;
import model.ErrorData;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class CreateGameHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());
    private final GameService gameService = new GameService(new GameMemoryDataAccess());

    Object createGame(Request req, Response res) {
        String AuthToken = req.headers("Authorization");
        var newGame = new Gson().fromJson(req.body(), GameData.class);


        try {
            if(!authService.getAuth(AuthToken)) {
                res.status(401);
                return new Gson().toJson((new ErrorData("Error: User is not authorized. Please login")));
            }
            int gameID = gameService.createGame(newGame.gameName());
            return new Gson().toJson(Map.of("gameID", gameID));

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson(new ErrorData("Error: " + e.getMessage()));
        }
    }
}
