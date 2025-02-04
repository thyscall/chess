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

        for (int[] dir : directions) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + dir[0], myPosition.getColumn() + dir[1]);
            if (board.isValidPosition(newPos) && (board.getPiece(newPos) == null || board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor())) {
                validMoves.add(new ChessMove(myPosition, newPos, null));
            }
        }
        return validMoves;
    }
}