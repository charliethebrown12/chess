package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.GameMemoryDataAccess;
import model.ErrorData;
import service.AuthService;
import service.GameService;
import service.GamesList;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

public class GetGamesHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());
    private final GameService gameService = new GameService(new GameMemoryDataAccess());

    Object getGames(Request req, Response res) {
        String AuthToken = req.headers("Authorization");

        try {
            if(!authService.getAuth(AuthToken)) {
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
