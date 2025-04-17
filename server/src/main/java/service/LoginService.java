package service;

import dataaccess.DataAccessException;
import static dataaccess.DataAccess.*;

import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.util.UUID;

public class LoginService {
    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData user = userDAO.getUser(request.getUsername());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.getUsername());
        authDAO.createAuth(auth);
        return new LoginResult(user.getUsername(), authToken);
    }
}
