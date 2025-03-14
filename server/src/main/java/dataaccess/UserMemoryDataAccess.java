package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class UserMemoryDataAccess implements UserAccess {
    final List<UserData> users;

    public UserMemoryDataAccess() {
        this.users = new ArrayList<>();
    }

    public UserData getUser(String username, String password) throws DataAccessException {
        try {
            for (UserData user : users) {
                if (user.username().equals(username) && BCrypt.checkpw(password, user.password())) {
                    return user;
                }
            }
            return null;
        }
        catch (Exception e) {
            throw new DataAccessException("Error getting user: " + e.getMessage());
    }
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        try {
            UserData newUser = new UserData(username, password, email);
            if (users.isEmpty()) {
                users.add(newUser);
                System.out.println(users);
                return newUser;
            }
            for (UserData user : users){
                if (!user.username().equals(username)) {
                    users.add(newUser);
                    System.out.println(users);
                    return newUser;
                }
            }
            return null;
        }
        catch (Exception e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }

    public void deleteAll() {
        users.clear();
    }
}
