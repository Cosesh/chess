package chess;

import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves;
        int[][] directions = {{1,0}, {0,1}, {-1,0}, {0,-1}};
        PieceHelper helper = new PieceHelper();
        moves = helper.slideMoveLoop(directions, board, position);
        return moves;

    }


}
