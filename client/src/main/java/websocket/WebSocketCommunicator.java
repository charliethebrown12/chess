package websocket;

import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

public class WebSocketCommunicator {

    private final WebSocketClient client;
    private final WebSocketHandler handler;
    private final String serverUrl;

    public WebSocketCommunicator(String serverUrl, ServerMessageObserver observer) {
        this.serverUrl = serverUrl;
        this.client = new WebSocketClient();
        this.handler = new WebSocketHandler(observer);
        try {
            client.start();
            client.connect(handler, new URI(serverUrl + "/ws")).get();
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
