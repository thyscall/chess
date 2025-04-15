package model;

public class AuthResponse {
    private String username;
    private String authToken;

    public AuthResponse() {}

    public AuthResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
