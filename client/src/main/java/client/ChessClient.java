package client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;

public class ChessClient {
    private String username = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private AuthData authData;
    private String userColor;
    private Map<Integer, Integer> gameNumberMapping;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> watchGame(params);
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "makemove" -> makeMove(params);
                case "resign" -> resignGame();
                case "highlight" -> highlightMoves(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            username = params[0];
            String password = params[1];
            String email = params[2];
            this.authData = server.register(username, password, email);
            state = State.SIGNEDIN;
            return String.format("Thank you for registering as %s. You are now logged in.", username);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            username = params[0];
            String password = params[1];
            this.authData = server.login(username, password);
            state = State.SIGNEDIN;
            return String.format("You signed in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        server.logout(authData);
        state = State.SIGNEDOUT;
        return String.format("Logged out as %s.", username);
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        var games = server.listGames(authData);
        if (games == null) {
            return "There are no games in this server.";
        }
        var result = new StringBuilder("Available Games:\n");
        Map<Integer, Integer> gameNumberMap = new HashMap<>(); // Maps display numbers to actual game IDs
        int displayNum = 1;

        for (var game : games) {
            gameNumberMap.put(displayNum, game.gameID()); // Store mapping
            result.append(displayNum)
                    .append(". ")
                    .append(game.gameName())
                    .append(" (Players: ")
                    .append(" WhiteUsername - ")
                    .append(game.whiteUsername() != null ? game.whiteUsername() : "Empty")
                    .append(" vs ")
                    .append(" BlackUsername - ")
                    .append(game.blackUsername() != null ? game.blackUsername() : "Empty")
                    .append(")\n");
            displayNum++;
        }

        // Store the mapping for later use when joining a game
        this.gameNumberMapping = gameNumberMap;
        return result.toString();
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var gameName = params[0];
            server.createGame(authData, gameName);
            return String.format("Created game '%s'", gameName);
        }
        throw new ResponseException(400, "Expected: create <GAME NAME>");
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            try {
                int displayID = Integer.parseInt(params[0]);
                if (gameNumberMapping == null || !gameNumberMapping.containsKey(displayID)) {
                    throw new ResponseException(400, "GameID does not exist.");
                }
                int gameID = gameNumberMapping.get(displayID);
                var color = params[1].toUpperCase();
                if (!color.equals("WHITE") && !color.equals("BLACK")) {
                    throw new ResponseException(400, "Expected: <gameID> <WHITE|BLACK>");
                }
                server.joinGame(authData, gameID, color);
                state = State.INGAME;
                userColor = color;
                return String.format("Joined game '%d' as %s.", displayID, username);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "<ID> must be a number.");
            }
        }
        throw new ResponseException(400, "Expected: join <ID> <WHITE|BLACK>");
    }

    public String watchGame(String... params) throws ResponseException {
        if (params.length == 1) {
            userColor = "WHITE";
            state = State.INGAME;
            return String.format("Watching game '%s' as an observer", params[0]);
        }
        throw new ResponseException(400, "Expected: observe <ID>");
    }

    public void printBoard() {
        String[][] board = {
                {"♖", "♘", "♗", "♔", "♕", "♗", "♘", "♖"},
                {"♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙"},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {"♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟"},
                {"♜", "♞", "♝", "♚", "♛", "♝", "♞", "♜"}
        };
        boolean isWhitePlayer = userColor.equals("WHITE");

        String columns;
        if (isWhitePlayer) {
            columns = "   a  b  c  d  e  f  g  h";
        } else {
            columns = "   h  g  f  e  d  c  b  a";
        }
        System.out.println(columns);

        for (int i = 0; i < 8; i++) {
            int row;
            if (isWhitePlayer) {
                row = i;
            } else {
                row = 7 - i;
            }
            System.out.print((8 - row) + " ");

            for (int j = 0; j < 8; j++) {
                int col;
                if (isWhitePlayer) {
                    col = 7 - j;
                } else {
                    col = j;
                }
                String squareColor = ((row + col) % 2 == 0) ? "\u001B[40m" : "\u001B[100m";
                String pieceColor = (row < 2) ? "\u001B[32m" : (row > 5) ? "\u001B[34m" : "\u001B[0m";
                String piece = board[row][col].equals(" ") ? "\u2003" + "  " : "\u2003" + pieceColor + board[row][col] + "\u2003";

                System.out.print(squareColor + piece + "\u001B[0m");
            }

            System.out.println(" " + (8 - row));
        }

        System.out.println(columns); // Column labels at the bottom
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play a game
                    - quit - exists program
                    - help - lists possible commands
                    """;
        }
        return """
                - create <GAME NAME> - creates a game
                - list - shows the list of all games
                - join <ID> <WHITE|BLACK> - joins a game
                - observe <ID> - watches a game
                - logout - logs out player
                - quit - exits program
                - help - lists possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    public boolean isSignedIn() {
        return state == State.SIGNEDIN;
    }

    public void quitGame() {
        state = State.SIGNEDIN;
    }

    public boolean isInGame() { return state == State.INGAME; }
}
