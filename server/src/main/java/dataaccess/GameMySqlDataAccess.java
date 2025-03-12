package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import service.GamesList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameMySqlDataAccess implements GameAccess{
    public GameMySqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        createTables();
    }

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameName, chessGame) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, new Gson().toJson(new ChessGame()));
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("No rows affected, game not created");
                }

                try (var resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    } else {throw new DataAccessException("Game creation failed, no ID obtained");}
                }
            } catch (SQLException e) {
                throw new DataAccessException("Game creation failed " + e.getMessage());
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public List<GamesList> getGames() throws DataAccessException {
        String statement = "SELECT gameID, whiteUserID, blackUserID, gameName FROM games";
        List<GamesList> gamesLists = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        GameData game = new GameData(rs.getInt("gameID"), rs.getString("whiteUserID"), rs.getString("blackUserID"), rs.getString("gameName"), null);
                        gamesLists.add(new GamesList(game));
                    }

                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gamesLists;
    }

    public GameData joinGame(int gameID) throws DataAccessException {
        Gson gson = new Gson();
        String statement = "SELECT gameID, whiteUserID, blackUserID, gameName, chessGame FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        int gameID2 = rs.getInt("gameID");
                        String whiteUserID2 = rs.getString("whiteUserID");
                        String blackUserID2 = rs.getString("blackUserID");
                        String gameName2 = rs.getString("gameName");
                        String chessGameJson = rs.getString("chessGame");
                        ChessGame chessGame = gson.fromJson(chessGameJson, ChessGame.class);
                        return new GameData(gameID2, whiteUserID2, blackUserID2, gameName2, chessGame);
                    } else {throw new DataAccessException("No rows affected, game not joined");}

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

    public void deleteGame(int gameID) throws DataAccessException {
        String statement = "DELETE FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(statement)) {
                prepareStatement.setInt(1, gameID);
                int affected = prepareStatement.executeUpdate();
                if (affected == 0) {
                    throw new DataAccessException("No rows affected, game not found");
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void addGame(GameData game) throws DataAccessException {

    }

    public void deleteAll() throws DataAccessException {
        String statement = "DELETE FROM games";

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
            CREATE TABLE IF NOT EXISTS  games (
                gameID int AUTO_INCREMENT PRIMARY KEY,
                whiteUserID int,
                blackUserID int,
                gameName VARCHAR(255) NOT NULL,
                chessGame TEXT NOT NULL,
                FOREIGN KEY (whiteUserID) REFERENCES users(id),
                FOREIGN KEY (blackUserID) REFERENCES users(id)
            );
            """
    };

    private void createTables() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create tables: " + e.getMessage());
        }
    }
}
