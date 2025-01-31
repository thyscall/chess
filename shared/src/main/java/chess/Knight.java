package chess;

import java.util.Collection;

public class Knight extends ChessPiece {
    public Knight(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.KNIGHT);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // knight movement
        return super.pieceMoves(board, myPosition);
    }
}
