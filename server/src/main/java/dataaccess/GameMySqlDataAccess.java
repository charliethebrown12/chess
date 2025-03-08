package dataaccess;

import model.GameData;
import service.GamesList;

import java.util.List;

public class GameMySqlDataAccess implements GameAccess{

    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    public List<GamesList> getGames() throws DataAccessException {
        return List.of();
    }

    public GameData joinGame(int gameID) throws DataAccessException {
        return null;
    }

    public void deleteGame(int gameID) throws DataAccessException {

    }

    public void addGame(GameData game) throws DataAccessException {

    }

    public void deleteAll() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                whiteUserID INT,
                blackUserID INT,'
                gameName VARCHAR(255) NOT NULL,
                chessGame VARCHAR(255) NOT NULL,
            );
            """
    };
}
