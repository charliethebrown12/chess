package dataAccess;

import model.AuthData;

public interface AuthAccess {
    boolean getAuth(String authToken) throws DataAccessException;

    AuthData createAuth(String username, String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
