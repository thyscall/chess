package chess;

import java.util.ArrayList;
import java.util.Collection;

// implement the PieceMovesCalculator
public class BishopMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { // implementation is done in the subclasses
        Collection<ChessMove> validMoves = new ArrayList<>();

        // where can a Bishop move? Diagonal
        int[][] directions = {
                {1, 1}, // Up - right as both y and x increase
                {1, -1}, // Up - left when y increases and x decreases
                {-1, 1}, // Down - right when y decreases and x increases
                {-1, -1}, // Down - left when both y and x decrease
        };
        // check diagonal move
        for (int[] direction : directions) {
            int row = myPosition.getRow(), col = myPosition.getColumn();

            while (true) {
                row += direction[0]; // diagonally move in row
                col += direction[1]; // diagonally move in column

                ChessPosition newPos = new ChessPosition(row, col); // var to hold value for piece's new position

                if (!board.isValidPosition(newPos)) {
                    break; // check if position is valid by accessing isValidPosition method in ChessBoard
                }

                ChessPiece piece = board.getPiece(newPos); // set new position

                // check to see if there is a piece in the new piece position
                if (piece == null) {
                    // able to move to empty square, add new position to valid moves
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    // is the piece and an opponent's piece?
                    // GET THE PIECE AT THE POSITION
                    // if the piece's team is not equal to the current piece's team
                    if (piece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPos, null)); // capture opponent piece
                    }
                    break; // stop when opponent piece is captured
                }
            }
        }
        return validMoves;
    }
}