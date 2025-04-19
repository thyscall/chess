package dataaccess;

import model.*;

import java.util.*;

// whatever is in DataAccess must be implemented here
public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();

    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
    }
    // add user to memory
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: already taken");
        }
        users.put(user.username(), user);
    }
    //find user in data map
    public UserData getUser(String username) {
        return users.get(username);
    }
    // add token to map
    public void insertAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }
    // verify token is there
    public AuthData getAuth(String token) {
        return auths.get(token);
    }
    // delete token from map
    public void deleteAuth(String token) {
        auths.remove(token);
    }
    // add new game to map
    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }
    // find game from map
    public GameData getGame(int id) {
        return games.get(id);
    }
    // list of all games in map
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }
    // update game status
    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }
}
