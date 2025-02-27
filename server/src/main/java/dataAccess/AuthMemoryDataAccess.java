package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;

public class AuthMemoryDataAccess  implements AuthAccess{
    final List<AuthData> auths;

    public AuthMemoryDataAccess() {
        this.auths = new ArrayList<>();
    }

    public boolean getAuth(String authToken) throws DataAccessException {
        try {
            for (AuthData auth : auths) {
                if (authToken.equals(auth.authToken())) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
    }

    public AuthData createAuth(String username, String authToken) throws DataAccessException {
        try {
            AuthData newAuth = new AuthData(username, authToken);
            auths.add(newAuth);
            System.out.println(auths);
            return newAuth;
        }
        catch (Exception e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            auths.removeIf(auth -> authToken.equals(auth.authToken()));
            System.out.println(auths);
        }
        catch (Exception e) {
            throw new DataAccessException("Error deleting user: " + e.getMessage());
        }
    }
}
