package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameMemoryDataAccess implements GameAccess {
    final List<GameData> games;
    int counter;

    public GameMemoryDataAccess() {
        this.counter = 1;
        this.games = new ArrayList<>();
    }

    public int createGame(String gameName) throws DataAccessException {
        try {
            GameData newGame = new GameData(counter, null, null, gameName, new ChessGame());
            this.games.add(newGame);
            counter += 1;
            System.out.println(games);
            return newGame.gameID();
        }
        catch (Exception e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }
}
