package service;

import dataAccess.AuthAccess;
import dataAccess.DataAccessException;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private static AuthAccess authAccess;

    public AuthService(AuthAccess authAccess) {
        AuthService.authAccess = authAccess;
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException("Username cannot be empty");
        }
        UUID uuid = UUID.randomUUID();
        AuthData newAuth = new AuthData(uuid.toString(), username);
        return authAccess.createAuth(newAuth);
    }

    public boolean getAuth(String authToken) throws DataAccessException {
        AuthData auth = authAccess.getAuth(authToken);
        return auth != null;
    }

    public void deleteAuth(String AuthToken) throws DataAccessException {
        authAccess.deleteAuth(AuthToken);
    }

    public String getUser(String AuthToken) throws DataAccessException {
        return authAccess.getUsername(AuthToken);
    }

    public void deleteAll() throws DataAccessException {
        authAccess.deleteAll();
    }
}
