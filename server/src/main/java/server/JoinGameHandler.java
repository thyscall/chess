package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.JoinGameRequest;
import service.JoinGameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    public final JoinGameService service = new JoinGameService(DataAccess.authDAO, DataAccess.gameDAO);

    @Override
    public Object handle(Request req, Response res) {
        try {
            String authorization = req.headers("Authorization");
            JoinGameRequest request = new Gson().fromJson(req.body(), JoinGameRequest.class);

            service.joinGame(authorization, request);
            res.status(200);
            return "{}";
        } catch (DataAccessException error) {
            String message = error.getMessage().toLowerCase();
            if (message.contains("unauthorized")) {
                res.status(401);
            } else if (message.contains("bad request")) {
                res.status(400);
            } else if (message.contains("already taken")) {
                res.status(403);
            } else {
              res.status(500);
            }
            return new Gson().toJson(new ErrorMessage(error.getMessage()));
        }
     }
     private record ErrorMessage(String message) {}
}
