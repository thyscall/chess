package server;

import dataaccess.DataAccessException;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class ClearHandler implements Route {
    private final ClearService service = new ClearService();

    @Override
    public Object handle(Request req, Response res) {
        try {
            //clear all data
            service.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException error) {
            // int server error
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + error.getMessage()));
        }
    }
    private record ErrorMessage(String message) {}
}
