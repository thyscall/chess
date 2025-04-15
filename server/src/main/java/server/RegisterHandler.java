package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.RegisterResult;
import model.RegisterRequest;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final RegisterService registerService = new RegisterService();

    @Override
    public Object handle(Request req, Response res) {
        try {
            // 1. JSON into RegisterRequest
            RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);

            // 2. run service logic
            RegisterResult result = registerService.register(request);

            // 3. return as JSON
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {

            // Username taken or invalid input
            if (e.getMessage().contains("already taken")) {
                // already taken
                res.status(403);
            } else {
                // bad request
                res.status(400);
            }
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private record ErrorMessage(String message) {}
}
