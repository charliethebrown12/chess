package dataAccess;

import chess.ChessGame;
import model.GameData;
import service.GamesList;

import java.util.ArrayList;
import java.util.List;

public class GameMemoryDataAccess implements GameAccess {
    final List<GameData> games;
    final List<GamesList> gamesList;
    int counter;

    public GameMemoryDataAccess() {
        this.gamesList = new ArrayList<>();
        this.counter = 1;
        this.games = new ArrayList<>();
    }

    public int createGame(String gameName) throws DataAccessException {
        try {
            GameData newGame = new GameData(counter, null, null, gameName, new ChessGame());
            GamesList newGamesList = new GamesList(newGame);
            this.games.add(newGame);
            this.gamesList.add(newGamesList);
            counter += 1;
            System.out.println(games);
            return newGame.gameID();
        }
        catch (Exception e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    public List<GamesList> getGames() throws DataAccessException {
        try {
            return gamesList;
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData joinGame(int gameID) throws DataAccessException {
        for (GameData game : games) {
            if (gameID == game.gameID()) {
                return game;
            }
        }
        throw new DataAccessException("Game ID does not exist.");
    }

    public void deleteGame(int gameID) {
        gamesList.removeIf(game -> game.getGameID() == gameID);
        games.removeIf(game -> gameID == game.gameID());
    }

    public void addGame(GameData game) {
        games.add(game);
        GamesList newGamesList = new GamesList(game);
        gamesList.add(newGamesList);
    }

    public void deleteAll() {
        games.clear();
        gamesList.clear();
    }
}
