package client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import chess.ChessMove;
import chess.ChessPosition;
import websocket.*;
import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;

public class ChessClient implements ServerMessageObserver {
    private String username = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private AuthData authData;
    private String userColor;
    private Map<Integer, Integer> gameNumberMapping;
    private int gameID;
    private WebSocketCommunicator websocket;
    private ChessGame game;
    private final String serverUrl;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
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
                gameID = gameNumberMapping.get(displayID);
                var color = params[1].toUpperCase();
                if (!color.equals("WHITE") && !color.equals("BLACK")) {
                    throw new ResponseException(400, "Expected: <gameID> <WHITE|BLACK>");
                }
                server.joinGame(authData, gameID, color);

                websocket = new WebSocketCommunicator(serverUrl, this);
                UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData.authToken(), gameID);
                websocket.send(command);
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
        assertSignedIn();
        if (params.length == 1) {
            try {
                gameID = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "<ID> must be a number");
            }
            websocket = new WebSocketCommunicator(serverUrl, this);
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData.authToken(), gameID);
            websocket.send(command);
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

    public String redrawBoard() throws ResponseException {
        assertInGame();
        printBoard();
        return "Board redrawn";
    }

    public String leaveGame() throws ResponseException {
        assertInGame();
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authData.authToken(), gameID);
        websocket.send(command);
        quitGame();
        websocket.close();
        return "You have left the game";
    }

    public String makeMove(String... params) throws ResponseException {
        assertInGame();
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: makemove <STARTSQUARE> <ENDSQUARE>");
        }
        String start = params[0];
        String end = params[1];
        ChessPosition startPos = parseSquare(start, userColor);
        ChessPosition endPos = parseSquare(end, userColor);
        ChessMove move = new ChessMove(startPos, endPos, null);
        UserGameCommand moveCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authData.authToken(), gameID, move);
        websocket.send(moveCommand);
        return String.format("Move %s to %s send to server", start, end);
    }

    public String resignGame() throws ResponseException {
        assertInGame();
        UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authData.authToken(), gameID);
        websocket.send(resign);
        state = State.SIGNEDIN;
        return "You have resigned the game";
    }

    public String highlightMoves(String... params) throws ResponseException {
        assertInGame();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: highlight <SQUARE>");
        }
        String square = params[0];
        ChessPosition position = parseSquare(square, userColor);
        if (game == null) {
            return "No game state available to highlight moves";
        }
        var legalMoves = game.validMoves(position);
        return "Highlighted legal moves for piece at " + square + ": " + legalMoves;
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
        if (state == State.INGAME) {
            return """
                    - redraw - redraws the game board
                    - leave - leaves the current game
                    - makemove <MOVE> - implements a move in the game
                    - resign - leaves the game and causes game to end
                    - highlight <POSITION> - highlights possible moves for piece at given position
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

    private void assertInGame() throws ResponseException {
        if (!(state == State.INGAME)) {
            throw new ResponseException(400, "You must join a game");
        }
    }

    public boolean isSignedIn() {
        return state == State.SIGNEDIN;
    }

    public void quitGame() {
        state = State.SIGNEDIN;
    }

    public boolean isInGame() { return state == State.INGAME; }

    private ChessPosition parseSquare(String notation, String userColor) {
        int col = notation.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(notation.charAt(1));
        return new ChessPosition(row, col);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                this.game = message.getGame();
                printBoard();
            }
            case ERROR -> System.out.println("Error from server: " + message.getErrorMessage());
            case NOTIFICATION -> System.out.println("Notification: " + message.getNotification());
        }
    }
}
