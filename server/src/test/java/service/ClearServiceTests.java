package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {
    private MemoryDataAccess db;
    private ClearService service;

    @BeforeEach
    public void setup() {
        db = new MemoryDataAccess();
        service = new ClearService(db);
    }

    // positive test that nothing is left after clearing
    @Test
    @DisplayName("Nothing left after clear")
    public void testClearSuccess() throws Exception {
        db.insertUser(new UserData("tobecleared","clearme", "clear@clear.com"));
        db.insertAuth(new AuthData("token", "tobecleared"));
        db.createGame(new GameData(1, null, null, "ClearGame", new ChessGame()));

        service.clear();

        Assertions.assertNull(db.getUser("tobecleared"));
        Assertions.assertNull(db.getAuth("token"));
        Assertions.assertEquals(0, db.listGames().size());
    }
}
