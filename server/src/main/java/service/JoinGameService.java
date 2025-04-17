package service;

import chess.ChessGame;
import static dataaccess.DataAccess.*;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;

public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (request == null || request.getGameID() == null) {
            throw new DataAccessException("Error: bad request - game ID is null");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = gameDAO.getGame(request.getGameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request - invalid game ID");
        }

        ChessGame.TeamColor color = request.getUserColor();

        if (color == ChessGame.TeamColor.WHITE) {
            if (game.getWhiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game.setWhiteUsername(auth.getUsername());
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (game.getBlackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game.setBlackUsername(auth.getUsername());
        } else if (color == null) {
            return; // do nothing to game if no user color
        } else {
            throw new DataAccessException("Error: bad request - invalid color");
        }

        gameDAO.updateGame(game);
    }

//    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
//        if (authToken == null || authToken.isBlank()) {
//            throw new DataAccessException("Error: unauthorized");
//        }
//        if (request == null || request.getGameID() == null) {
//            throw new DataAccessException("Error: bad request - game ID is null");
//        }
//        AuthData auth = authDAO.getAuth(authToken);
//        if (auth == null) {
//            throw new DataAccessException("Error: unauthorized");
//        }
//        GameData game = gameDAO.getGame(request.getGameID());
//
//        if (request.getGameID() == null || gameDAO.getGame(request.getGameID()) == null) {
//            throw new DataAccessException("Error: bad request - invalid game ID");
//        }
//        ChessGame.TeamColor color = request.getUserColor();
//
//        if (color == ChessGame.TeamColor.WHITE) {
//            if (game.getWhiteUsername() != null) {
//                throw new DataAccessException("Error: already taken");
//            }
//            game.setWhiteUsername(auth.getUsername());
//        } else if (color == ChessGame.TeamColor.BLACK) {
//            if (game.getBlackUsername() != null) {
//                throw new DataAccessException("Error: already taken");
//            }
//            game.setBlackUsername(auth.getUsername());
//        } else if (color == null) {
//            return; // do nothing to game if no user color
//        } else {
//            throw new DataAccessException("Error: bad request - invalid color");
//        }
//        gameDAO.updateGame(game);
//    }
}
