package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.*;


public class RegisterHandler {
    private Object register(Request req, Response res) {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var newAuth = new Gson().fromJson(req.body(), AuthData.class);
        newUser = UserService.createUser(newUser.username(), newUser.password(), newUser.email());
        newAuth = AuthService.createAuth(newAuth.username());
        return new Gson().toJson(newAuth);
    }
}
