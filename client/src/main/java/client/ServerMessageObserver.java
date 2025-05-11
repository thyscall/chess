package client;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notifyMessage(ServerMessage message);
}
