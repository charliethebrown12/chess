package dataAccess;

import model.GameData;

public interface GameAccess {
    int createGame(String gameName) throws DataAccessException;
}
