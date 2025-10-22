package chess;

import java.util.Collection;


public class KnightMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves;
        PieceHelper helper = new PieceHelper();
        int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        moves = helper.stepMoveLoop(directions, board, position);
        return moves;
    }

}
