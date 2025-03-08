package dataaccess;

import model.AuthData;

public class AuthMySqlDataAccess implements AuthAccess{

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public AuthData createAuth(AuthData authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public String getUsername(String authToken) throws DataAccessException {
        return "";
    }

    public void deleteAll() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `token` VARCHAR(255) PRIMARY KEY,
              `userID` INT,
              FOREIGN KEY (`userID`) REFERENCES users (`id`)
            );
            """
    };
}
