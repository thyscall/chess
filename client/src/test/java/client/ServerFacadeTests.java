package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;


import java.util.List;

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
    @DisplayName("Login fail wrong password")
    public void loginNeg() throws Exception {
        var register = new RegisterRequest("testUser", "goodPass", "email@email.com");
        facade.register(register);
        // try to log in with wrong password
        var badLogin = new LoginRequest("testUser", "badPass");
        // throw error with wrong password, use lamda
        assertThrows(Exception.class, () -> facade.login(badLogin));
    }


    // logout successful
    @Test
    @DisplayName("Logout success")
    public void logoutPos() throws Exception {
        var register = new RegisterRequest("testUser", "testPass", "email@email.com");
        var auth = facade.register(register);

        var logoutReq = new LogoutRequest(auth.authToken());
        assertDoesNotThrow(() -> facade.logout(logoutReq));
    }

    @Test
    @DisplayName("Logout failed, wrong token")
    public void logoutNeg() throws Exception {
        var logoutReq = new LogoutRequest("wrong-token");
        assertThrows(Exception.class, () -> facade.logout(logoutReq));

    }


    @Test
    @DisplayName("Create Game success")
    public void createGamePos() throws Exception {
        var registerReq = new RegisterRequest("testUser", "testPass", "email@email.com");

        AuthData auth = facade.register(registerReq);
        //create game
        var createGameReq = new CreateGameRequest("Test Game");
        CreateGameResult result = facade.createGame(auth.authToken(), createGameReq);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Create Game fail")
    public void createGameNeg() throws Exception {
        var request = new CreateGameRequest("Bad Game"); // create new game then add bad token
        // test game with bad token
        assertThrows(Exception.class, () -> facade.createGame("badToken", request));
    }

    @Test
    @DisplayName("List Games success")
    public void listGamesPos() throws Exception {
        var request = new RegisterRequest("testUser", "testPass", "email@email.com");
        AuthData result = facade.register(request);
        String authToken = result.authToken();

        ListGamesResult games = facade.listGames(authToken);


        assertNotNull(games);
    }

    @Test
    @DisplayName("List Games failure")
    public void listGamesNeg() throws Exception {
        assertThrows(Exception.class, () -> facade.listGames("badToken"));
    }

    @Test
    @DisplayName("Join game success")
    public void joinGamePos() throws Exception {
        // reg user and get auth token
        var registerReq = new RegisterRequest("testUser", "testPass", "email@email.com");
        AuthData auth = facade.register(registerReq);
        // create game and join it
        var createGameReq = new CreateGameRequest("testGame");
        CreateGameResult gameResult = facade.createGame(auth.authToken(), createGameReq);
        var joinGameReq = new JoinGameRequest("WHITE", gameResult.gameID());

        assertDoesNotThrow(() -> facade.joinGame(auth.authToken(), joinGameReq));
    }

    @Test
    @DisplayName("Join game failure")
    public void joinGameNeg() throws Exception {
        var joinGameReq = new JoinGameRequest("WHITE", 88888);
        // test bad token
        assertThrows(Exception.class, () -> facade.joinGame("badToken", joinGameReq));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}
