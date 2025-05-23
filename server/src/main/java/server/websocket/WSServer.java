package server.websocket;


import chess.*;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
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
    private static final ConcurrentHashMap<Integer, Set<Session>> GAME_SESSION = new ConcurrentHashMap<>();
    private static final Map<Session, String> SESSION_USERS = new ConcurrentHashMap<>();

    // make web socket connections
    // follow games with gameID
    public WSServer() throws DataAccessException {
    }
    // notification when websocket is init
    @OnWebSocketConnect
    public void wsConnected(Session session) {
        System.out.println("WebSocket connected to " + session.getRemoteAddress());
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
            SESSION_USERS.put(session, auth.username());

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

        GAME_SESSION.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        GAME_SESSION.get(gameID).add(session);

        try {
            GameData game = db.getGame(gameID);
            // show board
            session.getRemote().sendString(gson.toJson(ServerMessage.loadGame(game.game())));
            String who = username.equals(game.whiteUsername()) ? "white player" :
                    username.equals(game.blackUsername()) ? "black player" : "observer";
            // tell all other observers
            broadcastOthers(gameID, session, ServerMessage.notification(username + " joined as " + who));
            // give error message
        } catch (Exception error) {
            send(session, ServerMessage.error("Error loading game: " + error.getMessage()));
        }
    }

    private void broadcastOthers(int gameID, Session notSession, ServerMessage notification) {
        Set<Session> sessions = GAME_SESSION.get(gameID);

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

        Set<Session> sessions = GAME_SESSION.get(gameID);

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

            // check if game is over before action
            if (game.game().isGameOver()) {
                sendError(session, "Unable to resign. Game is over!");
                return;
            }
            //end game when someone resigns
            ChessGame updatedGame = game.game();
            updatedGame.setGameOver(true);

            db.updateGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), updatedGame));
            broadcast(command.getGameID(), ServerMessage.notification(username + " resigned from the game!"));
        } catch (Exception error) {
            send(session, ServerMessage.error("Error resigning."));
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        try {
            Set<Session> sessions = GAME_SESSION.get(command.gameID);
            // check if user leaves, if so, session set will be null
            // notify players/observers that someone left
            if (sessions != null) {
                sessions.remove(session);
            }
            String username = SESSION_USERS.get(session);

            // then remove users in session
            SESSION_USERS.remove(session);
            GameData gameData = db.getGame(command.gameID);
            boolean updated = false;

            // if white resigns
            if (username != null && username.equals(gameData.whiteUsername())) {
                gameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(),gameData.game());
                updated = true;
            }

            // if black resigns
            else if (username != null && username.equals(gameData.blackUsername())) {
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(),gameData.game());
                updated = true;
            }
            // update space available for someone else to join
            if (updated) {
                db.updateGame(gameData);
            }

            broadcast(command.getGameID(), ServerMessage.notification("Someone has left the game"));
        } catch (Exception error) {
            send(session, ServerMessage.error("Error leaving game"));
        }
    }

    private void handleMakeMove(Session session, String message, UserGameCommand command, String username) {
        GameData game = getGame(command.gameID);
        if (game == null) {
            sendError(session, "Invalid game ID");
            return;
        }
        // only allow moves from white or black team players
        boolean isUserWhite = username.equals(game.whiteUsername());
        boolean isUserBlack = username.equals(game.blackUsername());
        if (!isUserWhite && !isUserBlack) {
            sendError(session, "Only players can make moves");
            return;
        }
        // only make moves if it is players turn
        ChessGame.TeamColor thisTurn = game.game().getTeamTurn();
        if ((isUserWhite && thisTurn != ChessGame.TeamColor.WHITE || isUserBlack && thisTurn != ChessGame.TeamColor.BLACK)) {
            sendError(session, "Not your turn!");
            return;
        }
        try {
            // get the move
            var parsed = gson.fromJson(message, UserGameCommand.class);
            ChessMove move = parsed.getMove();
            // verify the game number
            GameData gameNum = db.getGame(command.getGameID());
            ChessGame chessGame = gameNum.game();
            // actual move happens
            chessGame.makeMove(move);
            db.updateGame(new GameData(gameNum.gameID(), gameNum.whiteUsername(), gameNum.blackUsername(), gameNum.gameName(), chessGame));

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
            send(session, ServerMessage.error("Move error. Try again"));
        }
    }

    private GameData getGame(Integer gameID) {
        try {
            return db.getGame(gameID);
        } catch ( DataAccessException error) {
            error.printStackTrace();
            return null;
        }
    }

    private void sendError(Session session, String error) {
        send(session, ServerMessage.error(error));
    }

    // notify when web socket is closed
    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        System.out.println("WebSocket closed: " + reason);
    }

    // notify websocket error
    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println(("Websocket error:" + error.getMessage()));
    }

}
