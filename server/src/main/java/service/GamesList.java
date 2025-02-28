package service;

import model.GameData;

public class GamesList {
    private final int gameID;
    private String whiteUsername;
    private String blackUsername;
    private final String gameName;

    public GamesList(GameData game) {
        this.gameID = game.gameID();
        this.whiteUsername = game.whiteUsername();
        this.blackUsername = game.blackUsername();
        this.gameName = game.gameName();
    }

    public int getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getGameName() {
        return gameName;
    }
}
