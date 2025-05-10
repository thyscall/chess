package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccess;
import spark.*;
import server.websocket.WSServer;

public class Server {

    public int run(int desiredPort) {
        DataAccess db;
        try {
            dataaccess.DatabaseManager.initDB();
            dataaccess.DatabaseManager.initTables();

            db = new MySQLDataAccess();
        } catch (Exception error) {
            System.out.println("DB init failed: " + error.getMessage());
            throw new RuntimeException(error);
        }

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // register websocket
        Spark.webSocket("/ws", WSServer.class);

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
