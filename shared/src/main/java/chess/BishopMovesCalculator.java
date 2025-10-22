package chess;

import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves;
        int[][] directions = {{1,-1}, {1,1}, {-1,1}, {-1,-1}};
        PieceHelper helper = new PieceHelper();
        moves = helper.slideMoveLoop(directions, board, position);
        return moves;
    }
}
