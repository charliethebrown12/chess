package server;

import com.google.gson.Gson;
import dataAccess.AuthMemoryDataAccess;
import dataAccess.DataAccessException;
import dataAccess.UserMemoryDataAccess;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.*;

import java.util.ArrayList;


public class RegisterHandler {
    private final UserService userService = new UserService(new UserMemoryDataAccess(new ArrayList<>()));
    private final AuthService authService = new AuthService(new AuthMemoryDataAccess(new ArrayList<>()));

    Object register(Request req, Response res) throws DataAccessException {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var newAuth = new Gson().fromJson(req.body(), AuthData.class);

        try {
            userService.createUser(newUser.username(), newUser.password(), newUser.email());
            newAuth = authService.createAuth(newAuth.username());
            return new Gson().toJson(newAuth);

        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson("Error registering user: " + e.getMessage());
        }
    }
}
