package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        DataAccess db = new MemoryDataAccess();

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", new ClearHandler(db)); // Clears the database. Removes all users, games, and authTokens.
        Spark.post("/user", new RegisterHandler(db)); //	Register a new user.
        Spark.post("/session", new LoginHandler(db)); // Logs in an existing user (returns a new authToken).
        Spark.delete("/session", new LogoutHandler(db)); // Logs out the user represented by the authToken.
        Spark.post("/game", new CreateGameHandler(db)); // Creates a new game.
        Spark.get("/game", new ListGamesHandler(db)); // Gives a list of all games.
        Spark.put("/game", new JoinGameHandler(db)); // Verifies specified game exists, adds caller as requested...


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
