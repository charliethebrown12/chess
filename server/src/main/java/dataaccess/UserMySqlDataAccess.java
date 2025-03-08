package dataaccess;

import model.UserData;

public class UserMySqlDataAccess implements UserAccess {

    public UserData getUser(String username, String password) throws DataAccessException {
        return null;
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        return null;
    }

    public void deleteAll() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` VARCHAR(50) UNIQUE NOT NULL,
              email VARCHAR(100) UNIQUE NOT NULL,
              password VARCHAR(255) NOT NULL,
              PRIMARY KEY (`id`),
            );
            """
    };
}
