package client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void reset() throws ResponseException {
        facade.deleteAll();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        var authData = facade.register("test1", "testpassword", "test@email");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerFail() throws ResponseException {
        facade.register("test1", "testpassword", "test@email");
        assertThrows(ResponseException.class, () -> facade.register("test1", "testpassword", "test@email"));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        facade.register("test1", "testpassword", "test@email");
        var authData = facade.login("test1", "testpassword");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginFail() throws ResponseException {
        facade.register("test1", "testpassword", "test@email");
        assertThrows(ResponseException.class, () -> facade.login("test1", "cheese"));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        var authData = facade.register("test1", "testpassword", "test@email");
        facade.logout(authData);
        assertThrows(ResponseException.class, () -> facade.logout(authData));
    }

    @Test
    public void logoutFail() {
        AuthData authData = new AuthData("BADAUTHTOKEN", "test1");
        assertThrows(ResponseException.class, () -> facade.logout(authData));
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        var authData = facade.register("test1", "testpassword", "test@email");
        int gameID = facade.createGame(authData, "testgame");
        assertInstanceOf(Integer.class, gameID);
    }

    @Test
    public void createGameFail() {
        AuthData authData = new AuthData("BADAUTHTOKEN", "test1");
        assertThrows(ResponseException.class, () -> facade.createGame(authData, "testgame"));
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        var authData = facade.register("test1", "testpassword", "test@email");
        GameData[] gamesList = facade.listGames(authData);
        assertNotNull(gamesList);
    }

    @Test
    public void listGamesFail() {
        AuthData authData = new AuthData("BADAUTHTOKEN", "test1");
        assertThrows(ResponseException.class, () -> facade.listGames(authData));
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        var authData = facade.register("test1", "testpassword", "test@email");
        int gameID = facade.createGame(authData, "testgame");
        facade.joinGame(authData, gameID, "WHITE");
        GameData[] gamesList = facade.listGames(authData);
        String gamesListString = Arrays.toString(gamesList);
        boolean usernameExists = gamesListString.contains("test1");
        assertTrue(usernameExists, "Username should exist in the list of games string");
    }

    @Test
    public void joinGameFail() throws ResponseException {
        var authData = facade.register("test1", "testpassword", "test@email");
        assertThrows(ResponseException.class, () -> facade.joinGame(authData, 1, "WHITE"));
    }

    @Test
    public void deleteAllSuccess() throws ResponseException {
        facade.register("test1", "testpassword", "test@email");
        facade.deleteAll();
        assertThrows(ResponseException.class, () -> facade.login("test1", "testpassword"));
    }
}
