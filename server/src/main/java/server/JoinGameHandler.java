package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameMemoryDataAccess;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());
    private final GameService gameService = new GameService(new GameMemoryDataAccess());

    Object joinGame(Request req, Response res) {
        String AuthToken = req.headers("Authorization");
        var joinRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);


        try {
            if(!authService.getAuth(AuthToken)) {
                throw new DataAccessException("User is not authorized to join game.");
            }
            String user = authService.getUser(AuthToken);
            gameService.joinGame(joinRequest.gameID(), joinRequest.playerColor(), user);
            return new Gson().toJson(new Object());

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson("Error: " + e.getMessage());
        }
    }
}
