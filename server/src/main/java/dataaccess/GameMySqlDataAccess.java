package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import service.GamesList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameMySqlDataAccess implements GameAccess{
    public GameMySqlDataAccess() {
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
                throw new DataAccessException("Game creation failed ");
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
                        GameData game = new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUserID"),
                                rs.getString("blackUserID"),
                                rs.getString("gameName"),
                                null);
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
                    } else {throw new DataAccessException("No players have joined this game yet: Cannot observe game.");}

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
        var statement = "INSERT INTO games (gameID, whiteUserID, blackUserID, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(5, new Gson().toJson(game.game()));
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("No rows affected, game not created");
                }
            } catch (SQLException e) {
                throw new DataAccessException("Game creation failed ");
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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
                whiteUserID VARCHAR(50),
                blackUserID VARCHAR(50),
                gameName VARCHAR(255) NOT NULL,
                chessGame TEXT NOT NULL
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
            System.out.println("Tables created successfully.");
        } catch (SQLException | DataAccessException e) {
            System.err.println("Failed to create tables: " + e.getMessage());
        }
    }

    public void updateGameState(int gameID, ChessGame game) throws DataAccessException {
        String statement = "UPDATE games SET chessGame = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            String gameJson = new Gson().toJson(game);
            preparedStatement.setString(1, gameJson);
            preparedStatement.setInt(2, gameID);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Failed to update game state in DB");
            }
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error: " + e.getMessage());
        }
    }

    public void clearPlayerFromGame(int gameID, String color) throws DataAccessException {
        String sql;
        if (color.equalsIgnoreCase("WHITE")) {
            sql = "UPDATE games SET whiteUserID = NULL WHERE gameID = ?";
        } else if (color.equalsIgnoreCase("BLACK")) {
            sql = "UPDATE games SET blackUserID = NULL WHERE gameID = ?";
        } else {
            throw new DataAccessException("Invalid color: " + color);
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear player from game" , e);
        }
    }
}
