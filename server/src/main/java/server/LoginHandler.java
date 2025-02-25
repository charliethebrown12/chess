package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.DataAccessException;
import dataAccess.UserMemoryDataAccess;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class LoginHandler {
    private final UserService userService = new UserService(new UserMemoryDataAccess(new ArrayList<>()));
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess(new ArrayList<>()));

    Object login(Request req, Response res) throws DataAccessException {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var newAuth = new Gson().fromJson(req.body(), AuthData.class);

        try {
            userService.getUser(newUser.username());
            newAuth = authService.createAuth(newAuth.username());
            return new Gson().toJson(newAuth);

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson("User does not exist. Please try again or register.");
        }
    }
}
