import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(4567);
        System.out.println("Server running on port " + port);
    }
}