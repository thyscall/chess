package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    // replace this with my unit tests
    // "Write positive and negative unit tests for each method
    // on your ServerFacade class (all the methods used to call your server)"
    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
