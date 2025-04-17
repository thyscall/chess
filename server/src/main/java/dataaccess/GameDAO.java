package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameID = 1;

    // new game and give it an ID
    public int createGame(GameData game) {
        int id = gameID++;
        game.setGameID(id);
        games.put(id, game);
        return id;
    }
    // get game from hash table using ID
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }
    // all games in games hash table
    public Collection<GameData> listGames() {
        return games.values();
    }

    // update already existing game
    public void updateGame(GameData updatedGame) {
        games.put(updatedGame.getGameID(), updatedGame);
    }
    // clear all games
    public void clear() {
        games.clear();
        gameID = 1;
    }

}
