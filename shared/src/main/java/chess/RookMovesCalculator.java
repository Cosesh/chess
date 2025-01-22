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
        ChessMove newMove;
        ChessPosition checker;
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        ChessPiece pieceChecked;

        while(testRow < 8){
            testRow++;
            checker = new ChessPosition(testRow, testCol);
            pieceChecked = board.getPiece(checker);
            if(pieceChecked == null){
                newMove = new ChessMove(position, checker, null);
                moves.add(newMove);
            }
            else if(pieceChecked.getTeamColor() != color ){
                newMove = new ChessMove(position, checker, null);
                moves.add(newMove);
                break;
            }

            else if(pieceChecked.getTeamColor() == color ){
                break;
            }

        }
        testRow = startRow;

        while(testRow > 1){
            testRow--;
            checker = new ChessPosition(testRow, testCol);
            pieceChecked = board.getPiece(checker);
            if(pieceChecked == null){
                newMove = new ChessMove(position, checker, null);
                moves.add(newMove);
            }
            else if(pieceChecked.getTeamColor() != color ){
                newMove = new ChessMove(position, checker, null);
                moves.add(newMove);
                break;
            }

            else if(pieceChecked.getTeamColor() == color ){
                break;
            }


        }
        testRow = startRow;

        while(testCol < 8){
            testCol++;
            checker = new ChessPosition(testRow, testCol);
            pieceChecked = board.getPiece(checker);
            if(pieceChecked == null){
                newMove = new ChessMove(position, checker, null);
                moves.add(newMove);
            }
            else if(pieceChecked.getTeamColor() != color ){
                newMove = new ChessMove(position, checker, null);
                moves.add(newMove);
                break;
            }

            else if(pieceChecked.getTeamColor() == color ){
                break;
            }

        }
        testCol = startCol;

        while(testCol > 1) {
            testCol--;
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

        return moves;
    }


}
