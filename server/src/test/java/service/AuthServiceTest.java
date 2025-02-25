package service;

import dataAccess.DataAccessException;
import dataAccess.AuthMemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;
    private AuthMemoryDataAccess authDAO;

    @BeforeEach
    void setUp() {
        authDAO = new AuthMemoryDataAccess(new ArrayList<>());
        authService = new AuthService(authDAO);
    }

    @Test
    void testCreateAuthSuccess() throws DataAccessException {
        AuthData auth = authService.createAuth("test");
        assertNotNull(auth, "User should not be null");
        assertEquals("test", auth.username(), "Username should match");
    }

    @Test
    void testCreateAuthNullUsername() {
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                authService.createAuth(null)
        );

        assertEquals("Username cannot be empty", exception.getMessage());
    }

}