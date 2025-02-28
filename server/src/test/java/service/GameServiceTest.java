package service;

import dataAccess.DataAccessException;
import dataAccess.GameMemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testGetGamesNoGames() {
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                gameService.getGames()
        );

        assertEquals("No games found. Please create a new game.", exception.getMessage());
    }
}
