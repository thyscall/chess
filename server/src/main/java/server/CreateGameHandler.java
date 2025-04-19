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

import java.util.Map;

public class CreateGameHandler implements Route {
    private final CreateGameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(DataAccess db) {
        this.gameService = new CreateGameService(db);
    }

    public Object handle(Request req, Response res) {
        String token = req.headers("Authorization");
        CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
        CreateGameResult result = gameService.create(token, request);

        Object error = HandlerUtils.handleResult(res, result.message());
        if (error != null) {
            return error;
        }
        return gson.toJson(Map.of("gameID", result.gameID()));
    }
}