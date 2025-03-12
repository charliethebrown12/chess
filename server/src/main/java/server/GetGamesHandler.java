package server;

import com.google.gson.Gson;
import dataaccess.AuthMemoryDataAccess;
import dataaccess.AuthMySqlDataAccess;
import dataaccess.GameMemoryDataAccess;
import dataaccess.GameMySqlDataAccess;
import model.ErrorData;
import service.AuthService;
import service.GameService;
import service.GamesList;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

public class GetGamesHandler {
    private final AuthService authService = new AuthService(new AuthMySqlDataAccess());
    private final GameService gameService = new GameService(new GameMySqlDataAccess());

    Object getGames(Request req, Response res) {
        String authToken = req.headers("Authorization");

        try {
            if(!authService.getAuth(authToken)) {
                res.status(401);
                return new Gson().toJson((new ErrorData("Error: User is not authorized. Please login")));
            }
            List<GamesList> games = gameService.getGames();
            return new Gson().toJson(Map.of("games", games));

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson(new ErrorData("Error: " + e.getMessage()));
        }
    }
}
