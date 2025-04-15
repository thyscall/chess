package model;

public class RegisterResult {
    private String username;
    private String authToken;

    public RegisterResult() {}

    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    // username getter method
    public String getUsername() {
        return username;
    }
    // authToken getter method
    public String getAuthToken() {
        return authToken;
    }
    //setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
