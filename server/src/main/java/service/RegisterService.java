package service;

import dataaccess.*;
import model.*;
import static dataaccess.DataAccess.*;

import java.util.UUID;

public class RegisterService {

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        //validate username input
        if (request == null || request.getUsername() == null || request.getPassword() == null || request.getEmail() ==null) {
            throw new DataAccessException("Error: Invalid username, password or email");
        }
        // check if username has been taken
        if (userDAO.userExists(request.getUsername())) {
            throw new DataAccessException("Error: Username already taken");
        }
        //register new user
        UserData newUser = new UserData(request.getUsername(), request.getPassword(), request.getEmail());
        // create new user with the new username, p word, and email
        userDAO.createUser(newUser);
        // auth token to go with it
        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, request.getUsername());
        //create auth token for new username
        authDAO.createAuth(newAuth);

        //return username and auth token
        return new RegisterResult(request.getUsername(), authToken);
    }
}
