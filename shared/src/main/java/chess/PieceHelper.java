package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceHelper {


    public Collection<ChessMove> stepMoveLoop(int[][] directions,
                                              ChessBoard board,
                                              ChessPosition position) {

        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int testRow;
        int testCol;
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

    public Collection<ChessMove> slideMoveLoop (int[][] directions,
                                                ChessBoard board,
                                                ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int testRow;
        int testCol;
        ChessMove newMove;
        ChessPosition checker;
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        ChessPiece pieceChecked;

        for (int[] direction : directions) {
            testRow = startRow;
            testCol = startCol;
            while (testCol >= 1 && testCol <= 8 && testRow >= 1 && testRow <= 8) {
                testRow = testRow + direction[0];
                testCol = testCol + direction[1];
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
                    } else if (pieceChecked.getTeamColor() == color) {
                        break;
                    }
                }
            }
        }
        return moves;
    }
}
