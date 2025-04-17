package service;

import dataaccess.DataAccessException;
import static dataaccess.DataAccess.*;
import dataaccess.AuthDAO;

public class LogoutService {
    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
