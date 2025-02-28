package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameMemoryDataAccess;
import model.GameData;
import service.AuthService;
import service.GameService;
import service.GamesList;
import spark.Request;
import spark.Response;

import java.util.List;

public class GetGamesHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());
    private final GameService gameService = new GameService(new GameMemoryDataAccess());

    Object getGames(Request req, Response res) {
        String AuthToken = req.headers("Authorization");

        try {
            if(!authService.getAuth(AuthToken)) {
                throw new DataAccessException("User is not authorized to get games list. Please login first.");
            }
            List<GamesList> games = gameService.getGames();
            return new Gson().toJson(games);

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson(e.getMessage());
        }
    }
}
