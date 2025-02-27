package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class UserMemoryDataAccess implements UserAccess {
    final List<UserData> users;

    public UserMemoryDataAccess() {
        this.users = new ArrayList<>();
    }

    public UserData getUser(String username) throws DataAccessException {
        try {
            for (UserData user : users) {
                if (user.username().equals(username)) {
                    return user;
                }
            }
            throw new DataAccessException("User not found");
        }
        catch (Exception e) {
            throw new DataAccessException("Error getting user: " + e.getMessage());
    }
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        try {
            UserData newUser = new UserData(username, password, email);
            users.add(newUser);
            System.out.println(users);
            return newUser;
        }
        catch (Exception e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }
}
