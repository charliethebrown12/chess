package dataAccess;

import model.AuthData;

public interface AuthAccess {
    AuthData getAuth(String authToken) throws DataAccessException;

    AuthData createAuth(String username, String authToken) throws DataAccessException;
}
