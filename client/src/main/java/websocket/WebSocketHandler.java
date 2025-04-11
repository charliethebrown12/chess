package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final Gson gson = new Gson();
    private Session session;
    private final ServerMessageObserver observer;

    public WebSocketHandler(ServerMessageObserver observer) {
        this.observer = observer;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        System.out.println("WebSocket connection opened: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        observer.notify(serverMessage);
        }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket connection closed. Code: " + statusCode + ", Reason: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
    }

    public void send(UserGameCommand command) {
        if (session != null && session.isOpen()) {
            String json = gson.toJson(command);
            try {
                session.getRemote().sendString(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot send message, session is not open.");
        }
    }
    }