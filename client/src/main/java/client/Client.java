package client;

import chess.*;
import model.*;
import websocket.commands.UserGameCommand;

import java.util.*;

import websocket.messages.ServerMessage;

public class Client implements ServerMessageObserver {
    private final ServerFacade server;
    private final Scanner scanner;
    private String authToken = null;
    private WSClient wsClient;
    private Integer thisGameID = null;
    private List<GameData> gamesList = new ArrayList<>();
    private boolean isFlipped = false;

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

            isFlipped = false;

            // get current board
            ChessGame thisGameBoard = server.getGame(authToken, gameID).game();

            // draw board without highlights
            drawBoard(thisGameBoard, isFlipped, Set.of(), null);
        } catch (Exception error) {
            System.out.println("Sorry, unable to observe game...");
        }
    }

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
        // get username, password, and email, shown in console UI
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
            System.out.println("Registration failed... " + error.getMessage());
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
            System.out.println("Registration failed... " + error.getMessage());
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
        // invalid user not recognized
        if (!userTeamColor.equals("white") && !userTeamColor.equals("black")) {
            System.out.println("I don't recognize that team. White or Black? ");
            return;
        }

        // then join game as team color
        int gameID = gamesList.get(index).gameID();
        try {
            server.joinGame(authToken, new JoinGameRequest(userTeamColor, gameID));
            thisGameID = gameID;
            System.out.println("Game joined as " + userTeamColor + "!");

            isFlipped = userTeamColor.equals("black");
            // websocket phase 6 + Connect
            wsClient = new WSClient((ServerMessageObserver) this);
            wsClient.connect("ws://localhost:8080/ws");
            wsClient.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, thisGameID));

            // gameplay implemented in phase 6
            gameplay();

        } catch (Exception error) {
            System.out.println("Failed to join game... " + error.getMessage());
        }
    }

    private void gameplay() {
        // help menu that shows command options
        System.out.println("Enter 'help' to see gameplay commands");

        // while loop that evaluates UserGameCommands
        while (true) {
            System.out.print("Game > ");
            String input = scanner.nextLine().trim().toLowerCase();
            String[] inWords = input.split("\\s+"); // *** WHAT REGEX NEEDED??
            // don't look at empty input
            if (inWords.length == 0) {
                continue;
            }
            // check input to see what commands are given in command line after parsed
            switch (inWords[0]) {
                // if MOVE, send command through websocket
                case "move" -> {
                    if (inWords.length != 3) {
                        System.out.println("Use this pattern: move g2 g3"); // pawn move as example
                        break;
                    }
                    try {
                        // 'from ___ position to ___ position' using parsed input
                        ChessMove move = new ChessMove(parsePos(inWords[1]), parsePos(inWords[2]), null);
                        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, thisGameID);
                        command.setMove(move);
                        wsClient.sendCommand(command);
                    } catch (Exception error) {
                        System.out.println("Invalid move command. Example: move g2 g4");
                    }
                }
                // if LEAVE, send command and break loop
                case "leave" -> {
                    wsClient.sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, thisGameID));
                    return;
                }
                // if RESIGN, send command through websocket
                case "resign" -> {
                    wsClient.sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, thisGameID));
                }
                // highlight possible moves
                // Allows the user to input the piece for which they want to highlight legal moves.
                // The selected piece’s current square and all squares it can legally move to are highlighted.
                // This is a local operation and has no effect on remote users’ screens
                case "highlight legal moves" -> {
                    System.out.println("Enter square (ex: g4");
                    String sqInput = scanner.nextLine().trim();
                    try {
                        ChessPosition pos = parsePos(scanner.nextLine().trim());
                        // highlight move helper
                        highlightMoves(pos);
                    } catch (Exception error) {
                        System.out.println("Invalid move request.");
                    }

                }
                // Redraws the chess board upon the user’s request.
                case "redraw" -> {
                    // add functionality try catch
                    // getGame, drawBoard without highlights
                    // error if no redraw
                    try {
                        var game = server.getGame(authToken, thisGameID).game();
                        // board with no highlights
                        drawBoard(game, isFlipped, Set.of(), null);
                    } catch (Exception error) {
                        System.out.println("Could not redraw board");
                    }
                }

                case "help" -> {
                    System.out.println("""
                            move        >>> make a move
                            resign      >>> resign from the game
                            leave       >>> exit the game
                            highlight   >>> see a piece's legal moves
                            redraw      >>> refresh board view
                            help        >>> show this menu
                            """);
                }
                default -> System.out.println("Command not recognized. Try 'help' for valid commands.");
            }
        }
    }

    private ChessPosition parsePos(String input) {
        // check for len = 2 "g2"
        if (input.length() != 2) {
            throw new IllegalArgumentException("Invalid format. Use something like 'g2'");
        }
        char colChar = input.charAt(0); // column identified in first character
        char rowChar = input.charAt(1); // row identified in first character

        if (colChar < 'a' || colChar > 'h' || rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException("Invalid square position. Enter coordinates found on the board.");
        }
        // change col value from string/letter to int row for backend
        // row will still be number, but change from string to int
        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        return new ChessPosition(row, col);
    }

    private void highlightMoves(ChessPosition pos) {
        try {
            // get most recent game condition
            ChessGame game = server.getGame(authToken, thisGameID).game();
            var moves = game.validMoves(pos);

            // select and highlight actual squares for possible moves
            Set<ChessPosition>highlightSquares = new HashSet<>();
            for (ChessMove move : moves) {
                highlightSquares.add(move.getEndPosition());
            }

            // draw board with highlighted squares
            drawBoard(game, isFlipped, highlightSquares, pos);
        } catch (Exception error) {
            System.out.println("Error highlighting moves: " + error.getMessage());
        }
    }

// draw board in UI
    public void drawBoard(ChessGame game, Boolean isFlipped, Set<ChessPosition> highlights, ChessPosition selection) {
        // ANSI chars styling
        String reset = "\033[0m";
        String labels = "\033[38;2;89;60;40m";              // brown
        String darkSquares = "\033[48;2;89;60;40m";         // brown
        String lightSquares = "\033[48;2;220;201;163m";     // sandy
        String whitePieceColor = "\033[38;2;255;255;255m";  // white
        String blackPieceColor = "\033[38;2;0;0;0m";        // black
        // possible move highlights phase 6
        String yellowHighlight = "\033[48;2;255;255;0m";    // yellow
        String greenHighlight = "\033[48;2;0;255;0m";      // green

        ChessBoard board = game.getBoard();

        // Column labels after indent (3 chars)
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            char col = (char) ('a' + (isFlipped ? 7 - i : i));
            System.out.print(labels + col + "  " + reset);
        }
        System.out.println();

        // row labels
        for (int row = 0; row < 8; row++) {
            int normRow = isFlipped ? row : 7 - row;
            System.out.print(labels + (isFlipped ? row + 1 : 8 - row) + " " + reset);

            for (int col = 0; col < 8; col++) {
                int normCol = isFlipped ? 7 - col : col;
                ChessPosition thisPos = new ChessPosition(normRow + 1, normCol + 1);
                // every other square rotating color
                String sqColor = ((normRow + normCol) % 2 == 0) ? darkSquares : lightSquares;

                // legal move highlights
                if (selection != null && thisPos.equals(selection)) { // leave null option in case observer or redraw
                    sqColor = yellowHighlight;
                } else if (highlights != null && highlights.contains(thisPos)) {
                    sqColor = greenHighlight;
                }

                ChessPiece piece = game.getBoard().getPiece(thisPos);
                String pieceIcon = " ";

                if (piece != null) {
                    String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? whitePieceColor : blackPieceColor;

                    pieceIcon = switch (piece.getPieceType()) {
                        case KING -> color + "♚";
                        case QUEEN -> color + "♛";
                        case ROOK -> color + "♜";
                        case BISHOP -> color + "♝";
                        case KNIGHT -> color + "♞";
                        case PAWN -> color + "♟";
                    };
                }
                // print square and piece to board
                System.out.print(sqColor + " " + pieceIcon + " " + reset);
            }
            // right side vert labels
            System.out.print(" " + labels + (isFlipped ? row + 1 : 8 - row) + reset);
            System.out.println();
        }
    // Bottom column labels
    System.out.print("   ");
    for (int i = 0; i < 8; i++) {
        char col = (char) ('a' + (isFlipped ? 7 - i : i));
        System.out.print(labels + col + "  " + reset);
    }
    System.out.println();
}

    @Override
    public void notifyMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                drawBoard(message.getGame(), isFlipped, Set.of(), null);
            }
            case NOTIFICATION -> {
                System.out.println(message.getMessage());
            }
            case ERROR -> System.err.println(message.getErrorMessage());
        }
    }

    // run client UI
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        new Client(serverUrl).run();
    }

}
