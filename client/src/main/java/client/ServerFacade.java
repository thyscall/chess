package client;

import model.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

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

    public void clear() throws Exception {
        this.makeRequest("DELETE", "/db", null, null);
    }

    public AuthData register( RegisterRequest request) throws Exception {
        return this.makeRequest("POST", "/user", request, AuthData.class);
    }

    public void login(LoginResult loginResult) throws Exception {}

    public void logout(LogoutRequest logoutRequest) throws Exception {}

    public void createGame(CreateGameRequest createGameRequest) throws Exception {}

    public void listGames(ListGamesRequest listGamesRequest) throws Exception {}

    public void joinGame(JoinGameRequest joinGameRequest) throws Exception {}


    // "handle sending and receiving HTTP requests to and from your server"
    // T for generic data type
    // build URL with server and API found in Server.java calls
    // connect to URL
    // error only if errors are bad, not 200  level
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        URL url = (new URI(serverURL + path)).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(true);

        if (request != null) {
            http.addRequestProperty("Body", "application/json");
            String JSONReq = new com.google.gson.Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(JSONReq.getBytes());
            }
        }
        // send http request with .connect()
        http.connect();

        var status = http.getResponseCode();
        // error only if errors are bad, not 200  level
        if (status / 100 != 2) {
            throw new Exception("Server error: " + status);
        }

        if (responseClass == null) {
            return null;
        }

        try (InputStream responseContent = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(responseContent);
            return new com.google.gson.Gson().fromJson(reader, responseClass);
        }
    }
}


