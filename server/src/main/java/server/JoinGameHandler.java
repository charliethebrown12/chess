package server;

import com.google.gson.Gson;
import dataaccess.AuthMemoryDataAccess;
import dataaccess.GameMemoryDataAccess;
import model.ErrorData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());
    private final GameService gameService = new GameService(new GameMemoryDataAccess());

    Object joinGame(Request req, Response res) {
        String authToken = req.headers("Authorization");
        var joinRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);


        try {
            if(!authService.getAuth(authToken)) {
                res.status(401);
                return new Gson().toJson((new ErrorData("Error: User is not authorized. Please login")));
            }
            String user = authService.getUser(authToken);
            gameService.joinGame(joinRequest.gameID(), joinRequest.playerColor(), user);
            return new Gson().toJson(new Object());

        } catch (Exception e) {
            if (e.getMessage().equals("That color is already in use by another player")) {
                res.status(403);
                return new Gson().toJson((new ErrorData("Error: " + e.getMessage())));
            }
            res.status(400);
            return new Gson().toJson(new ErrorData("Error: " + e.getMessage()));
        }
    }
}
