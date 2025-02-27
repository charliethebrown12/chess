package service;

import dataAccess.DataAccessException;
import dataAccess.GameAccess;

public class GameService {
    private static GameAccess gameAccess;

    public GameService(GameAccess gameAccess) {
        GameService.gameAccess = gameAccess;
    }

    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Game name cannot be empty");
        }
        return gameAccess.createGame(gameName);
    }
}
