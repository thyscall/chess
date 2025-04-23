package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {
    private MySQLDataAccess db;

    @BeforeEach
    public void setup() throws DataAccessException {
        db = new MySQLDataAccess();
        db.clear();
    }

    // USER TESTS
    @Test
    @DisplayName("Correct User in DB")
    public void testInsertUserPos() throws DataAccessException {
        UserData user = new UserData("testerPositive", "testPassword", "email@email.com");
        db.insertUser(user);
        assertNotNull(db.getUser("testerPositive"));
    }

    // see if there is a duplicate of user in DB
    @Test
    @DisplayName("Insert, but user already in DB")
    public void testInsertUserNeg() throws DataAccessException {
        UserData user = new UserData("testerPositive", "testPassword", "email@email.com");
        db.insertUser(user); // add user and if user duplicate then throw error
        assertThrows(DataAccessException.class, () -> db.insertUser(user));
    }

    @Test
    @DisplayName("Find user in DB")
    public void testGetUserPos() throws DataAccessException {
        UserData user = new UserData("testerPositive", "testPassword", "email@email.com");
        db.insertUser(user);
        assertNotNull(db.getUser("testerPositive"));
    }

    // try to find a user that is not in DB
    @Test
    @DisplayName("User not in DB")
    public void testGetUserNeg() throws DataAccessException {
        assertNull(db.getUser("noOneNoOne"));
    }


    // AUTH TESTS
    // tests if a token can be inserted and found
    @Test
    @DisplayName("Insert token success")
    public void testInsertAuthPos() throws DataAccessException {
        UserData user = new UserData("player1", "testPass", "email@email.com");
        db.insertUser(user);

        AuthData auth = new AuthData("token10", "player1");
        db.insertAuth(auth);
        assertNotNull(db.getAuth("token10"));
    }

    @Test
    @DisplayName("Dupe token fail")
    public void testInsertAuthNeg() throws DataAccessException {
        // create users
        db.insertUser(new UserData("player1", "password", "email1"));
        db.insertUser(new UserData("player2", "password", "email2"));

        // test when they have the same token
        AuthData auth = new AuthData("dupeToken", "player1");
        db.insertAuth(auth);
        AuthData dupe = new AuthData("dupeToken", "player2");
        assertThrows(DataAccessException.class, () -> db.insertAuth(dupe));
    }

    @Test
    @DisplayName("No token ")
    public void testGetAuthPos() throws DataAccessException {
        // create user with insertUser
        db.insertUser(new UserData("player1", "password", "email@email.com"));

        AuthData auth = new AuthData("token10", "player1");
        db.insertAuth(auth);
        AuthData got = db.getAuth("token10");
        assertNotNull(got);
        assertEquals("player1", got.username());

    }


    @Test
    @DisplayName("No token ")
    public void testGetAuthNeg() throws DataAccessException {
        assertNull(db.getAuth("noToken"));
    }

    @Test
    @DisplayName("Remove successfully from db")
    public void testDeleteAuthPos() throws DataAccessException {
        // create user and insert into db to delete later
        db.insertUser(new UserData("player1", "password", "email@email.com"));

        AuthData auth = new AuthData("tokenTest", "player1");

        db.insertAuth(auth);
        db.deleteAuth("tokenTest"); // delete token from db
        assertNull(db.getAuth("tokenTest")); // null validates token was deleted

    }

    @Test
    @DisplayName("Can't delete a token that doesn't exist")
    public void testDeleteAuthNeg() throws DataAccessException {
        db.deleteAuth("tokenTest"); // token never inserted
    }

    // GAME TESTS
    @Test
    @DisplayName("Game created and verified")
    public void testCreateGamePos() throws DataAccessException {
        ChessGame cGame = new ChessGame();
        GameData game = new GameData(0,null,null,"game1", cGame);
        GameData createdGame = db.createGame(game);
        // get from db
        assertNotNull(db.getGame(createdGame.gameID()));
    }

    @Test
    @DisplayName("Game with invalid ID")
    public void testCreateGameNeg() throws DataAccessException {
        assertNull(db.getGame(1234));
    }

    @Test
    @DisplayName("Game created and verified")
    public void testUpdateGamePos() throws DataAccessException {
        ChessGame cGame = new ChessGame();
        GameData game = db.createGame(new GameData(0, null, null, "updateTest", cGame));
        GameData update = new GameData(game.gameID(), "whiteUsername", "blackUsername", game.gameName(), game.game());

        db.updateGame(update);

        GameData got = db.getGame(game.gameID());

        assertEquals("whiteUsername", got.whiteUsername());
        assertEquals("blackUsername", got.blackUsername());
    }

    @Test
    @DisplayName("Invalid ID")
    public void testUpdateGameNeg() throws DataAccessException {
        // create a wrong game and a good game to compare
        ChessGame cGame = new ChessGame();
        GameData wrongGame = new GameData(1234, "err", "notgood", "wrongGame", cGame);

        db.updateGame(wrongGame);
        assertNull(db.getGame(1234));
    }

    @Test
    @DisplayName("Count games in list")
    public void testListGamePos() throws DataAccessException {
        // create games in db
        db.createGame(new GameData(0, null, null, "game1", new ChessGame()));
        db.createGame(new GameData(0, null, null, "game2", new ChessGame()));

        // create a list with games in db
        List<GameData> games = db.listGames();
        // count all valid games
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("List when no games exist")
    public void testListGameNeg() throws DataAccessException {
        List<GameData> games = db.listGames();
        assertNotNull(games, "Expected not null");
        assertEquals(0, games.size(), "Expecting empty list when no games in db");
    }


    // CLEAR TEST
    @Test
    @DisplayName("Invalid ID")
    public void testClearPos() throws DataAccessException {
        db.insertUser(new UserData("username", "password", "email@email.com"));
        db.clear();
        assertNull(db.getUser("username"));
    }

    // "examine your game board" from Phase 4 spec
    @Test
    @DisplayName("Update board and server")
    public void testExamineBoard() throws DataAccessException {
        // create new game
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        // put piece in new position
        ChessPosition pos = new ChessPosition(4, 4);
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board.addPiece(pos, piece);

        // add game to db
        GameData ogGame = db.createGame(new GameData(0, null, null, "boardExamine", game));
        //update game with game changes
        db.updateGame(new GameData(ogGame.gameID(), null, null, "boardExamine", game));

        // get game from db
        GameData result = db.getGame(ogGame.gameID());
        ChessPiece gotPiece = result.game().getBoard().getPiece(pos);

        // test valid piece exists after restart
        assertNotNull(gotPiece);
        assertEquals(ChessPiece.PieceType.QUEEN, gotPiece.getPieceType());
        assertEquals(ChessGame.TeamColor.WHITE, gotPiece.getTeamColor());
    }
}
