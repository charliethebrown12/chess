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
        return authAccess.createAuth(uuid.toString(), username);
    }

    public boolean getAuth(String authToken) throws DataAccessException {
        return authAccess.getAuth(authToken);
    }

    public void deleteAuth(String AuthToken) throws DataAccessException {
        authAccess.deleteAuth(AuthToken);
    }
}
