package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class UserMySqlDataAccess implements UserAccess {
    public UserMySqlDataAccess() {
        DatabaseManager.createDatabase();
        createTables();
    }

    public UserData getUser(String username, String password) throws DataAccessException {
        String statement = "SELECT username, email, password FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        if (BCrypt.checkpw(password, hashedPassword)) {
                            return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                        } else {return null;}
                    } else {return null;}

                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("No rows affected, user not created");
                }
                return new UserData(username, password, email);
            } catch (SQLException e) {
                throw new DataAccessException("User creation failed " + e.getMessage());
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAll() throws DataAccessException {
        String statement = "DELETE FROM users";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(statement)) {
                prepareStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              id int NOT NULL AUTO_INCREMENT,
              username VARCHAR(50) UNIQUE NOT NULL,
              email VARCHAR(100) UNIQUE NOT NULL,
              password VARCHAR(255) NOT NULL,
              PRIMARY KEY (id)
            );
            """
    };

    private void createTables() {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Failed to create tables: " + e.getMessage());
        }
    }
}
