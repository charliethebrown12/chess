package websocket;

public interface ServerMessageObserver {
    void notify(ServerMessage message);
}
