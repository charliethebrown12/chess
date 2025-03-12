package dataaccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GamesList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameDAOTests {
    private static GameMySqlDataAccess gameDao;

    @BeforeAll
    static void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        gameDao = new GameMySqlDataAccess();
        gameDao.deleteAll();
    }

    @BeforeEach
    void reset() throws DataAccessException {
        gameDao.deleteAll();
    }

    @Test
    void testCreateGameSuccess() {
        assertDoesNotThrow(() -> {
            gameDao.createGame("test");
            gameDao.createGame("test2");
        });
    }

    @Test
    void testCreateGameFailure() {
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                gameDao.createGame(null)
        );

        assertEquals("Game creation failed Column 'gameName' cannot be null", exception.getMessage());
    }

    @Test
    void testGetGamesSuccess() throws DataAccessException {
        gameDao.createGame("test");
        gameDao.createGame("test2");
        List<GamesList> games = gameDao.getGames();
        assertEquals(2, games.size(), "Number of games should be 2");

    }

    @Test
    void testGetGamesNoGames() throws DataAccessException {
        List<GamesList> games = gameDao.getGames();
        assertEquals(0, games.size(), "Number of games should be 0");
    }

    @Test
    void testJoinGameSuccess() throws DataAccessException {
        int gameID = gameDao.createGame("test");
        gameDao.joinGame(gameID);
        List<GamesList> games = gameDao.getGames();
        assertEquals(1, games.size(), "Number of games should be 1");
    }

    @Test
    void testJoinGameFailure() {
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                gameDao.joinGame(100)
        );

        assertEquals("No rows affected, game not joined", exception.getMessage());
    }

    @Test void testDeleteAll() throws DataAccessException {
        gameDao.createGame("test");
        List<GamesList> games = gameDao.getGames();
        assertEquals(1, games.size(), "Number of games should be 1");
        gameDao.deleteAll();
        List<GamesList> games2 = gameDao.getGames();
        assertEquals(0, games2.size(), "Number of games should be 0");
    }
}
