package chess;

import java.util.ArrayList;
import java.util.Collection;

public class King extends ChessPiece {
    public King(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.KING);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // straight moves
                {-1, -1}, {1, -1}, {1, 1}, {-1, 1} // diagonal moves
        };
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + direction[0],
                    myPosition.getColumn() + direction[1]);
            if (board.isValidPosition(newPosition) && (board.getPiece(newPosition) == null) ||
                    board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return validMoves;
    }
}