package client;

import model.LoginRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;


import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }


    // "Write positive and negative unit tests for each method
    // on your ServerFacade class (all the methods used to call your server)"
    @BeforeEach
    public void clearDB() throws Exception {
        facade.clear();
    }

    // replace this with my unit tests
    @Test
    @DisplayName("Clear success")
    public void clearPos() throws Exception {
        // check that clearDB ran properly and cleared DB successfully
        assertDoesNotThrow(() -> facade.clear());
    }

    // new user can register on server
    @Test
    @DisplayName("Register success")
    public void registerPos() throws Exception {
        var request = new RegisterRequest("john", "pass321", "email@email.com");
        var result = facade.register(request);

        assertNotNull(result);
        assertNotNull(result.authToken());
        assertEquals("john", result.username());
    }

    // duplicate user not allowed, not allowed to create one
    @Test
    @DisplayName("Username already taken")
    public void registerNeg() throws Exception {
        var request = new RegisterRequest("johnDupe", "passDupe", "johndupe@email.com");
        // initial registration
        facade.register(request);
        // second reg not allowed
        assertThrows(Exception.class, () -> facade.register(request));
    }

    // login good after registering user
    @Test
    @DisplayName("Login with new user")
    public void loginPos() throws Exception {
        var register = new RegisterRequest("userOkay", "passOkay", "email@email.com");
        // register new user
        facade.register(register);

        var login = new LoginRequest("userOkay", "passOkay");
        var result = facade.login(login);
        // second login attempt should pass bc registered
        assertNotNull(result);
        assertNotNull(result.authToken());
        assertEquals("userOkay", result.username());
    }

    // login failed bc wrong password
    @Test
    @DisplayName("Login with new user")
    public void loginNeg() throws Exception {
        var register = new RegisterRequest("testUser", "goodPass", "email@email.com");
        facade.register(register);
        // try to log in with wrong password
        var badLogin = new LoginRequest("testUser", "badPass");
        // throw error with wrong password
        assertThrows(Exception.class, () -> facade.login(badLogin));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}
