package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID; // use for random auth token

public class RegisterService {
    private final DataAccess db;
    // connect to database
    public RegisterService(DataAccess db) {
        this.db = db;
    }

    public AuthData register(UserData user) throws DataAccessException {
        // no user data args missing or throw err
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (db.getUser(user.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }
        // add user to database
        db.insertUser(user);
        String token = UUID.randomUUID().toString(); // random token to auth login using UUID
        AuthData authData = new AuthData(token, user.username());

        // save auth token to database
        db.insertAuth(authData);
        return authData;
    }
}