import client.PreLoginRepl;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "http://localhost:4567";
        new PreLoginRepl(serverAddress).run();
    }
}