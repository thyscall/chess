package server.websocket;


import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import dataaccess.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@WebSocket
public class WSServer {

    private final DataAccess db = new MySQLDataAccess();
    private final Gson gson = new Gson();
    // use concurrent hm to allow for multiple records happening at a time
    // map of client sessions and their usernames
    private static final ConcurrentHashMap<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private static final Map<Session, String> sessionUsers = new ConcurrentHashMap<>();


    // make web socket connections
    // follow games with gameID
    public WSServer() throws DataAccessException {
    }

    // notification when websocket is init
    @OnWebSocketConnect
    public void wsConnected(Session session) {
        System.out.println("WebSocket connected to " + session.getRemoteAddress().getAddress());
    }

    // notification when message sent + description
    // use switch for different commands
    // helpers for each command functionality
    @OnWebSocketMessage
    public void wsMessage( Session session, String message) {
        System.out.println("Received message: " + message);
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            AuthData auth = db.getAuth(command.getAuthToken());

            if (auth == null) {
                send(session, ServerMessage.error("Error: Invalid auth token"));
                return;
            }
            sessionUsers.put(session, auth.username());

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command, auth.username());
                case MAKE_MOVE -> handleMakeMove(session, message, command, auth.username());
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command, auth.username());
            }
        } catch ( Exception error) {
            send(session, ServerMessage.error("Error: " + error.getMessage()));
        }
    }

    private void handleConnect(Session session, UserGameCommand command, String username) {
        // add new game entry
        // add client to list of observers
        // empty set until someone joins as observers

        int gameID = command.getGameID();

        gameSessions.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        gameSessions.get(gameID).add(session);

        try {
            GameData game = db.getGame(gameID);
            // show board
            session.getRemote().sendString(gson.toJson(ServerMessage.loadGame(game.game())));
            String who;
            if (username.equals(game.whiteUsername())) {
                who = "white player";
            } else if (username.equals(game.blackUsername())) {
                who = "black player";
            } else {
                who = "observer";
            }
            // tell all other observers
            broadcastOthers(gameID, session, ServerMessage.notification(username + " joined as player or observer"));
            // give error message
        } catch (Exception error) {
            send(session, ServerMessage.error("Error loading game: " + error.getMessage()));
        }

    }

    private void broadcastOthers(int gameID, Session notSession, ServerMessage notification) {
        Set<Session> sessions = gameSessions.get(gameID);

        if (sessions != null) {
            for (Session sesh : sessions) {
                if (!sesh.equals(notSession)) {
                    send(sesh, notification);
                }
            }
        }
    }

    private void broadcast(int gameID, ServerMessage message) {
        // for all sessions that are in the set, send a message to them unless the set is null

        Set<Session> sessions = gameSessions.get(gameID);

        if (sessions != null) {
            for (Session session : sessions) {
                send(session, message);
            }
        }
    }

    private void send(Session session, ServerMessage message) {
        try {
            // send java obj -> json -> client
            session.getRemote().sendString(gson.toJson(message));
        } catch (IOException error) {
            error.printStackTrace();
        }
    }


    private void handleResign(Session session, UserGameCommand command, String username) {
        try {
            GameData game = db.getGame(command.gameID);

            // do not allow observers to be able to resign
            if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
                send(session, ServerMessage.error("Observers not allowed to resign"));
                return;
            }

            ChessGame updatedGame = game.game();
            updatedGame.setGameOver(true);

            db.updateGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), updatedGame));
            broadcast(command.getGameID(), ServerMessage.notification(username + " resigned from the game!"));
        } catch (Exception error) {
            send(session, ServerMessage.error("Error resigning: " + error.getMessage()));
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        Set<Session> sessions = gameSessions.get(command.gameID);
        // check if user leaves, if so, session set will be null
        // notify players/observers that someone left
        if (sessions != null) {
            sessions.remove(session);
        }
        // then remove users in session
        sessionUsers.remove(session);
        broadcast(command.getGameID(), ServerMessage.notification("Someone has left the game"));
    }

    private void handleMakeMove(Session session, String message, UserGameCommand command, String username) {
        try {
            // get the move
            var parsed = gson.fromJson(message, UserGameCommand.class);
            ChessMove move = parsed.getMove();
            // verify the game number
            GameData game = db.getGame(command.getGameID());
            ChessGame chessGame = game.game();
            // actual move happens
            chessGame.makeMove(move);
            db.updateGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame));

            // update real time using ws
            broadcast(command.getGameID(), ServerMessage.loadGame(chessGame));
            // output move that was just made to all except mover
            broadcastOthers(command.getGameID(), session, ServerMessage.notification(username + " made a move! " + move));

            // notify if in checkmate or check
            if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                broadcast(command.getGameID(), ServerMessage.notification("Checkmate! " + chessGame.getTeamTurn() + "loses."));
            } else if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                broadcast(command.getGameID(), ServerMessage.notification(chessGame.getTeamTurn() + " is in check."));

            }
        } catch (Exception error) {
            send(session, ServerMessage.error("Move error: " + error.getMessage()));
        }
    }

    // notify when web socket is closed
    @OnWebSocketClose
    public void wsClosed(int status, String why) {
        System.out.println("WebSocket closed: " + why);
    }

    // notify websocket error
    @OnWebSocketError
    public void wsError(Session session, Throwable error) {
        System.err.println(("Websocket error in session " + session + ":" + error.getMessage()));
    }

}