package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.DataAccessException;
import service.AuthService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess());

    Object logout(Request req, Response res) {
        String AuthToken = req.headers("Authorization");

        try {
            if(!authService.getAuth(AuthToken)) {
                throw new DataAccessException("User is not authorized to logout.");
            }
            authService.deleteAuth(AuthToken);
            return new Gson().toJson(new Object());

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson("Error logging out: " + e.getMessage());
        }
    }
}
