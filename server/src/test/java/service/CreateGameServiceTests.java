package service;


import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CreateGameServiceTests {
    private MemoryDataAccess db;
    private CreateGameService service;

    @BeforeEach
    public void setup() {
        db = new MemoryDataAccess();
        service = new CreateGameService(db);
    }

    // positive test for correct game creation
    @Test
    @DisplayName("Correct game creation")
    public void testGameCreate() {
        AuthData auth = new AuthData("authToken", "chesschamp2025");
        db.insertAuth(auth);

        CreateGameRequest request = new CreateGameRequest("ChampMatch");
        CreateGameResult result = service.create("authToken", request);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.gameID());
    }

    //negative test for no game name
    @Test
    @DisplayName("No name no game test")
    public void testNoName() {
        AuthData auth = new AuthData("gameToken", "NoNameGame");
        db.insertAuth(auth);

        CreateGameRequest request = new CreateGameRequest(null);
        CreateGameResult result = service.create("gameToken", request);

        Assertions.assertTrue(result.message().toLowerCase().contains("bad request"));
    }
}
