package chess;

import java.util.ArrayList;
import java.util.Collection;


public class KnightMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int testRow;
        int testCol;
        int[][] directions = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}};
        ChessMove newMove;
        ChessPosition checker;
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        ChessPiece pieceChecked;
        for (int i = 0; i < 8; i++){
            testRow = startRow + directions[i][0];
            testCol = startCol + directions[i][1];
            if(testCol >= 1 && testCol <= 8 && testRow >= 1 && testRow <= 8) {
                checker = new ChessPosition(testRow, testCol);
                pieceChecked = board.getPiece(checker);
                if (pieceChecked == null) {
                    newMove = new ChessMove(position, checker, null);
                    moves.add(newMove);
                } else if (pieceChecked.getTeamColor() != color) {
                    newMove = new ChessMove(position, checker, null);
                    moves.add(newMove);
                }
            }
        }
        return moves;
    }

}
