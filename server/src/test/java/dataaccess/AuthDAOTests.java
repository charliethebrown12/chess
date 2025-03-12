package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {
    private static AuthMySqlDataAccess authDao;

    @BeforeAll
    static void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        authDao = new AuthMySqlDataAccess();
        authDao.deleteAll();
    }

    @BeforeEach
    void reset() throws DataAccessException {
        authDao.deleteAll();
    }

    @Test
    void testCreateAuthSuccess() throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        AuthData newAuth = new AuthData(uuid.toString(), "test");
        AuthData auth = authDao.createAuth(newAuth);
        assertNotNull(auth, "User should not be null");
        assertEquals("test", auth.username(), "Username should match");
    }

    @Test
    void testCreateAuthNonExistentUser() {
        AuthData auth = new AuthData("valid-token", "nonexistent-user");

        assertThrows(DataAccessException.class, () ->
                authDao.createAuth(auth)
        );
    }

    @Test
    void testGetAuth() throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        AuthData newAuth = new AuthData(uuid.toString(), "test");
        AuthData auth = authDao.createAuth(newAuth);
        auth = authDao.getAuth(auth.authToken());
        assertEquals(newAuth.username(), auth.username(), "Username should match");
    }

    @Test
    void testGetAuthNullAuthToken() throws DataAccessException {
        AuthData auth = authDao.getAuth(null);
        assertNull(auth, "Should return null");
    }

    @Test
    void testDeleteAuth() throws DataAccessException {
        AuthData newAuth = authDao.createAuth(new AuthData(UUID.randomUUID().toString(), "test"));
        AuthData getAuth = authDao.getAuth(newAuth.authToken());
        assertNotNull(getAuth, "Should not return null");
        authDao.deleteAuth(newAuth.authToken());
        getAuth = authDao.getAuth(newAuth.authToken());
        assertNull(getAuth, "Should return null");

    }

    @Test
    void testDeleteAuthDNE() throws DataAccessException {
        AuthData getAuth = authDao.getAuth("test");
        assertNull(getAuth, "Should return null");
        authDao.deleteAuth("test");
        getAuth = authDao.getAuth("test");
        assertNull(getAuth, "Should return null");
    }

    @Test
    void testGetUser() throws DataAccessException {
        AuthData newAuth = authDao.createAuth(new AuthData(UUID.randomUUID().toString(), "test"));
        String user = authDao.getUsername(newAuth.authToken());
        assertEquals("test", user, "Username should match");
    }

    @Test
    void testGetUserDNE() throws DataAccessException {
        AuthData newAuth = authDao.createAuth(new AuthData(UUID.randomUUID().toString(), "test"));
        String user = authDao.getUsername(newAuth.authToken());
        assertNotEquals("test1", user, "Username should not match");
    }

    @Test void testDeleteAll() throws DataAccessException {
        AuthData newAuth = authDao.createAuth(new AuthData(UUID.randomUUID().toString(), "test"));
        assert(!newAuth.authToken().isEmpty());
        authDao.deleteAll();
        assertNull(authDao.getAuth(newAuth.authToken()));
    }
}
