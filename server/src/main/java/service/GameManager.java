package service;

import chess.ChessGame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private static final GameManager instance = new GameManager();
    private final Map<Integer, ChessGame> games = new ConcurrentHashMap<>();

    private GameManager() {}

    public static GameManager getInstance() {
        return instance;
    }

    public ChessGame getGame(int gameID) {
        return games.get(gameID);
    }

    public void createGame(int gameID) {
        games.putIfAbsent(gameID, new ChessGame());
    }

    public void updateGame(int gameID, ChessGame updated) {
        games.put(gameID, updated);
    }

    public void removeGame(int gameID) {
        games.remove(gameID);
    }

    public boolean hasGame(int gameID) {
        return games.containsKey(gameID);
    }
}
