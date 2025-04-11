package server;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.ServerMessage;
import websocket.UserGameCommand;
import com.google.gson.Gson;

import java.io.IOException;

@WebSocket
public class ServerWebSocketHandler {

    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Server: WebSocket connected - " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Server: Message received - " + message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            ChessGame game = GameService.; // or load real one by gameID
            ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            response.setGame(game);
            session.getRemote().sendString(gson.toJson(response));
        } else {
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
}
