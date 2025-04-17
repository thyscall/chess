package service;

import static dataaccess.DataAccess.*;

import dataaccess.DataAccessException;

public class ClearService {

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
