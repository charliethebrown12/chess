package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class UserMemoryDataAccess implements UserAccess {
    final List<UserData> users;

    public UserMemoryDataAccess(List<UserData> users) {
        this.users = new ArrayList<>();
    }

    public UserData getUser(String username) throws DataAccessException {
        try {
            if (users.contains(userData)) return userData;
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
    }
        return null;
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        try {
            if (getUser(username) != null) {
                throw new DataAccessException("User already exists");
            }
            UserData newUser = new UserData(username, password, email);
            users.add(newUser);
            return newUser;
        }
        catch (Exception e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }
}
