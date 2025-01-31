package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen extends ChessPiece {
    public Queen(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.QUEEN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Rook straight moves
                {-1, -1}, {1, -1}, {1, 1}, {-1, 1} // Bishop diagonal moves
        };

        for (int[] direction : directions) {
            int row = myPosition.getRow(), col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];
                ChessPosition newPosition = new ChessPosition(row, col);

                if (!board.isValidPosition(newPosition)) break;
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition,null));
                    }
                    break;
                }
            }
        }
        return validMoves;
    }

}
