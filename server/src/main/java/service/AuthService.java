package service;

import dataaccess.AuthDAO;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void clear() {
        authDAO.clear();
    }

    public AuthData createAuth(String username) {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        authDAO.createAuth(newAuth);
        return newAuth;
    }

    public AuthData getAuth(String authToken) {
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) {
        authDAO.deleteAuth(authToken);
    }
}
