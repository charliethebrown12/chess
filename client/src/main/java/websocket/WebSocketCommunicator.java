package websocket;

import com.google.gson.Gson;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import websocket.commands.UserGameCommand;

public class WebSocketCommunicator {

    private final WebSocketHandler handler;

    public WebSocketCommunicator(String serverUrl, ServerMessageObserver observer) {
        String serverUrl1 = serverUrl.startsWith("http")
                ? serverUrl.replaceFirst("http", "ws")
                : "ws://" + serverUrl;

        CountDownLatch connectLatch = new CountDownLatch(1);
        this.handler = new WebSocketHandler(observer, connectLatch);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            System.out.println("Connecting to WebSocket: " + serverUrl1 + "/ws");
            container.connectToServer(handler, new URI(serverUrl1 + "/ws"));
            connectLatch.await();
            System.out.println("Connected to " + serverUrl + "/ws");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(UserGameCommand command) {
        handler.send(command);
    }

    public void close() {
        // Note: Tyrus handles closing connections via the Session close.
        System.out.println("WebSocket connection closed.");
    }
}
