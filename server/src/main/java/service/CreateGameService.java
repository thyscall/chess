package service;

import dataaccess.DataAccessException;
import static dataaccess.DataAccess.*;
import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

public class CreateGameService {

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
