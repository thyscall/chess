package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {
    private MySQLDataAccess db;

    @BeforeEach
    public void setup() throws DataAccessException {
        db = new MySQLDataAccess();
        db.clear();
    }

    // USER TESTS
    @Test
    @DisplayName("Correct User in DB")
    public void testInsertUserPos() throws DataAccessException {
        UserData user = new UserData("testerPositive", "testPassword", "email@email.com");
        db.insertUser(user);
        assertNotNull(db.getUser("testerPositive"));
    }

    // see if there is a duplicate of user in DB
    @Test
    @DisplayName("Insert, but user already in DB")
    public void testInsertUserNeg() throws DataAccessException {
        UserData user = new UserData("testerPositive", "testPassword", "email@email.com");
        db.insertUser(user); // add user and if user duplicate then throw error
        assertThrows(DataAccessException.class, () -> db.insertUser(user));
    }

    @Test
    @DisplayName("Find user in DB")
    public void testGetUserPos() throws DataAccessException {
        UserData user = new UserData("testerPositive", "testPassword", "email@email.com");
        db.insertUser(user);
        assertNotNull(db.getUser("testerPositive"));
    }

    // try to find a user that is not in DB
    @Test
    @DisplayName("User not in DB")
    public void testGetUserNeg() throws DataAccessException {
        assertNull(db.getUser("noOneNoOne"));
    }


    // AUTH TESTS
    // tests if a token can be inserted and found
    @Test
    @DisplayName("Insert token success")
    public void testInsertAuthPos() throws DataAccessException {
        UserData user = new UserData("player1", "testPass", "email@email.com");
        db.insertUser(user);

        AuthData auth = new AuthData("token10", "player1");
        db.insertAuth(auth);
        assertNotNull(db.getAuth("token10"));
    }

    @Test
    @DisplayName("Dupe token fail")
    public void testInsertAuthNeg() throws DataAccessException {
        // create users
        db.insertUser(new UserData("player1", "password", "email1"));
        db.insertUser(new UserData("player2", "password", "email2"));

        // test when they have the same token
        AuthData auth = new AuthData("dupeToken", "player1");
        db.insertAuth(auth);
        AuthData dupe = new AuthData("dupeToken", "player2");
        assertThrows(DataAccessException.class, () -> db.insertAuth(dupe));
    }

    @Test
    @DisplayName("No token ")
    public void testGetAuthPos() throws DataAccessException {
        // create user with insertUser
        db.insertUser(new UserData("player1", "password", "email@email.com"));

        AuthData auth = new AuthData("token10", "player1");
        db.insertAuth(auth);
        AuthData got = db.getAuth("token10");
        assertNotNull(got);
        assertEquals("player1", got.username());

    }


    @Test
    @DisplayName("No token ")
    public void testGetAuthNeg() throws DataAccessException {
        assertNull(db.getAuth("noToken"));
    }

    @Test
    @DisplayName("Remove successfully from db")
    public void testDeleteAuthPos() throws DataAccessException {
        // create user and insert into db to delete later
        db.insertUser(new UserData("player1", "password", "email@email.com"));

        AuthData auth = new AuthData("tokenTest", "player1");

        db.insertAuth(auth);
        db.deleteAuth("tokenTest");
        assertNull(db.getAuth("tokenTest"));

    }

    @Test
    @DisplayName("Can't delete a token that doesn't exist")
    public void testDeleteAuthNeg() throws DataAccessException {
        db.deleteAuth("tokenTest"); // token never inserted
    }
}
