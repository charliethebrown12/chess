package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthMySqlDataAccess implements AuthAccess{
    public AuthMySqlDataAccess() {
        createTables();
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT token, username FROM auths WHERE token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("token"), rs.getString("username"));
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

    public AuthData createAuth(AuthData authToken) throws DataAccessException {
        var statement = "INSERT INTO auths (token, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken.authToken());
                preparedStatement.setString(2, authToken.username());
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("No rows affected, auth token not created");
                }
                return authToken;
            } catch (SQLException e) {
                throw new DataAccessException("Auth token creation failed " + e.getMessage());
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auths WHERE token = ?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(statement)) {
                prepareStatement.setString(1, authToken);
                prepareStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String getUsername(String authToken) throws DataAccessException {
        String statement = "SELECT username FROM auths WHERE token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
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

    public void deleteAll() throws DataAccessException {
        String statement = "DELETE FROM auths";

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
            CREATE TABLE IF NOT EXISTS  auths (
              token VARCHAR(255) PRIMARY KEY,
              username VARCHAR(50) NOT NULL,
              FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
            );
            """
    };

    private void createTables()  {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println("Failed to create tables for auths: " + e.getMessage());
        }
    }
}
