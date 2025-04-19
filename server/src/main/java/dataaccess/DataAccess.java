package dataaccess;

import model.*;

import java.util.List;

public interface DataAccess {
    // clear db
    void clear() throws DataAccessException;

    // User info
    // add new user
    void insertUser(UserData user) throws DataAccessException;
    // look up user
    UserData getUser(String username) throws DataAccessException;

    // token info
    // add new auth token
    void insertAuth(AuthData auth) throws DataAccessException;

    // token validater
    AuthData getAuth(String authToken) throws DataAccessException;

    // remove token = log out
    void deleteAuth(String authToken) throws DataAccessException;

    // game info
    // add game
    void createGame(GameData game) throws DataAccessException;

    // get game ID
    GameData getGame(int gameID) throws DataAccessException;

    // alll games played
    List<GameData> listGames() throws DataAccessException;

    // update game status
    void updateGame(GameData game) throws DataAccessException;
}
