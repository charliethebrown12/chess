package server;

import com.google.gson.Gson;
import dataaccess.AuthMySqlDataAccess;
import dataaccess.UserMySqlDataAccess;
import model.AuthData;
import model.ErrorData;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.*;


public class RegisterHandler {
    private final UserService userService = new UserService(new UserMySqlDataAccess());
    private final AuthService authService = new AuthService(new AuthMySqlDataAccess());

    Object register(Request req, Response res) {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var newAuth = new Gson().fromJson(req.body(), AuthData.class);

        try {
            newUser = userService.createUser(newUser.username(), newUser.password(), newUser.email());
            if (newUser == null) {
                res.status(403);
                return new Gson().toJson((new ErrorData("Error: User already exists")));
            }
            newAuth = authService.createAuth(newAuth.username());
            return new Gson().toJson(newAuth);

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson(new ErrorData("Error: " + e.getMessage()));
        }
    }
}
