package chess;

import java.util.ArrayList;
import java.util.Collection;

// implement the PieceMovesCalculator
public class QueenMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { // implementation is done in the subclasses
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Rook straight moves
                {-1, -1}, {1, -1}, {1, 1}, {-1, 1} // Bishop diagonal moves
        };
        // same for loop as Bishop and Rook
        for (int[] direction : directions) {
            int row = myPosition.getRow(), col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];
                ChessPosition newPosition = new ChessPosition(row, col);

                if (!board.isValidPosition(newPosition)) {
                    break;
                }
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (piece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition,null));
                    }
                    break;
                }
            }
        }
        return validMoves;
    }

}
