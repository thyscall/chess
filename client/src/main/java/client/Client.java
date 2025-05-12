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
            case "help" -> logoutHelp();
            case "logout" -> runLogout();
            // functionality post auth
            case "create game" -> runCreateGame();
            case "list games" -> runListGames();
            case "play game" -> runJoinGame();
            case "observe game" -> runObserveGame();

            case "quit" -> {
                System.out.println("See ya!");
                System.exit(0);
            }
            default -> System.out.println("Sorry, I didn't quite get that. Try 'help'");
        }
    }

    private void runObserveGame() {
        runListGames();

        if (gamesList.isEmpty()) {
            System.out.println("No games available to observe!");
            return;
        }
        System.out.print("Choose a game number to observe it: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine().trim()) -1;
            if (index < 0 || index >= gamesList.size()) {
                System.out.println("That's not a valid game number. Try again");
                return;
            }
        } catch (NumberFormatException error) {
            System.out.println("Enter a valid game number");
            return;
        }

        int gameID = gamesList.get(index).gameID();
        try {
            server.joinGame(authToken, new JoinGameRequest(null, gameID));
            System.out.println("Observing game...");
            drawBoard(false);
        } catch (Exception error) {
            System.out.println("Sorry, unable to observe game...");
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
            System.out.println("Failed to list games");
        }
    }


    // login menu, what user will see (UI)
    private void loginHelp() {
        System.out.println("""
                Commands:
                - help          >>> show this help menu
                - register      >>> create an account
                - login         >>> enter your account
                - quit          >>> exit chess
                """);
    }

    private void logoutHelp() {
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
            System.out.println(authData.username() + " logged in!");
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
            System.out.println("Logout total failure... ");
        }
    }

    private void runCreateGame() {
        System.out.println("New game name: ");
        String gameName = scanner.nextLine().trim();

        try {
            var result = server.createGame(authToken, new CreateGameRequest(gameName));
            System.out.println("New Game Created!");
        } catch (Exception error) {
            System.out.println("Create game total failure... ");
        }
    }

    private void runJoinGame() {
        runListGames();

        if (gamesList.isEmpty()) {
            System.out.println("Games list is empty. Create a game name before joining one.");
            return;
        }

        // get game number before asking for team color
        System.out.print("Enter game number to join game: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (index < 0 || index >= gamesList.size()) {
                System.out.println("Game number is invalid");
                return;
            }
        } catch (NumberFormatException error) {
            System.out.println("That game number doesn't look right... Try another");
            return;
        }

        // get team color from user
        System.out.print("Choose your team. White or Black? ");
        String userTeamColor = scanner.nextLine().trim().toLowerCase();
        if (!userTeamColor.equals("white") && !userTeamColor.equals("black")) {
            System.out.println("I don't recognize that team. White or Black? ");
            return;
        }

        // then join game as team color
        int gameID = gamesList.get(index).gameID();
        try {
            server.joinGame(authToken, new JoinGameRequest(userTeamColor, gameID));
            System.out.println("Game joined as " + userTeamColor + "!");

            // then draw board
            drawBoard(userTeamColor.equals("black"));
        } catch (Exception error) {
            System.out.println("Failed to join game... ");
        }
    }

    public void drawBoard(boolean flip) {
        // ANSI chars styling
        String reset = "\033[0m";
        String labels = "\033[38;2;89;60;40m";              // brown
        String darkSquares = "\033[48;2;89;60;40m";         // brown
        String lightSquares = "\033[48;2;220;201;163m";     // sandy
        String whitePieceColor = "\033[38;2;255;255;255m";  // white
        String blackPieceColor = "\033[38;2;0;0;0m";        // black

        String[] whitePieces = {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"};
        String[] blackPieces = {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"};

        // Column labels after indent (3 chars)
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            char col = (char) ('a' + (flip ? 7-i : i));
            System.out.print(labels + col + "  " + reset);
        }
        System.out.println();

        // row labels left side
        for (int row = 0; row < 8; row++) {
            int normRow = flip ? row : 7 - row;
            System.out.print(labels + (flip ? row + 1 : 8 - row) + " " + reset);

            for (int col = 0; col < 8; col++) {
                int normCol = flip ? 7 - col : col;
                // every other square rotating color
                String boardColor = ((normRow + normCol) % 2 == 0) ? darkSquares : lightSquares;
                String piece = " ";

                if (!flip) { // normal board
                    // white pieces on row 1 of game board, row 0 in back end
                    if (normRow == 0) {
                        piece = whitePieceColor + whitePieces[normCol];
                    }
                    // white pawns on row 2 of board, row 1 in back end
                    else if (normRow == 1) {
                        piece = whitePieceColor + "♟";
                    }
                    // add black pawns to row 7, row 6 in back end
                    else if (normRow == 6) {
                        piece = blackPieceColor + "♟";
                    }
                    // add normal black pieces to row 8, row 7 in back end
                    else if (normRow == 7) {
                        piece = blackPieceColor + blackPieces[normCol];
                    }
                } else {
                    if (normRow == 7) {
                        piece = blackPieceColor + blackPieces[normCol];
                    }
                    else if (normRow == 6) {
                        piece = blackPieceColor + "♟";
                    }
                    else if (normRow == 1) {
                        piece = whitePieceColor + "♟";
                    }
                    else if (normRow == 0) {
                        piece = whitePieceColor + whitePieces[normCol];
                    }
                }

                System.out.print(boardColor + " " + piece + " " + reset);
            }
            // right side vert labels
            System.out.print(" " + labels + (flip ? row + 1 : 8 - row) + reset);
            System.out.println();
        }

        // Bottom column labels
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            char col = (char) ('a' + (flip ? 7 - i : i));
            System.out.print(labels + col + "  " + reset);
        }
        System.out.println();
    }


    // run client UI
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        new Client(serverUrl).run();
    }

}
