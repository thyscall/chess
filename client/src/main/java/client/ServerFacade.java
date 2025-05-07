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
        this.makeRequest("DELETE", "/db", null, null, null);
    }

    public AuthData register( RegisterRequest request) throws Exception {
        return this.makeRequest("POST", "/user", request, AuthData.class, null);
    }

    public AuthData login(LoginRequest request) throws Exception {
        return this.makeRequest("POST", "/session", request, AuthData.class, null);
    }

    public AuthData logout(LogoutRequest request) throws Exception {
        return this.makeRequest("DELETE", "/session", request, AuthData.class, request.getAuthToken());
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws Exception {
        return this.makeRequest("POST", "/game", request, CreateGameResult.class, authToken);

    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return this.makeRequest("GET", "/game", null, ListGamesResult.class, authToken);
    }

    // Spark.put("/game", new JoinGameHandler(db));
    public void joinGame(String authToken, JoinGameRequest request) throws Exception {
        this.makeRequest("PUT", "/game", request, null, authToken);
    }


    // "handle sending and receiving HTTP requests to and from your server"
    // T for generic data type
    // build URL with server and API found in Server.java calls
    // connect to URL
    // error only if errors are bad, not 200  level
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        URL url = (new URI(serverURL + path)).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(true);

        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
        }

        if (authToken != null) {
            http.setRequestProperty("Authorization", authToken);
        }


        if (request != null) {
            String jsonReq = new com.google.gson.Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(jsonReq.getBytes());
            }
        }

        int status = http.getResponseCode();
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


