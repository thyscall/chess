package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

/**
 * See Phase 0 Design Tips video for visual + flowchart of additional PieceMovesCalculator class implementation
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    // Constructor
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(pieceType) {
            case ROOK -> {
                return new RookMoveCalculator().pieceMoves(board, myPosition); // return Rook moves into an array
            }
//            case KNIGHT -> { // NOT IMPLEMENTED
//                return new KnightMoveCalculator().pieceMoves(board, myPosition);
//            }
            case BISHOP -> {
                return new  BishopMoveCalculator().pieceMoves(board, myPosition); // return instances method return statement -> a collection of chess moves
            }
            case QUEEN -> {
                return new QueenMoveCalculator().pieceMoves(board, myPosition);
            }
            // switch case for all pieces
            default -> {
                return new ArrayList<>();
            }
        }
    }


}
