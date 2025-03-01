package dataAccess;

import model.GameData;
import service.GamesList;

import java.util.List;

public interface GameAccess {
    int createGame(String gameName) throws DataAccessException;

    List<GamesList> getGames() throws DataAccessException;

    GameData joinGame(int gameID) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

    void addGame(GameData game) throws DataAccessException;

    void deleteAll() throws DataAccessException;
}
