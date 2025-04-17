package model;

import java.util.Collection;

public class ListGamesResult {
    private final Collection<GameData> games;

    public ListGamesResult(Collection<GameData> games) {
        this.games = games;
    }

    public Collection<GameData> getGames() {
        return games;
    }
}
