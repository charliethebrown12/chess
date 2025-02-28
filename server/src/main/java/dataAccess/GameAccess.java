package dataAccess;

import model.GameData;
import service.GamesList;

import java.util.List;

public interface GameAccess {
    int createGame(String gameName) throws DataAccessException;

    List<GamesList> getGames() throws DataAccessException;
}
