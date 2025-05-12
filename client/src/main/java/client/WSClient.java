package client;

import com.google.gson.Gson;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import java.net.URI;
import java.util.concurrent.Future;

@WebSocket
public class WSClient {
    // connect to /ws
    // UserGameCommands: Connect, Make_Move, Resign, Leave
    // receive messages from ServerMessage
    // show messages in UI

    private final Gson gson = new Gson();
    private final ServerMessageObserver observer;
    private Session session;

    public WSClient(ServerMessageObserver observer) {
        this.observer = observer;
    }

    // connect to /ws endpoint
    public void connect(String serverUri) throws Exception {
        WebSocketClient client = new WebSocketClient();
        client.start();
        URI uri = new URI(serverUri); // "localhost:8080/ws"
        Future<Session> future = client.connect(this, uri);
        session = future.get();
    }

    public void sendCommand(UserGameCommand command) {
        try {
            if (session != null && session.isOpen()) {
                String json = gson.toJson(command);
                session.getRemote().sendString(json);
            } else {
                observer.notifyMessage(ServerMessage.error("WebSocket not open"));
            }
        } catch (Exception error) {
            observer.notifyMessage(ServerMessage.error("Could not send command: " + error.getMessage()));
        }
    }

    // receive message from server
    @OnWebSocketMessage
    public void whenMessage(String message) {
        ServerMessage text = gson.fromJson(message, ServerMessage.class);
        observer.notifyMessage(text);
    }

    // notif when websocket is closed
    @OnWebSocketClose
    public void whenClosed(int statusCode, String why) {
        observer.notifyMessage(ServerMessage.notification("WebSocket closed" + why));
    }

    // when error
    @OnWebSocketError
    public void whenError(Throwable why) {
        observer.notifyMessage(ServerMessage.error("WebSocket error" + why));
    }
}
