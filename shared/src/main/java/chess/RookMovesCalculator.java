package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int testRow = startRow;
        int testCol = startCol;
        int[][] directions = {{1,0}, {0,1}, {-1,0}, {0,-1}};
        ChessMove newMove;
        ChessPosition checker;
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        ChessPiece pieceChecked;
        for (int i = 0; i < 4; i++){
            testRow = startRow;
            testCol = startCol;
            while(testCol >= 1 && testCol <= 8 && testRow >= 1 && testRow <= 8) {
                testRow = testRow + directions[i][0];
                testCol = testCol + directions[i][1];
                if (testCol >= 1 && testCol <= 8 && testRow >= 1 && testRow <= 8) {
                    checker = new ChessPosition(testRow, testCol);
                    pieceChecked = board.getPiece(checker);
                    if (pieceChecked == null) {
                        newMove = new ChessMove(position, checker, null);
                        moves.add(newMove);
                    } else if (pieceChecked.getTeamColor() != color) {
                        newMove = new ChessMove(position, checker, null);
                        moves.add(newMove);
                        break;
                    } else if(pieceChecked.getTeamColor() == color ){
                        break;
                    }
                }
            }
        }
        return moves;
    }


}
