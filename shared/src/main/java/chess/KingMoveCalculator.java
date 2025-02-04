package chess;

import java.util.ArrayList;
import java.util.Collection;

// implement the PieceMovesCalculator
public class KingMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { // implementation is done in the subclasses
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // straight moves
                {-1, -1}, {1, -1}, {1, 1}, {-1, 1} // diagonal moves
        };

        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + direction[0],
                    myPosition.getColumn() + direction[1]);

            if (!board.isValidPosition(newPosition)) continue; // Skip invalid positions

            ChessPiece targetPiece = board.getPiece(newPosition);

            if (targetPiece == null || targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return validMoves;
    }
}