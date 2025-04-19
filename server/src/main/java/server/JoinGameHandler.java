package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.JoinGameRequest;
import service.JoinGameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class JoinGameHandler implements Route {
    private final JoinGameService joinGameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(DataAccess db) {
        this.joinGameService = new JoinGameService(db);
    }

    public Object handle(Request req, Response res) {
        String token = req.headers("Authorization");
        JoinGameRequest joinReq = gson.fromJson(req.body(), JoinGameRequest.class);
        String error = joinGameService.join(token, joinReq);

        if (error != null) {
            if (error.contains("bad request")) {
                res.status(400);
            } else if (error.contains("unauthorized")) {
                res.status(401);
            } else if (error.contains("already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", error));
        }

        res.status(200);
        return "{}";
    }
}