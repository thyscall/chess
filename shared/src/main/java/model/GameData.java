package model;

import chess.ChessGame;

public class GameData {
    private int gameID;
    private String gameName;
    private String whiteUsername;
    private String blackUsername;
    private ChessGame game;

    public GameData(String gameName){
        this.gameName = gameName;
        this.game = new ChessGame();
    }

    // GETTERS

    public int getGameID() {
        return gameID;
    }
    public String getGameName() {
        return gameName;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }
    //SETTERS

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

}
