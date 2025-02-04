package chess;

import java.util.ArrayList;
import java.util.Collection;

// implement the PieceMovesCalculator
public class PawnMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) { // implementation is done in the subclasses
        Collection<ChessMove> validMoves = new ArrayList<>();

        