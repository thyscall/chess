//// make sure class extends endpoint javax.websocket.endpoint
//// look at petshop communicator
//// web socket instruction

package client;

import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint {

    private Session session;

    public WSClient(String uri, MessageHandler.Whole<String> onMessage) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        // connect to server and messages
        this.session = container.connectToServer(this, URI.create(uri));
        this.session.addMessageHandler(onMessage);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("WebSocket Client Connected to Server");
    }

    public void send(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(new com.google.gson.Gson().toJson(command));
    }

    public void close() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}