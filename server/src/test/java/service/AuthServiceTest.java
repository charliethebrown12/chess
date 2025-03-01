package service;

import dataAccess.DataAccessException;
import dataAccess.AuthMemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;

    @BeforeEach
    void setUp() {
        AuthMemoryDataAccess authDAO = new AuthMemoryDataAccess();
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

    @Test
    void testGetAuth() throws DataAccessException {
        AuthData auth = authService.createAuth("test");
        boolean t = authService.getAuth(auth.authToken());
        assertTrue(t);
    }

    @Test
    void testGetAuthNullAuthToken() throws DataAccessException {
        boolean t = authService.getAuth(null);
        assertFalse(t);
    }

    @Test
    void testDeleteAuth() throws DataAccessException {
        AuthData auth = authService.createAuth("test");
        boolean before = authService.getAuth(auth.authToken());
        assertTrue(before);
        authService.deleteAuth(auth.authToken());
        boolean after = authService.getAuth(auth.authToken());
        assertFalse(after);

    }

    @Test
    void testDeleteAuthDNE() throws DataAccessException {
        boolean before = authService.getAuth("test");
        assertFalse(before);
        authService.deleteAuth("test");
        boolean after = authService.getAuth("test");
        assertFalse(after);
    }

    @Test
    void testGetUser() throws DataAccessException {
        AuthData auth = authService.createAuth("test");
        String user = authService.getUser(auth.authToken());
        assertEquals("test", user, "Username should match");
    }

    @Test
    void testGetUserDNE() throws DataAccessException {
        AuthData auth = authService.createAuth("test");
        String user = authService.getUser(auth.authToken());
        assertNotEquals("test1", user, "Username should not match");
    }

}