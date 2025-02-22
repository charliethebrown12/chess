package service;

import model.UserData;

public class UserService {
    public static UserData createUser(String username, String password, String email) {
        return new UserData(username, password, email);
    }
}
