package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LogoutServiceTests {
    private MemoryDataAccess db;
    private LogoutService service;

    @BeforeEach
    public void setup() {
        db = new MemoryDataAccess();
        service = new LogoutService(db);
    }

    // positive test for successful logout
    @Test
    @DisplayName("Correct logout")
    public void testLogoutSuccess() throws Exception {
        AuthData auth = new AuthData("logoutAuthToken", "logoutUser"); // info given
        db.insertAuth(auth);
        service.logout("logoutAuthToken"); // info received

        Assertions.assertNull(db.getAuth("logoutAuthToken"));
    }

    //negative test for no or invalid auth token
    @Test
    @DisplayName("Invalid Token in Logout")
    public void testTokenValid() {
        try {
            service.logout("badToken"); // this token is made up to test
            Assertions.fail("No error thrown for invalid token");
        } catch (Exception error) {
            Assertions.assertTrue(error.getMessage().toLowerCase().contains("unauthorized"));
        }
    }
}
