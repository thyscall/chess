package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.ListGamesResult;
import service.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ListGamesHandler implements Route {
    private final ListGamesService listGamesService;
    private final Gson gson = new Gson();

    public ListGamesHandler(DataAccess db) {
        this.listGamesService = new ListGamesService(db);
    }

    public Object handle(Request req, Response res) {
        String token = req.headers("Authorization");
        ListGamesResult result = listGamesService.list(token); // list of games using token as arg

        String error = result.message();

        if (error != null) {
            if (error.contains("unauthorized")) {
                // unauthoroized
                res.status(401);
            }
            else {
                // bad request
                res.status(500);
            }
            return gson.toJson(Map.of("message", error)); // json of error message
        }

        res.status(200);
        return gson.toJson(Map.of("games", result.games()));
    }
}
