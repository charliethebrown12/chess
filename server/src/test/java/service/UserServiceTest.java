package service;

import dataAccess.DataAccessException;
import dataAccess.UserMemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserMemoryDataAccess userDAO = new UserMemoryDataAccess();
        userService = new UserService(userDAO);
    }

    @Test
    void testCreateUserSuccess() throws DataAccessException {
        UserData user = userService.createUser("test", "password", "test@gmail.com");
        assertNotNull(user, "User should not be null");
        assertEquals("test", user.username(), "Username should match");
        assertEquals("test@gmail.com", user.email(), "Email should match");
    }

    @Test
    void testCreateUserDuplicate() throws DataAccessException {
        assertDoesNotThrow(() -> userService.createUser("testuser", "password123", "test@example.com"));
        UserData user = userService.createUser("testuser", "newpass", "newemail@example.com");
        assertNull(user);
    }

    @Test
    void testGetUserSuccess() throws DataAccessException {
        UserData user = userService.createUser("test", "password", "test@example.com");
        assertNotNull(user, "User should not be null");
        UserData user2 = userService.getUser("test", "password");
        assertNotNull(user2, "User should not be null");
        assertEquals("test", user2.username(), "Username should match");
    }

    @Test
    void testGetUserNotFound() throws DataAccessException {
        userService.createUser("test", "password", "test@example.coom");
        UserData nothing = userService.getUser("test", "differentpassword");
        assertNull(nothing);
    }

    @Test void testDeleteAll() throws DataAccessException {
        userService.createUser("test", "password", "test@example.com");
        UserData user = userService.getUser("test", "password");
        assertNotNull(user, "User should not be null");
        userService.deleteAll();
        UserData user1 = userService.getUser("test", "password");
        assertNull(user1);
    }

}