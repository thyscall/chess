package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

public class JoinGameService {
    private final DataAccess db;

    public JoinGameService(DataAccess db) {
        this.db = db;
    }

    public String join(String token, JoinGameRequest req) {
        try {
            AuthData auth = db.getAuth(token);
            // valid token
            if (auth == null) {
                return "Error: unauthorized";
            }
            // legit player & game ?
            if (req.gameID() == null) {
                return "Error: bad request";
            }
            // find game in db
            GameData game = db.getGame(req.gameID());
            if (game == null) {
                return "Error: bad request";
            }

            String username = auth.username();

            // allow for null team color to observe game (phase 5)
            if (req.playerColor() == null) {
                System.out.println("Observer joined " + req.gameID() + "!");
                return null;
            }

            // check user color and if authenticated
            // if someone already joined as white, null
            // if someone already joined as black, null, err
            switch (req.playerColor().toUpperCase()) {
                case "WHITE" -> {

                    if (game.whiteUsername() != null) {
                        return "Error: already taken";
                    }
                    // if not alreay taken, make new obj
                    game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                }
                case "BLACK" -> {

                    if (game.blackUsername() != null) {
                        return "Error: already taken";
                    }
                    // if available, new obj
                    game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                }
                // if not white or black, err
                default -> {
                    return "Error: bad request";
                }
            }
            System.out.println("Joining game as user: " + username);
            System.out.println("Saving game data: " + game);

            // save game to database
            db.updateGame(game);
            return null;
        } catch (DataAccessException error) {
            return "Error: " + error.getMessage();
        }
    }
}