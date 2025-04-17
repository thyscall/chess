package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResult;
import service.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    public final CreateGameService createGameService = new CreateGameService(DataAccess.authDAO, DataAccess.gameDAO);

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            //get token from header
            String authToken = req.headers("Authorization");
            //request body deserialize JSON
            CreateGameRequest request = new Gson().fromJson(req.body(), CreateGameRequest.class);
            //run service
            CreateGameResult result = createGameService.createGame(authToken, request);

            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException error) {
            String message = error.getMessage().toLowerCase();
            if (message.contains("unauthorized")) {
                res.status(401);
            } else if (message.contains("bad request")) {
                res.status(400);
            } else {
                res.status(500);
            }
            return new Gson().toJson(new ErrorMessage(error.getMessage()));
        } catch (Exception error) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + error.getMessage()));
        }
    }
    private record ErrorMessage(String message) {}
}

