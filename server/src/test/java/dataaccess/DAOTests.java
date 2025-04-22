package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

}
