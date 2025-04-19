package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public class HandlerUtils {
    private static final Gson gson = new Gson();

    public static Object handleResult(Response res, String message) {
        if (message != null) {
            if (message.contains("bad request")) {
                res.status(400);
            }
            else if (message.contains("unauthorized")) {
                res.status(401);
            }
            else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", message));
        }

        res.status(200);
        return null;
    }
}
