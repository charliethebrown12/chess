package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserDAOTests {
    private static UserMySqlDataAccess userDao;

    @BeforeAll
    static void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();
        userDao = new UserMySqlDataAccess();
        userDao.deleteAll();
    }

    @BeforeEach
    void reset() throws DataAccessException {
        userDao.deleteAll();
    }

    @Test
    void testCreateUserSuccess() throws DataAccessException {
        UserData user = userDao.createUser("test", "password", "test@gmail.com");
        assertNotNull(user, "User should not be null");
        assertEquals("test", user.username(), "Username should match");
        assertEquals("test@gmail.com", user.email(), "Email should match");
    }

    @Test
    void testCreateUserDuplicate() throws DataAccessException {
        assertDoesNotThrow(() -> userDao.createUser("testuser", "password123", "test@email.com"));
        assertThrows(DataAccessException.class, () -> userDao.createUser("testuser", "newpass", "newemail@email.com"));
    }

    @Test
    void testGetUserSuccess() throws DataAccessException {
        UserData user = userDao.createUser("test", "password", "test@example.com");
        assertNotNull(user, "User should not be null");
        UserData user2 = userDao.getUser("test", "password");
        assertNotNull(user2, "User should not be null");
        assertEquals("test", user2.username(), "Username should match");
    }

    @Test
    void testGetUserNotFound() throws DataAccessException {
        UserData nothing = userDao.getUser("test", "differentpassword");
        assertNull(nothing);
    }

    @Test
    void testDeleteAll() throws DataAccessException {
        userDao.createUser("test", "password", "test@example.com");
        UserData user = userDao.getUser("test", "password");
        assertNotNull(user, "User should not be null");
        userDao.deleteAll();
        UserData user1 = userDao.getUser("test", "password");
        assertNull(user1);
    }
}
