package server;

import com.google.gson.Gson;
import dataaccess.AuthMySqlDataAccess;
import dataaccess.UserMySqlDataAccess;
import model.AuthData;
import model.ErrorData;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final UserService userService = new UserService(new UserMySqlDataAccess());
    private final AuthService authService = new AuthService(new AuthMySqlDataAccess());

    Object login(Request req, Response res) {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var newAuth = new Gson().fromJson(req.body(), AuthData.class);
        Gson gson = new Gson();

        try {
            newUser = userService.getUser(newUser.username(), newUser.password());
            if (newUser == null) {
                res.status(401);
                res.type("application/json");
                return gson.toJson((new ErrorData("Error: Invalid username or password")));
            }
            newAuth = authService.createAuth(newAuth.username());
            return gson.toJson(newAuth);

        } catch (Exception e) {
            res.status(400);
            return gson.toJson(new ErrorData("Error: " + e.getMessage()));
        }
    }
}
