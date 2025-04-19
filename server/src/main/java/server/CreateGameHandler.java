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

        if (result.message() != null) {
            if (result.message().contains("bad request")) {
                res.status(400);
            } else if (result.message().contains("unauthorized")) {
                res.status(401);
            }
            else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", result.message()));
        }

        res.status(200);
        return gson.toJson(Map.of("gameID", result.gameID()));
    }
}