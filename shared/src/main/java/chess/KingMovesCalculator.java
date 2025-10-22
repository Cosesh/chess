package chess;

import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves;
        PieceHelper helper = new PieceHelper();
        int[][] directions = {{1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}, {0,-1}};
        moves = helper.stepMoveLoop(directions, board, position);
        return moves;
    }
}
