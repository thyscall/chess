package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

import static dataaccess.DataAccess.*;

public class LogoutService {
    private final DataAccess db;

    public LogoutService(DataAccess db) {
        this.db = db;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = db.getAuth(authToken);

        // no token throw error
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        // delete from hash table
        db.deleteAuth(authToken);
    }
}

