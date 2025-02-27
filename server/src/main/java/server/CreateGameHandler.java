package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameMemoryDataAccess;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());
    private final GameService gameService = new GameService(new GameMemoryDataAccess());

    Object createGame(Request req, Response res) {
        String AuthToken = req.headers("Authorization");
        var newGame = new Gson().fromJson(req.body(), GameData.class);


        try {
            if(!authService.getAuth(AuthToken)) {
                throw new DataAccessException("User is not authorized to logout.");
            }
            int gameID = gameService.createGame(newGame.gameName());
            return new Gson().toJson(gameID);

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson("Error logging out: " + e.getMessage());
        }
    }
}
