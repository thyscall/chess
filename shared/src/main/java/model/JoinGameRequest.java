package model;

import chess.ChessGame;

public class JoinGameRequest {
    private ChessGame.TeamColor userColor;
    private Integer gameID;

    public JoinGameRequest(ChessGame.TeamColor userColor, Integer gameID) {
        this.userColor = userColor;
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getUserColor() {
        return userColor;
    }

    public Integer getGameID() {
        return gameID;
    }
}
