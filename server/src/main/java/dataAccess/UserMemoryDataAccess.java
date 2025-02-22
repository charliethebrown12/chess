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
            for (UserData user : users) {
                if (user.username().equals(username)) {
                    return user;
                }
            }
            return null;
        }
        catch (Exception e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
    }
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
