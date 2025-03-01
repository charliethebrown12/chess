package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;

public class AuthMemoryDataAccess  implements AuthAccess{
    final List<AuthData> auths;

    public AuthMemoryDataAccess() {
        this.auths = new ArrayList<>();
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try {
            for (AuthData auth : auths) {
                if (authToken.equals(auth.authToken())) {
                    return auth;
                }
            }
            return null;
        }
        catch (Exception e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
    }

    public AuthData createAuth(AuthData authToken) throws DataAccessException {
        try {
            auths.add(authToken);
            System.out.println(auths);
            return authToken;
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

    public String getUsername(String authToken) throws DataAccessException {
        try {
            for (AuthData auth : auths) {
                if (authToken.equals(auth.authToken())) {
                    return auth.username();
                }
            }
            throw new DataAccessException("Error retrieving user: ");
        }
        catch (Exception e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
    }

    public void deleteAll() {
        auths.clear();
    }
}
