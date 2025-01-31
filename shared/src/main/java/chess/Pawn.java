package chess;

import java.util.Collection;

public class Pawn extends ChessPiece {
    public Pawn(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.PAWN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // pawn movement
        return super.pieceMoves(board, myPosition);
    }
}
