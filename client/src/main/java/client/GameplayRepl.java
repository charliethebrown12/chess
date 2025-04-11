package client;

import java.util.Scanner;

public class GameplayRepl {
    private final ChessClient client;

    public GameplayRepl(ChessClient client) {
        this.client = client;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            var input = scanner.nextLine();
            if (result.equals("leave")) {
                client.quitGame();
                new PostLoginRepl(client).run();
                break;
            }
            try {
                result = client.eval(input);
                System.out.println(result);
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }


    }

    private void printPrompt() {
        System.out.print("\n" + "[IN_GAME] >>> ");
    }

}
