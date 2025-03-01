package service;

import dataAccess.DataAccessException;
import dataAccess.GameAccess;
import model.GameData;

import java.util.List;
import java.util.Objects;

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

    public List<GamesList> getGames() throws DataAccessException {
        return gameAccess.getGames();
    }

    public void joinGame(int gameID, String playerColor, String user) throws DataAccessException {
        GameData game = gameAccess.joinGame(gameID);
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();
        gameAccess.deleteGame(gameID);
        GameData updateGame;
        if (Objects.equals(playerColor, "WHITE")) {
            if (whiteUsername == null) {
                updateGame = new GameData(game.gameID(), user, game.blackUsername(), game.gameName(), game.game());
                gameAccess.addGame(updateGame);
            }
        }
        else if (Objects.equals(playerColor, "BLACK")) {
            if (blackUsername == null) {
                updateGame = new GameData(game.gameID(), game.whiteUsername(), user, game.gameName(), game.game());
                gameAccess.addGame(updateGame);
            }
            else {
                throw new DataAccessException("That color is already in use by another player");
            }
        }
        else {
            throw new DataAccessException("Invalid game");
        }
    }

    public void deleteAll() throws DataAccessException {
        gameAccess.deleteAll();
    }
}
