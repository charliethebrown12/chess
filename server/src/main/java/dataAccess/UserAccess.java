package dataAccess;


import model.UserData;

public interface UserAccess {
    UserData getUser(String username, String password) throws DataAccessException;

    UserData createUser(String username, String password, String email) throws DataAccessException;

    void deleteAll() throws DataAccessException;
}
