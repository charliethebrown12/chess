package server;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.AuthMySqlDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameMySqlDataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameManager;
import websocket.ServerMessage;
import websocket.UserGameCommand;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ServerWebSocketHandler {

    private final Gson gson = new Gson();
    private static final Map<Integer, List<Session>> gameSessions = new ConcurrentHashMap<>();


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Server: WebSocket connected - " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        System.out.println("Server: Message received - " + message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            gameSessions.computeIfAbsent(command.getGameID(), id -> new ArrayList<>()).add(session);
            int gameID = command.getGameID();
            try {
                GameData gameData = new GameMySqlDataAccess().joinGame(gameID);
                ChessGame game = gameData.game();

                GameManager.getInstance().updateGame(gameID, game);

                ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                System.out.println("Sending LOAD_GAME for gameID: " + gameID);
                response.setGame(game);

                session.getRemote().sendString(gson.toJson(response));

            } catch (DataAccessException e) {
                System.err.println("Failed to load game: " + e.getMessage());

                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Could not load game: " + e.getMessage());
                session.getRemote().sendString(gson.toJson(error));
            }
        }
        else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            String username = new AuthMySqlDataAccess().getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            GameData gameData = new GameMySqlDataAccess().joinGame(gameID);
            boolean isPlayer = username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername());
            if (!isPlayer) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Observers are not allowed to make moves.");
                session.getRemote().sendString(gson.toJson(error));
                return;
            }
            ChessMove move = command.getMove();

            ChessGame game = GameManager.getInstance().getGame(gameID);
            if (game == null) {
                System.err.println("No game found in memory for gameID " + gameID);
                return;
            }

            try {
                game.makeMove(move);
                GameManager.getInstance().updateGame(gameID, game);
                new GameMySqlDataAccess().updateGameState(gameID, game);
                System.out.println("Move applied: " + move);


                broadcastGameState(gameID, game);

            } catch (Exception e) {
                System.err.println("Invalid move: " + e.getMessage());
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Invalid move: " + e.getMessage());
                session.getRemote().sendString(gson.toJson(error));
            }
        }
        else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            GameManager.getInstance().removeGame(gameID); // if you're done with the game

            // Notify all players
            String username = new AuthMySqlDataAccess().getUsername(authToken);
            String noticeText = username + " has resigned. Game over.";
            ServerMessage resignNotice = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            resignNotice.setNotification(noticeText);

            String result = gson.toJson(resignNotice);
            List<Session> sessions = gameSessions.getOrDefault(gameID, List.of());
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getRemote().sendString(result);
                    } catch (IOException e) {
                        System.err.println("Failed to notify session: " + e.getMessage());
                    }
                }
            }

            System.out.println("Player resigned from game " + gameID);
        }
        else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            int gameID = command.getGameID();

            List<Session> sessions = gameSessions.get(gameID);
            if (sessions != null) {
                sessions.removeIf(s -> s.equals(session));
                System.out.println("Player left game " + gameID);
            }

            String username = new AuthMySqlDataAccess().getUsername(command.getAuthToken());
            ServerMessage leaveNotice = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            leaveNotice.setNotification(username + " has left the game.");

            String result = gson.toJson(leaveNotice);
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getRemote().sendString(result);
                    } catch (IOException e) {
                        System.err.println("Failed to send leave notification: " + e.getMessage());
                    }
                }
            }
        }
        else {
            ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            response.setNotification("Command received: " + command.getCommandType());
            session.getRemote().sendString(gson.toJson(response));
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Server: WebSocket closed. Code: " + statusCode + ", Reason: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("Server: WebSocket error - " + error.getMessage());
    }

    private void broadcastGameState(int gameID, ChessGame game) {
        ServerMessage update = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        update.setGame(game);
        String message = gson.toJson(update);

        List<Session> sessions = gameSessions.getOrDefault(gameID, List.of());
        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    s.getRemote().sendString(message);
                } catch (IOException e) {
                    System.err.println("Failed to send update to session: " + e.getMessage());
                }
            }
        }
    }
}
