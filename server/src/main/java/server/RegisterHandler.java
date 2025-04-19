package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import java.util.UUID;

public class RegisterHandler implements Route {
    private final RegisterService service;
    private final Gson gson = new Gson(); // parse JSON

    public RegisterHandler(DataAccess db) {
        this.service = new RegisterService(db);
    }

    public Object handle(Request req, Response res) {
        try {
            UserData user = gson.fromJson(req.body(), UserData.class);
            AuthData auth = service.register(user);

            res.status(200); // if no errors, success 200
            return gson.toJson(Map.of("username", user.username(), "authToken", auth.authToken()));

        // failed response, 500
        } catch (DataAccessException error) {
            String message = error.getMessage();
            if (message.contains("bad request")) {
                res.status(400);
            } else if (message.contains("already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + error.getMessage()));
        }
    }
}