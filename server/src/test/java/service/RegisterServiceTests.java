package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterServiceTests {
    private MemoryDataAccess db;
    private RegisterService service;

    @BeforeEach
    public void setup() {
        db = new MemoryDataAccess();
        service = new RegisterService(db);
    }

    // positive test to see username and info is passed in correctly
    @Test
    @DisplayName("Correct register user info")
    public void testRegisterSuccess() throws Exception {
        // example correct user info
        UserData user = new UserData("chessmasterswagger", "321winner", "chess.swagger@email.com");
        AuthData result = service.register(user);

        // check to see if it is passing correctly into Register
        Assertions.assertNotNull(result);
        Assertions.assertEquals("chessmasterswagger", result.username());
        Assertions.assertNotNull(result.authToken());
        // check if it is in the db how it should be
        Assertions.assertEquals(user, db.getUser("chessmasterswagger"));
    }

    // negative test for missing password
    @Test
    @DisplayName("Missing password")
    public void testRegisterNoPassword() {
        UserData user = new UserData("chessKing", null, "chessKing@email.com");
        try {
            service.register(user);
            Assertions.fail("Missing password");
        } catch (Exception error) {
            Assertions.assertTrue(error.getMessage().toLowerCase().contains("bad request"));
        }

    }
}
