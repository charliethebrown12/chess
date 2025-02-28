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
            GameData newGame = new GameData(counter, "", "", gameName, new ChessGame());
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
            if (!games.isEmpty()) {
                return gamesList;
            }
            else {throw new DataAccessException("No games found. Please create a new game.");}
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
