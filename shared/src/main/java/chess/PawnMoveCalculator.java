package chess;

import java.util.ArrayList;
import java.util.Collection;

// implement the PieceMovesCalculator
public class PawnMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { // implementation is done in the subclasses
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = board.getPiece(myPosition);

        if (piece == null || piece.getPieceType() != ChessPiece.PieceType.PAWN) { // if there is no piece (null) or the piece type is not a pawn, return empty valid moves array
            return validMoves; // this will be an empty array because the piece is null, or it is not a pawn
        }

        ChessGame.TeamColor teamColor = piece.getTeamColor();
        int direction; // White pawn moves up, Black pawn moves down
        int startRow; // starting row
        int promoRow;  // promotion row

        if (teamColor == ChessGame.TeamColor.WHITE) {
            direction = 1; // white moves up rows
            startRow = 2;
            promoRow = 8;
        } else {
            direction = -1; // black moves down rows
            startRow = 7;
            promoRow = 1;
        }

        // Move forward one square
        ChessPosition oneStep = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.isValidPosition(oneStep) && board.getPiece(oneStep) == null) {
            addPawnMove(validMoves, myPosition, oneStep, promoRow);

            // Move forward two squares (only from starting position) – 2 x direction to get two steps
            ChessPosition twoSteps = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
            if (myPosition.getRow() == startRow && board.isValidPosition(twoSteps) && board.getPiece(twoSteps) == null) {
                validMoves.add(new ChessMove(myPosition, twoSteps, null));
            }
        }

        // Capture diagonally (left and right)
        int[][] diagonalCap = {{direction, -1}, {direction, 1}};
        for (int[] move : diagonalCap) {
            ChessPosition capturePos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);

            if (!board.isValidPosition(capturePos)) continue; // Skip invalid positions

            ChessPiece targetPiece = board.getPiece(capturePos);
            if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                addPawnMove(validMoves, myPosition, capturePos, promoRow);
            }
        }

        return validMoves;
    }

    // helper to handle pawn promo
    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int promotionRow) {
        if (end.getRow() == promotionRow) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}