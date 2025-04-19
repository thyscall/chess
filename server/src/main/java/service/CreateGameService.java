package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

public class CreateGameService {
    private final DataAccess db;
    private static int gameIDCounter = 1;

    public CreateGameService(DataAccess db) {
        this.db = db;
    }

    public CreateGameResult create(String token, CreateGameRequest req) {
        try {
            AuthData auth = db.getAuth(token);

            // unauth if no auth
            if (auth == null) {
                return new CreateGameResult(null, "Error: unauthorized");
            }
            // no name no game
            if (req.gameName() == null || req.gameName().isBlank()) {
                return new CreateGameResult(null, "Error: bad request");
            }
            // change ID after each came created
            int id = gameIDCounter++;
            ChessGame game = new ChessGame();
            GameData gameData = new GameData(id, null, null, req.gameName(), game);
            db.createGame(gameData);
            return new CreateGameResult(id, null);

        } catch (DataAccessException error) {
            return new CreateGameResult(null, "Error: " + error.getMessage());
        }
    }
}