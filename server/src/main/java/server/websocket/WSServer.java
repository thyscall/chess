package server.websocket;


import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import dataaccess.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@WebSocket
public class WSServer {

    private final DataAccess db = new MySQLDataAccess();
    private final Gson gson = new Gson();


    // use concurrent hm to allow for multiple records happening at a time
    // set of client sessions
    private static final ConcurrentHashMap<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();


    // make web socket connections
    // follow games with gameID
    public WSServer() throws DataAccessException {
    }

    // notification when websocket is init
    @OnWebSocketConnect
    public void wsConnected(Session session) {
        System.out.println("WebSocket connected to " + session);
    }

    // notification when message sent + description
    // use switch for different commands
    // helpers for each command functionality
    @OnWebSocketMessage
    public void wsMessage( Session session, String message) {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMakeMove(session, command);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
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
            // tell all other observers
            broadcast(gameID, ServerMessage.notification("Someone joined as player or observer"));
            // give error message
        } catch (Exception error) {
            send(session, ServerMessage.error("Error loading game: " + error.getMessage()));
        }

    }

    private void broadcast(int gameID, Object notification) {
    }

    private void send(Session session, Object error) {
    }


    private void handleResign(Session session, UserGameCommand command) {
        broadcast(command.gameID, ServerMessage.notification("A user has resigned..."));
    }

    private void handleLeave(Session session, UserGameCommand command) {
        Set<Session> sessions = gameSessions.get(command.gameID);
        // check if user leaves, if so, session set will be null
        // notify players/observers that someone left
        if (sessions != null) {
            sessions.remove(session);
            broadcast(command.getGameID(), ServerMessage.notification("Someone has left the game"));
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command) {
    }



    // notify when web socket is closed
    @OnWebSocketClose
    public void wsClosed(Session session, String message) {
        System.out.println("WebSocked from " + session + "closed: " + message);
    }

    // notify websocket error
    @OnWebSocketError
    public void wsError() {
    }

}