package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.LoginRequest;
import model.LoginResult;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final LoginService loginService = new LoginService();

    @Override
    public Object handle(Request req, Response res) {
        try {
            LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResult result = loginService.login(request);
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

    public record ErrorMessage(String message) {}
}
