package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ClearHandler implements Route {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(DataAccess db) {
        this.clearService = new ClearService(db);
    }

    public Object handle(Request req, Response res) {
        try {
            clearService.clear();
            res.status(200); // success
            return gson.toJson(Map.of());

        } catch (DataAccessException error) {
            res.status(500); // fail response
            return gson.toJson(Map.of("message", "Error: " + error.getMessage()));
        }
    }
}