package service;

import dataAccess.DataAccessException;
import dataAccess.UserMemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private UserMemoryDataAccess userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserMemoryDataAccess(new ArrayList<>());
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

}