package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        if(color == ChessGame.TeamColor.WHITE && startRow == 7){
            promotionLoop(board, position, startCol, moves, color, startRow+1);
        }
        else if(color == ChessGame.TeamColor.BLACK && startRow == 2){
            promotionLoop(board, position, startCol, moves, color, startRow-1);
        }
        else if(color == ChessGame.TeamColor.WHITE) {
            if (startRow == 2) {
                initialMove(board, position, startRow + 2,startRow + 1, startCol, moves);
            }
            normalMove(board, position, startCol, startRow + 1, moves, color);
        }
        else if(color == ChessGame.TeamColor.BLACK) {
            if (startRow == 7) {
                initialMove(board, position, startRow - 2,startRow - 1, startCol, moves);
            }
            normalMove(board, position, startCol, startRow - 1, moves, color);
        }
        return moves;
    }

    private static void normalMove(ChessBoard board, ChessPosition position, int startCol, int row, Collection<ChessMove> moves, ChessGame.TeamColor color) {
        ChessPiece pieceChecked;
        ChessPosition checker;
        ChessMove newMove;
        for (int i = -1; i < 2; i++) {
            if(startCol + i > 8 || startCol + i < 1) {continue;}
            checker = new ChessPosition(row, startCol + i);
            pieceChecked = board.getPiece(checker);
            if (i == 0) {
                if (pieceChecked == null) {
                    newMove = new ChessMove(position, checker, null);
                    moves.add(newMove);
                }
            } else if (pieceChecked != null && pieceChecked.getTeamColor() != color) {
                newMove = new ChessMove(position, checker, null);
                moves.add(newMove);
            }
        }
    }

    private static void initialMove(ChessBoard board, ChessPosition position, int row1, int row2, int startCol, Collection<ChessMove> moves) {
        ChessPosition checker2;
        ChessPiece pieceChecked;
        ChessPosition checker;
        ChessPiece pieceChecked2;
        ChessMove newMove;
        checker2 = new ChessPosition(row1, startCol);
        pieceChecked2 = board.getPiece(checker2);
        checker = new ChessPosition(row2, startCol);
        pieceChecked = board.getPiece(checker);
        if (pieceChecked2 == null && pieceChecked == null) {
            newMove = new ChessMove(position, checker2, null);
            moves.add(newMove);
        }
    }

    private static void promotionLoop(ChessBoard board, ChessPosition position,
                                      int startCol, Collection<ChessMove> moves,
                                      ChessGame.TeamColor color, int proRow) {
        ChessPosition checker;
        ChessPiece pieceChecked;
        for (int i = -1; i < 2; i++) {
            if(startCol + i > 8 || startCol + i <1) {continue;}
            checker = new ChessPosition(proRow, startCol + i);
            pieceChecked = board.getPiece(checker);
            if (i == 0) {
                if (pieceChecked == null) {
                    promotion(position, checker, moves);
                }
            } else if (pieceChecked != null && pieceChecked.getTeamColor() != color) {
                promotion(position, checker, moves);
            }
        }
    }

    private static void promotion(ChessPosition position, ChessPosition checker, Collection<ChessMove> moves) {

        ChessMove newMove = new ChessMove(position, checker, ChessPiece.PieceType.ROOK);
        moves.add(newMove);
        newMove = new ChessMove(position, checker, ChessPiece.PieceType.QUEEN);
        moves.add(newMove);
        newMove = new ChessMove(position, checker, ChessPiece.PieceType.BISHOP);
        moves.add(newMove);
        newMove = new ChessMove(position, checker, ChessPiece.PieceType.KNIGHT);
        moves.add(newMove);
    }
}