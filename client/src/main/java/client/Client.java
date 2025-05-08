package client;

import model.LoginRequest;
import model.LogoutRequest;
import model.RegisterRequest;

import java.util.Scanner;

public class Client {
    private final ServerFacade server;
    private final Scanner scanner;
    private String authToken = null;

    public Client(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("It's Chess Game Time!");
        // check auth token to pass to menu after login
        while (true) {
            if (authToken == null) {
                preLogin();
            } else {
                postLogin();
            }
        }
    }

    private void preLogin() {
        System.out.print("Login >");
        String userInput = scanner.nextLine().trim().toLowerCase();

        switch (userInput) {
            case "help" -> loginHelp();
            case "register" -> runRegister();
            case "login" -> runLogin();
            case "quit" -> {
                System.out.println("See ya!");
                System.exit(0);
            }
            default -> System.out.println("Sorry, I didn't quite get that. Try 'help'");
        }
    }

    private void postLogin() {
        System.out.print("Logout >");
        String userInput = scanner.nextLine().trim().toLowerCase();

        switch (userInput) {
            case "help" -> loginHelp();
            case "logout" -> runLogout();
            // functionality post auth
            case "create game" -> runCreateGame();
            case "list games" -> runListGames();
            case "play game" -> runJoinGame("white");
            case "observe game" -> runJoinGame(null);

            case "quit" -> {
                System.out.println("See ya!");
                System.exit(0);
            }
            default -> System.out.println("Sorry, I didn't quite get that. Try 'help'");
        }
    }

    private void runLogout() {
        // logout by making auth token null
        try {
            var req = new LogoutRequest(authToken);
            server.logout(req);
            authToken = null;
            System.out.println("Log out successful!");
        } catch (Exception error) {
            System.out.println("Logout total failure..." + error.getMessage());
        }
    }

    // login menu, what user will see (UI)
    private void loginHelp() {
        System.out.println("""
                Commands:
                - help          >>> show this help menu
                - register      >>> create an account
                - login         >>> enter your account
                - logout        >>> exit your account
                - create game   >>> start new game
                - list games    >>> see all games
                - play game     >>> let's play chess!
                - observe game  >>> just watch a game
                - quit          >>> exit chess
                """);
    }

    private void runLogin() {
        // get username, password, and email
        System.out.print("username: ");
        String username = scanner.nextLine().trim();
        System.out.print("password: ");
        String password = scanner.nextLine().trim();

        try {
            var req = new LoginRequest(username, password);
            var authData =  server.login(req);
            authToken = authData.authToken();
            System.out.println(authData.username() + " logged in!");
        } catch (Exception error) {
            System.out.println("Registration failed... ");
        }
    }

    private void runRegister() {
        // get username, password, and email
        System.out.print("username: ");
        String username = scanner.nextLine().trim();
        System.out.print("password: ");
        String password = scanner.nextLine().trim();
        System.out.print("email: ");
        String email = scanner.nextLine().trim();

        // try login, catch failed login
        try {
            var req = new RegisterRequest(username, password, email);
            var authData = server.register(req);
            authToken = authData.authToken();
            System.out.println(authData.username() + "logged in!");
        } catch (Exception error) {
            System.out.println("Registration failed... ");
        }
    }

    // run client UI
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        new Client(serverUrl).run();
    }

}
