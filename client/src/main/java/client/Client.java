package client;

import model.*;

import java.util.ArrayList;
import java.util.List;
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



    private List<GameData> gamesList = new ArrayList<>();

    private void runListGames() {
        try {
            var result = server.listGames(authToken);
            gamesList = result.games();
            System.out.println("Games:");

            for (int i = 0; i < gamesList.size(); i ++) {
                var game = gamesList.get(i);
                System.out.printf("%d. %s | White: %s | Black: %s%n",
                        i + 1,
                        game.gameName(),
                        game.whiteUsername() != null ? game.whiteUsername() : "None",
                        game.blackUsername() != null ? game.blackUsername() : "None"
                );
            }
        } catch (Exception error) {
            System.out.println("Failed to list games: " + error.getMessage());
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

    private void runLogout() {
        // logout by making auth token null
        try {
            var req = new LogoutRequest(authToken);
            server.logout(req);
            authToken = null;
            System.out.println("Log out successful!");
        } catch (Exception error) {
            System.out.println("Logout total failure... " + error.getMessage());
        }
    }

    private void runCreateGame() {
        System.out.println("New game name: ");
        String gameName = scanner.nextLine().trim();

        try {
            var result = server.createGame(authToken, new CreateGameRequest(gameName));
            System.out.println("New Game Created! ID: " + result.gameID());
        } catch (Exception error) {
            System.out.println("Create game total failure... " + error.getMessage());
        }
    }

    private void runJoinGame(String userColor) {
        if (gamesList.isEmpty()) {
            System.out.println("Games list is empty. Create a game name before joining one.");
            return;
        }
        System.out.print("Enter game number to join game: ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (index < 0 || index >= gamesList.size()) {
                System.out.println("Game number is invalid");
                return;
            }

            int gameID = gamesList.get(index).gameID();
            server.joinGame(authToken, new JoinGameRequest(userColor, gameID));
            System.out.println("Game joined!");
        } catch (NumberFormatException error) {
            System.out.println("Enter a valid game number");
        } catch (Exception error) {
            System.out.println("Failed to join game... " + error.getMessage());
        }
    }

    // run client UI
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        new Client(serverUrl).run();
    }

}
