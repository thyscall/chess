package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook extends ChessPiece {
    public Rook(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.ROOK);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int [][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};  // left, right, down, up

        // this is the same for loop that is in Bishop class, could be used in Queen class as well
        // linear moves
        for (int[] direction : directions) {
            int row = myPosition.getRow(), col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];
                ChessPosition newPosition = new ChessPosition(row, col);

                if (!board.isValidPosition(newPosition)) break;
                ChessPiece targetPiece = board.getPiece(newPosition);

                if (targetPiece == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (targetPiece.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition,null));
                    }
                    break;
                }
            }
        }
        return validMoves;
    }
}
