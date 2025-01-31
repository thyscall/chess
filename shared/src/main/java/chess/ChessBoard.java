package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

/**
 * See Phase 0 Design Tips video for visual
 */

public class ChessBoard {
    private ChessPiece[][] squares =  new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece; // 0 based indexing -> -1 after row and position column
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // clear board
        squares = new ChessPiece[8][8];

        // helper to set up piece
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;
        // pawns
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(2, col), new Pawn(white));
            addPiece(new ChessPosition(7, col), new Pawn(black));
        }

        // white back row
        ChessPiece[] whitePieces = {
                new Rook(white), new Knight(white), new Bishop(white), new Queen(white),
                new King(white), new Bishop(white), new Knight(white), new Rook(white)
        };
        // black back row
        ChessPiece[] blackPieces = {
                new Rook(black), new Knight(black), new Bishop(black), new Queen(black),
                new King(black), new Bishop(black), new Knight(black), new Rook(black)
        };

        // add main pieces (minus pawns) to their correct place on each team's back row
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(1, col), whitePieces[col - 1]);
            addPiece(new ChessPosition(8, col), blackPieces[col - 1]);
        }
    }

    public boolean isValidPosition(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8
                && position.getColumn() >= 1 && position.getColumn() <= 8;
    }




    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
