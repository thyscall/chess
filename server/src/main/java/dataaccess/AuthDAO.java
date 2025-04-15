package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDAO {

    // store tokens in memory
    private final HashMap<String, AuthData> authTable = new HashMap<>();

    // gen a random token and save it
    public void createAuth(AuthData authData) {
        authTable.put(authData.getAuthToken(), authData);
    }
    // look for auth in hash table
    public AuthData getAuth(String authToken) {
        return authTable.get(authToken);
    }
    // delete a token
    public void deleteAuth(String authToken) {
        authTable.remove(authToken); // remove the authToken from the has table
    }
    public void clear() {
        authTable.clear(); // clear auth table
    }

}
