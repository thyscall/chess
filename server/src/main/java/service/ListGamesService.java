package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.List;

public class ListGamesService {
    private final DataAccess db;

    public ListGamesService(DataAccess db) {
        this.db = db;
    }

    public ListGamesResult list(String token) {
        try {
            AuthData auth = db.getAuth(token);
            if (auth == null) return new ListGamesResult(null, "Error: unauthorized");
            List<GameData> games = db.listGames();
            return new ListGamesResult(games, null);
        } catch (DataAccessException e) {
            return new ListGamesResult(null, "Error: " + e.getMessage());
        }
    }
}