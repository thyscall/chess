package client;

import model.*;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    // methods needed
    // clear()
    // register(RegisterRequest)
    // login(LoginRequest)
    // logout(LogoutRequest)
    // createGame(CreateGameRequest)
    // listGames(ListGamesRequest)
    // joinGame(JoinGameRequest)

    public void clear() throws Exception {}

    public void register( RegisterRequest request) throws Exception {}

    public void login(LoginResult loginResult) throws Exception {}

    public void logout(LogoutRequest logoutRequest) throws Exception {}

    public void createGame(CreateGameRequest createGameRequest) throws Exception {}

    public void listGames(ListGamesRequest listGamesRequest) throws Exception {}

    public void joinGame(JoinGameRequest joinGameRequest) throws Exception {}
}
