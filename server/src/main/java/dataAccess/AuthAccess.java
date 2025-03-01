package dataAccess;

import model.AuthData;

public interface AuthAccess {
    AuthData getAuth(String authToken) throws DataAccessException;

    AuthData createAuth(AuthData authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;
}
