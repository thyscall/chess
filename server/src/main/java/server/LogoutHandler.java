package server;

import dataaccess.DataAccess;
import service.LogoutService;
import spark.Route;
import spark.Response;
import spark.Request;
import com.google.gson.Gson;
import dataaccess.DataAccessException;

public class LogoutHandler implements Route {
    private final LogoutService logoutService; // log user out

    public LogoutHandler(DataAccess db) {
        this.logoutService = new LogoutService(db); // service gets db access, not new db
    }

    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");

            logoutService.logout(authToken);
            res.status(200);
            System.out.println("Received logout token: " + authToken);
            return "{}";
        } catch (DataAccessException error) {
            String message = error.getMessage().toLowerCase();
            if (message.contains("unauthorized")) {
                res.status(401); // unauth
            } else {
                res.status(500); // fail response
            }
            return new Gson().toJson(new ErrorMessage(error.getMessage()));
        } catch (Exception error) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + error.getMessage()));

        }
    }

    public record ErrorMessage(String message) {}
}
