package service;

import dataaccess.DataAccessException;
import dataaccess.GameMemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService gameService;

    @BeforeEach
    void setUp() {
        GameMemoryDataAccess gameDAO = new GameMemoryDataAccess();
        gameService = new GameService(gameDAO);
    }

    @Test
    void testCreateGameSuccess() throws DataAccessException {
        int game = gameService.createGame("test");
        int game2 = gameService.createGame("test2");
        assertEquals(1, game, "gameId should be 1");
        assertEquals(2, game2, "gameId should be 2");
    }

    @Test
    void testCreateGameFailure() {
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                gameService.createGame(null)
        );

        assertEquals("Game name cannot be empty", exception.getMessage());
    }

    @Test
    void testGetGamesSuccess() throws DataAccessException {
        gameService.createGame("test");
        gameService.createGame("test2");
        List<GamesList> games = gameService.getGames();
        assertEquals(2, games.size(), "Number of games should be 2");

    }

    @Test
    void testGetGamesNoGames() throws DataAccessException {
        List<GamesList> games = gameService.getGames();
        assertEquals(0, games.size(), "Number of games should be 0");
    }

    @Test
    void testJoinGameSuccess() throws DataAccessException {
        gameService.createGame("test");
        gameService.joinGame(1, "WHITE", "testUser");
        List<GamesList> games = gameService.getGames();
        assertEquals(1, games.size(), "Number of games should be 1");
    }

    @Test
    void testJoinGameFailure() throws DataAccessException {
        int gameID = gameService.createGame("test");
        gameService.joinGame(gameID, "WHITE", "testUser");
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                gameService.joinGame(gameID, "WHITE", "test")
        );

        assertEquals("That color is already in use by another player", exception.getMessage());
    }

    @Test void testDeleteAll() throws DataAccessException {
        gameService.createGame("test");
        List<GamesList> games = gameService.getGames();
        assertEquals(1, games.size(), "Number of games should be 1");
        gameService.deleteAll();
        assertEquals(0, games.size(), "Number of games should be 0");
    }
}
