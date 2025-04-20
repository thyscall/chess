package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JoinGameServiceTests {
    private MemoryDataAccess db;
    private JoinGameService service;

    @BeforeEach
    public void setup() {
        db = new MemoryDataAccess();
        service = new JoinGameService(db);
    }

    // positive test to join as white team
    @Test
    @DisplayName("Join as white team")
    public void testJoinWhite() {
        AuthData authData = new AuthData("gameToken", "whiteteamplayer");
        db.insertAuth(authData);

        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, null, null, "JoinTestGame", game);
        db.createGame(gameData);

        JoinGameRequest request = new JoinGameRequest("White", 1);
        String message = service.join("gameToken", request);

        Assertions.assertNull(message); // should be null if it worked right
    }

    // negative try to join with random color
    @Test
    @DisplayName("Join game wrong color")
    public void testJoinWrongColor() {
        AuthData authData = new AuthData("gameToken", "greenplayer");
        db.insertAuth(authData);

        GameData game = new GameData(1, null, null, "TestGreenGame", new ChessGame());
        db.createGame(game);

        JoinGameRequest request = new JoinGameRequest("Green", 1);
        String message = service.join("gameToken", request);

        Assertions.assertTrue(message.toLowerCase().contains("bad request"));
    }
}
