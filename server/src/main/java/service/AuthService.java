package service;

import dataAccess.AuthAccess;
import dataAccess.DataAccessException;
import dataAccess.UserAccess;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private static AuthAccess authAccess;

    public AuthService(AuthAccess authAccess) {
        this.authAccess = authAccess;
    }
    public AuthData createAuth(String username) throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        return authAccess.createAuth(uuid.toString(), username);
    }
}
