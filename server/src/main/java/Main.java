import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            var server = new server.Server();
            int port = server.run(8080);
            System.out.printf("♕ 240 Chess Server running on port %d%n", port);
        } catch (Exception error) {
            System.err.println("Server start failed");
        }
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        new Server().run(8080);
    }
}