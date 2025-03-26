package client;

import java.util.Scanner;

import static java.awt.Color.*;

public class GameplayRepl {
    private final ChessClient client;

    public GameplayRepl(ChessClient client) {
        this.client = client;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            client.printBoard();
            printPrompt();
            result = scanner.nextLine();
        }


    }

    private void printPrompt() {
        System.out.print("\n" + "[IN_GAME] >>> ");
    }

}
