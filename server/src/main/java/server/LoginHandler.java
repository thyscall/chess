package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.LoginRequest;
import model.LoginResult;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class LoginHandler implements Route {
    private final LoginService loginService;
    private final Gson gson = new Gson();

    public LoginHandler(DataAccess db) {
        this.loginService = new LoginService(db);
    }

    public Object handle(Request req, Response res) {
        var loginReq = gson.fromJson(req.body(), LoginRequest.class);
        var result = loginService.login(loginReq);

        if (result.message() != null) {
            if (result.message().contains("bad request")) {
                res.status(400);
            }
            else if (result.message().contains("unauthorized")) {
                res.status(401);
            }
            else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", result.message()));
        }

        res.status(200);
        return gson.toJson(Map.of("username", result.username(),
                "authToken", result.authToken()
        ));
    }
}