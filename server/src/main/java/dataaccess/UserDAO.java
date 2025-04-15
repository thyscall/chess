package dataaccess;

import model.UserData;
import java.util.HashMap;

public class UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    // clear users in db
    public void clear() {
        users.clear();
    }

    // create new user in db, error if already there
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.getUsername(), user);
    }
    // search user username
    public UserData getUser(String username) {
        return users.get(username);
    }
    //does user exist?
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
