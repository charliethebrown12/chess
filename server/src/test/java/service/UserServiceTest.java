package service;

import dataAccess.DataAccessException;
import dataAccess.UserMemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

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
    void testCreateUserDuplicate() {
        assertDoesNotThrow(() -> userService.createUser("testuser", "password123", "test@example.com"));

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                userService.createUser("testuser", "newpass", "newemail@example.com")
        );

        assertEquals("Error creating user: User already exists", exception.getMessage());
    }

    @Test
    void testGetUserSuccess() throws DataAccessException {
        UserData user = userService.createUser("test", "password", "test@example.com");
        assertNotNull(user, "User should not be null");
        UserData user2 = userService.getUser("test");
        assertNotNull(user2, "User should not be null");
        assertEquals("test", user2.username(), "Username should match");
    }

    @Test
    void testGetUserNotFound() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> userService.getUser("notausername"));
        assertEquals("Error getting user: User not found", exception.getMessage());
    }

}