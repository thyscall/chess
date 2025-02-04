package chess;

import java.util.ArrayList;
import java.util.Collection;

// implement the PieceMovesCalculator
public class KnightMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { // implementation is done in the subclasses
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        ChessPiece currentPiece = board.getPiece(myPosition);
        if (currentPiece == null){
            return validMoves;
        }

        for (int[] dir : directions) {

            ChessPosition newPos = new ChessPosition(myPosition.getRow() + dir[0], myPosition.getColumn() + dir[1]);

            if (!board.isValidPosition(newPos)) continue; // Skip invalid positions

            ChessPiece targetPiece = board.getPiece(newPos);

            // If the square is empty or contains an enemy piece, it's a valid move
            if (targetPiece == null || targetPiece.getTeamColor() != currentPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, newPos, null));
            }
        }
        return validMoves;
    }
}