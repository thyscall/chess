package chess;

import java.util.ArrayList;
import java.util.Collection;

// define a method that all the subclasses need to implement:
public interface PieceMoveCalculator {
    // method that is going to be called for all of its subclasses
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

}