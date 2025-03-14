package service;

import dataaccess.DataAccessException;
import dataaccess.UserAccess;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private static UserAccess userAccess;

    public UserService(UserAccess userAccess) {
        UserService.userAccess = userAccess;
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
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return userAccess.createUser(username, hashedPassword, email);
    }

    public UserData getUser(String username, String password) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException("Username cannot be empty");
        }
        return userAccess.getUser(username, password);
    }

    public void deleteAll() throws DataAccessException {
        userAccess.deleteAll();
    }

}
