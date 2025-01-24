package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class Bishop extends ChessPiece {

    //Set the team color and which piece
    public Bishop(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.BISHOP);
    }


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // where can a Bishop move?
        int[][] directions = {
                {1, 1}, // Up - right as both y and x increase
                {1, -1}, // Up - left when y increases and x decreases
                {-1, 1}, // Down - right when y decreases and x increases
                {-1, -1}, // Down - right when both y and x decrease
        };

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0]; // diagonally move in row
                col += direction[1]; // diagonally move in column

                // check if the new position in out of bounds
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break; // stop checking, break the loop
                }

                ChessPosition newPos = new ChessPosition(row, col); // var to hold value for piece's new position
                ChessPiece pieceNewPosition = board.getPiece(newPos); // set new position

                // check to see if there is a piece in the new piece position
                if (pieceNewPosition == null) {
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    // is the piece and an opponent's piece?
                    if (pieceNewPosition.getTeamColor() != this.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPos, null)); // capture opponent piece
                    }
                    break; // stop when opponent piece is captured
                }
            }
        }
        return validMoves;
    }
}
