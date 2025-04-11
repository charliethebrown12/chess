package websocket;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import websocket.commands.UserGameCommand;

import java.net.URI;

public class WebSocketCommunicator {

    private final WebSocketClient client;
    private final WebSocketHandler handler;

    public WebSocketCommunicator(String serverUrl, ServerMessageObserver observer) {
        String serverUrl1 = serverUrl.startsWith("http")
                ? serverUrl.replaceFirst("http", "ws")
                : "ws://" + serverUrl;
        this.client = new WebSocketClient();
        this.handler = new WebSocketHandler(observer);
        try {
            System.out.println("Connecting to WebSocket: " + serverUrl1 + "/ws");
            client.start();
            client.connect(handler, new URI(serverUrl1 + "/ws")).get();
            System.out.println("Connected to " + serverUrl + "/ws");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(UserGameCommand command) {
        handler.send(command);
    }

    public void close() {
        try {
            if (client != null && client.isStarted()) {
                client.stop();
                System.out.println("WebSocket connection closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
