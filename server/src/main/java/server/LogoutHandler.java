package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import model.ErrorData;
import service.AuthService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());

    Object logout(Request req, Response res) {
        String AuthToken = req.headers("Authorization");

        try {
            if(!authService.getAuth(AuthToken)) {
                res.status(401);
                return new Gson().toJson((new ErrorData("Error: User is not authorized. Please login")));
            }
            authService.deleteAuth(AuthToken);
            return new Gson().toJson(new Object());

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson(new ErrorData("Error: " + e.getMessage()));
        }
    }
}
