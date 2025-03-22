package client;

import exception.ResponseException;

import java.util.Scanner;

import static java.awt.Color.*;

public class PostLoginRepl {
    private final ChessClient client;

    public PostLoginRepl(ChessClient client) {
        this.client = client;
    }

    public void run() throws ResponseException {
        System.out.println("Logged in successfully");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(RED + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> " + GREEN);
    }
}
