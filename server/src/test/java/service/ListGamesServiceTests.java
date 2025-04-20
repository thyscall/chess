package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.ListGamesResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ListGamesServiceTests {
    private MemoryDataAccess db;
    private ListGamesService service;

    @BeforeEach
    public void setup() {
        db = new MemoryDataAccess();
        service = new ListGamesService(db);
    }

    // positive list of games in db
    @Test
    @DisplayName("List of Games Success")
    public void testListGames() {
        AuthData auth = new AuthData("token", "listgetter");
        db.insertAuth(auth);

        GameData game = new GameData(1, "whiteteam", "blackteam", "Game1", new ChessGame());
        db.createGame(game);

        ListGamesResult result = service.list("token");
        Assertions.assertNotNull(result.games());
        Assertions.assertEquals(1, result.games().size());
    }

    @Test
    @DisplayName("List without auth")
    public void testListNoAuth() {
        ListGamesResult result = service.list("badToken");
        Assertions.assertTrue(result.message().toLowerCase().contains("unauthorized"));
    }
}
