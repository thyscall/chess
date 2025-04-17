package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;

public class ClearService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
