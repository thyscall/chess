package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginServiceTests {
    private MemoryDataAccess db;
    private LoginService service;

    @BeforeEach
    public void setup() {
        db = new MemoryDataAccess();
        service = new LoginService(db);
    }

    // positive login for correct info
    @Test
    @DisplayName("Positive Wrong Password Error")
    public void testCorrectInfo() throws Exception {
        UserData user = new UserData("testLogin", "mypassword", "loginservice@email.com");
        db.insertUser(user);

        LoginRequest req = new LoginRequest("testLogin", "mypassword");
        LoginResult result = service.login(req);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testLogin", result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertNull(result.message());
    }

    //negative test wrong password error
    @Test
    @DisplayName("test wrong password error")
    public void testWrongPassword() throws DataAccessException {
        UserData user = new UserData("testLogin", "mypassword", "loginservice@email.com");
        db.insertUser(user);

        LoginRequest req = new LoginRequest("testLogin", "wrongpassword");
        LoginResult result = service.login(req);

        Assertions.assertNull(result.username());
        Assertions.assertNull(result.authToken());
        Assertions.assertTrue(result.message().toLowerCase().contains("unauthorized"));
    }
}


