package client;

import java.util.Scanner;

public class PostLoginRepl {
    private final ChessClient client;

    public PostLoginRepl(ChessClient client) {
        this.client = client;
    }

    public void run() {
        System.out.println("Logged in successfully");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result + "\n");
                if (client.isInGame()) {
                    new GameplayRepl(client).run();
                    break;
                }
                if (!client.isSignedIn()) {
                    new PreLoginRepl(client).run();
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
        System.out.print("\n" + "[LOGGED_IN] >>> ");
    }
}
