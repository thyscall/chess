package client;

import com.google.gson.Gson;


import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;
import java.util.concurrent.Future;

public class WSClient extends Endpoint {
    // connect to /ws
    // UserGameCommands: Connect, Make_Move, Resign, Leave
    // receive messages from ServerMessage
    // show messages in UI
    private final Consumer<String> messageHandler;
    private Session session;

    public WSClient(String serverUrl, Consumer<String> messageHandler) throws Exception {
        this.messageHandler = messageHandler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, URI.create(serverUrl));
    }
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        System.out.println("WebSocket connected");

        session.addMessageHandler(String.class, message -> {
            if (messageHandler != null) {
                messageHandler.accept(message);
            }
        });
    }

    // notif when websocket is closed
    @Override
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Websocket closed");
    }

    // when error
    @Override
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error");
    }

    public void send(String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        } else {
            throw new IOException("Websocket failed to open");
        }
    }

    public void close() throws IOException {
        if (session != null) {
            session.close();
        }
    }

}
// make sure class extends endpoint javax.websocket.endpoint
// look at petshop communicator
// web socket instruction