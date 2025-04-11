package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.AuthMySqlDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameMySqlDataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameManager;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ServerWebSocketHandler {

    private final Gson gson = new Gson();
    private static final Map<Integer, List<Session>> gameSessions = new ConcurrentHashMap<>();
    private static final Set<Integer> endedGames = ConcurrentHashMap.newKeySet();


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
                String username = new AuthMySqlDataAccess().getUsername(command.getAuthToken());
                if (username == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    error.setErrorMessage("Error: Invalid auth token.");
                    session.getRemote().sendString(gson.toJson(error));
                    session.close();
                    return;
                }
                ServerMessage joinNotice = getJoinNotice(username, gameData);

                String notice = gson.toJson(joinNotice);

                List<Session> sessions = gameSessions.getOrDefault(gameID, List.of());
                for (Session s : sessions) {
                    if (!s.equals(session) && s.isOpen()) {
                        s.getRemote().sendString(notice);
                    }
                }
                ChessGame game = GameManager.getInstance().getGame(gameID);

                if (game == null) {
                    game = gameData.game();
                    GameManager.getInstance().updateGame(gameID, game);
                }

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
            int gameID = command.getGameID();
            String username = new AuthMySqlDataAccess().getUsername(command.getAuthToken());
            if (username == null) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: Invalid auth token.");
                session.getRemote().sendString(gson.toJson(error));
                session.close();
                return;
            }
            GameData gameData = new GameMySqlDataAccess().joinGame(gameID);

            if (endedGames.contains(gameID)) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game is already over.");
                session.getRemote().sendString(gson.toJson(error));
                return;
            }
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
                ChessPosition from = move.getStartPosition();
                ChessPiece piece = game.getBoard().getPiece(from);

                if (piece == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    error.setErrorMessage("Error: No piece at the given position.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                ChessGame.TeamColor team = piece.getTeamColor();
                boolean isPlayerTurn = team == game.getTeamTurn();
                boolean isCorrectPlayer = (team == ChessGame.TeamColor.WHITE && username.equals(gameData.whiteUsername())) ||
                        (team == ChessGame.TeamColor.BLACK && username.equals(gameData.blackUsername()));

                if (!isPlayerTurn || !isCorrectPlayer) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    error.setErrorMessage("Error: It's not your turn or you're moving your opponent's piece.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                game.makeMove(move);
                GameManager.getInstance().updateGame(gameID, game);
                new GameMySqlDataAccess().updateGameState(gameID, game);
                System.out.println("Move applied: " + move);


                broadcastGameState(gameID, game);
                String moveNotation = moveToString(move);
                String messageText = username + " moved " + moveNotation;
                sendNotification(gameID, messageText, session);
                gameNotifications(gameID, game);

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

            GameData gameData = new GameMySqlDataAccess().joinGame(gameID);
            String username = new AuthMySqlDataAccess().getUsername(authToken);
            if (username == null) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: Invalid auth token.");
                session.getRemote().sendString(gson.toJson(error));
                session.close();
                return;
            }
            boolean isPlayer = username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername());
            if (!isPlayer) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Observers are not allowed to resign.");
                session.getRemote().sendString(gson.toJson(error));
                return;
            }
            if (endedGames.contains(gameID)) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game is already over.");
                session.getRemote().sendString(gson.toJson(error));
                return;
            }

            GameManager.getInstance().removeGame(gameID);
            endedGames.add(gameID);

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
            if (username == null) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: Invalid auth token.");
                session.getRemote().sendString(gson.toJson(error));
                session.close();
                return;
            }
            ServerMessage leaveNotice = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            leaveNotice.setNotification(username + " has left the game.");

            String result = gson.toJson(leaveNotice);
            assert sessions != null;
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

    private static ServerMessage getJoinNotice(String username, GameData gameData) {
        String joinType;
        if (username.equals(gameData.whiteUsername())) {
            joinType = "WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            joinType = "BLACK";
        } else {
            joinType = "an observer";
        }
        ServerMessage joinNotice = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        joinNotice.setNotification(username + " joined the game as " + joinType);
        return joinNotice;
    }

    private String moveToString(ChessMove move) {
        return squareToString(move.getStartPosition()) + " to " + squareToString(move.getEndPosition());
    }

    private String squareToString(ChessPosition position) {
        char file = (char) ('a' + position.getColumn());
        int rank = 8 - position.getRow();
        return "" + file + (rank + 1);
    }

    private void gameNotifications(int gameID, ChessGame game) throws IOException {
        ChessGame.TeamColor nextTurn = game.getTeamTurn();

        boolean checkmate = game.isInCheckmate(nextTurn);
        boolean stalemate = game.isInStalemate(nextTurn);
        boolean check = game.isInCheck(nextTurn);

        List<String> notifications = new ArrayList<>();

        if (checkmate) {
            notifications.add(nextTurn + " is in checkmate. Game over.");
            endedGames.add(gameID);
        } else if (stalemate) {
            notifications.add("Game is a stalemate.");
            endedGames.add(gameID);
        } else if (check) {
            notifications.add(nextTurn + " is in check.");
        }

        for (String note : notifications) {
            ServerMessage status = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            status.setNotification(note);
            String json = gson.toJson(status);

            for (Session s : gameSessions.getOrDefault(gameID, List.of())) {
                if (s.isOpen()) {
                    s.getRemote().sendString(json);
                }
            }
        }
    }

    private void sendNotification(int gameID, String message, Session currentSession) {
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        msg.setNotification(message);
        String json = gson.toJson(msg);

        for (Session s : gameSessions.getOrDefault(gameID, List.of())) {
            if (s.isOpen() && !s.equals(currentSession)) {
                try {
                    s.getRemote().sendString(json);
                } catch (IOException e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
            }
        }
    }
}
