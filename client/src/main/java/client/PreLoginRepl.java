package client;

import java.util.Scanner;

import static java.awt.Color.*;

public class PreLoginRepl {
    private final ChessClient client;

    public PreLoginRepl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess Client");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result + "\n");
                if (client.isSignedIn()) {
                    new PostLoginRepl(client).run();
                    break;
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + "[LOGGED_OUT] >>> ");
    }

}
