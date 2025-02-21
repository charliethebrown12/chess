package service;

import model.AuthData;

import java.util.UUID;

public class AuthService {
    public AuthData createAuth(String username) {
        UUID uuid = UUID.randomUUID();
        return new AuthData(uuid.toString(), username);
    }
}
