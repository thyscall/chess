package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;

    // Constructor that initializes a new game with empty board
    public ChessGame() {
        this.currentTurn = TeamColor.WHITE; // white always starts
        this.board = new ChessBoard(); // new chess board
        this.board.resetBoard(); // wipe the board
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition); // piece position at the start of the game
        if (piece == null || piece.getTeamColor() != currentTurn) { // if there is no piece or it is the opponents piece, return null
            return null;
        }

        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition()); // get piece at its starting position
        if (piece == null || piece.getTeamColor() != currentTurn) { // is there a piece? or is the piece not yours?
            throw new InvalidMoveException("Invalid move: No piece or wrong turn."); // if so, move is invalid
        }
        Collection<ChessMove> validMoves = piece.pieceMoves(board, move.getStartPosition());
        if (!validMoves.contains(move)) { // if it is not a valid move, throw error
            throw new InvalidMoveException("Invalid move: Move not allowed.");
        }
        ChessBoard tempBoard = new ChessBoard(board);
        tempBoard.movePiece(move);

        ChessPosition kingPosition = findKing(currentTurn, tempBoard);

        if (isUnderAttack(kingPosition, currentTurn, tempBoard)){
            throw new InvalidMoveException("Invalid: King is still in check");
        }
        // move piece
        board.movePiece(move); // makeMove from ChessBoard class

        // Change turn
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor, board); // find the team's king
        return kingPos == null && isUnderAttack(kingPos, teamColor, board); // can it be captured? isUnderAttack helper function
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) { // cannot be in check mate if king is not in check
            return false;
        }
        // if no valid moves, king is in check mate, mate!
        for (ChessPosition position : board.getAllPiecePositions(teamColor)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null) {
                for (ChessMove move : piece.pieceMoves(board, position)) {
                    ChessBoard boardCopy = new ChessBoard(board); // copy of board's actual state
                    boardCopy.movePiece(move); // simulated move, not actual
                    if (!isUnderAttack(findKing(teamColor, board), teamColor, board)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
       for (ChessPosition position : board.getAllPiecePositions(teamColor)) {
           ChessPiece piece = board.getPiece(position);
           if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
               return position;
           }
        }
        throw new IllegalStateException("King was not found for team" + teamColor); // King is not found, means game is invalid
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) { // if king in check, stalemate is false
            return false;
        }
        // if no valid moves exist, it's a stalemate
        // if any piece has a valid move, stalemate is false
        for (ChessPosition position : board.getAllPiecePositions(teamColor)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && !piece.pieceMoves(board, position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean isUnderAttack(ChessPosition position, TeamColor teamColor, ChessBoard tempBoard) {
        TeamColor opponent = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (ChessPosition opponentPosition : board.getAllPiecePositions(opponent)) {
            ChessPiece piece = board.getPiece(opponentPosition);
            if (piece != null) {
                for (ChessMove move : piece.pieceMoves(board, opponentPosition)) {
                    if (move.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}


