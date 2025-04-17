package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

public class CreateGameService {
    private final AuthDAO authDAO = new AuthDAO(); // checks for valid token
    private final GameDAO gameDAO = new GameDAO(); // inserts game

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData authenticated = authDAO.getAuth(authToken);
        if (authenticated == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (request == null || request.getGameName() == null || request.getGameName().isBlank()) {
            throw new DataAccessException("Error: bad request, game name missing");
        }
        GameData newGame = new GameData(null, null, null, request.getGameName(), null);
        int gameID = gameDAO.createGame(newGame);

        return new CreateGameResult(gameID);
    }
}
