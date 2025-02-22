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
        return userAccess.createUser(username, password, email);
    }
}
