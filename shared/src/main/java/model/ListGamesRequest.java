package model;

import java.util.List;

public class ListGamesRequest {
    private String authToken;

    public ListGamesRequest() {}

    public ListGamesRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
