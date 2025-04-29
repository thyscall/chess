package client;

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
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
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

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}
