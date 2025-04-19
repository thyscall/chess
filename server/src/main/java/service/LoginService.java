package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.UUID;

public class LoginService {
    private final DataAccess db;

    public LoginService(DataAccess db) {
        this.db = db;
    }

    public LoginResult login(LoginRequest req) {
        try {
            // does the user exist? right password? if not, it's null
            if (req.username() == null || req.password() == null) {
                return new LoginResult(null, null, "Error: bad request");
            }

            var user = db.getUser(req.username());

            // if no user or p word attempt != actual p word
            if (user == null || !user.password().equals(req.password())) {
                return new LoginResult(null, null, "Error: unauthorized");
            }

            String token = UUID.randomUUID().toString(); // give it random auth

            // reg login with new token, match it to username
            db.insertAuth(new AuthData(token, req.username()));
            return new LoginResult(req.username(), token, null);
        } catch (DataAccessException error) {
            return new LoginResult(null, null, "Error: " + error.getMessage());
        }
    }
}