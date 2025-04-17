package server;

import spark.Route;
import spark.Response;
import spark.Request;
import com.google.gson.Gson;
import model.ListGamesResult;
import service.ListGamesService;
import dataaccess.DataAccessException;

public class ListGamesHandler implements Route {
    private final ListGamesService service = new ListGamesService();

    @Override
    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            ListGamesResult result = service.listGames(authToken);
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException error) {
            String message = error.getMessage().toLowerCase();
            if (message.contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return new Gson().toJson(new ErrorMessage(error.getMessage()));
        }
    }
    public record ErrorMessage (String message) {}
}
