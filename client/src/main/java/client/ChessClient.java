package client;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;

public class ChessClient {
    private String username = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private AuthData authData;

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
                case "listgames" -> listGames();
                case "creategame" -> createGame(params);
                case "joingame" -> joinGame(params);
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
        var gson = new Gson();
        var result = new StringBuilder();
        for (var game : games) {
            result.append(gson.toJson(game)).append("\n");
        }
        return result.toString();
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var gameName = params[0];
            int gameID = server.createGame(authData, gameName);
            return String.format("Created game '%s' with gameID %d.", gameName, gameID);
        }
        throw new ResponseException(400, "Expected: create <GAME NAME>");
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            try {
                int gameID = Integer.parseInt(params[0]);
                var color = params[1].toUpperCase();
                if (!color.equals("WHITE") && !color.equals("BLACK")) {
                    throw new ResponseException(400, "Expected: <gameID> <WHITE|BLACK>");
                }
                server.joinGame(authData, gameID, color);
                return String.format("Joined game '%d' as %s.", gameID, username);
            } catch (ResponseException e) {
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: join <ID> <WHITE|BLACK>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - reigster <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play a game
                    - quit - exists program
                    - help - lists possible commands
                    """;
        }
        return """
                - create <GAME NAME> - creates a game
                - list - shows the list of all games
                - join <ID> <WHITE|BLACK> - joins a game
                - watch <ID> - watches a game
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
}
