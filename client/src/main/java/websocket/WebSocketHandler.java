package websocket;

import com.google.gson.Gson;
import javax.websocket.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class WebSocketHandler {
    private final Gson gson = new Gson();
    private Session session;
    private final ServerMessageObserver observer;
    private final CountDownLatch connectLatch;

    public WebSocketHandler(ServerMessageObserver observer, CountDownLatch connectLatch) {
        this.observer = observer;
        this.connectLatch = connectLatch;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connection opened: " + session);
        connectLatch.countDown();
    }

    @OnMessage
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        observer.notify(serverMessage);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed. Reason: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
    }

    public void send(UserGameCommand command) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(gson.toJson(command));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot send message, session is not open.");
        }
    }
}
