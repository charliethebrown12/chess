package service;

import dataAccess.DataAccessException;
import dataAccess.UserAccess;
import model.UserData;

public class UserService {
    private static UserAccess userAccess;

    public UserService(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new DataAccessException("Password cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new DataAccessException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new DataAccessException("Email must contain @");
        }
        return userAccess.createUser(username, password, email);
    }
}
