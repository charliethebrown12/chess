package server;

import com.google.gson.Gson;
import dataaccess.AuthMemoryDataAccess;
import dataaccess.AuthMySqlDataAccess;
import dataaccess.GameMemoryDataAccess;
import dataaccess.GameMySqlDataAccess;
import model.ErrorData;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class CreateGameHandler {
    private final AuthService authService = new AuthService(new AuthMySqlDataAccess());
    private final GameService gameService = new GameService(new GameMySqlDataAccess());

    Object createGame(Request req, Response res) {
        String authToken = req.headers("Authorization");
        var newGame = new Gson().fromJson(req.body(), GameData.class);


        try {
            if(!authService.getAuth(authToken)) {
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
