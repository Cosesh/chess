package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        ChessMove newMove;
        ChessPosition checker;
        ChessPosition checker2;
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        ChessPiece pieceChecked;
        ChessPiece pieceChecked2;
        if(color == ChessGame.TeamColor.WHITE && startRow == 7){
            for (int i = -1; i < 2; i++) {
                if(startCol + i <= 8 && startCol + i >=1) {
                    checker = new ChessPosition(8, startCol + i);
                    pieceChecked = board.getPiece(checker);
                    if (i == 0) {
                        if (pieceChecked == null) {
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.ROOK);
                            moves.add(newMove);
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.QUEEN);
                            moves.add(newMove);
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.BISHOP);
                            moves.add(newMove);
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.KNIGHT);
                            moves.add(newMove);
                        }
                    } else if (pieceChecked != null && pieceChecked.getTeamColor() != color) {
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.ROOK);
                        moves.add(newMove);
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.QUEEN);
                        moves.add(newMove);
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.BISHOP);
                        moves.add(newMove);
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.KNIGHT);
                        moves.add(newMove);
                    }
                }
            }
        }
        else if(color == ChessGame.TeamColor.BLACK && startRow == 2){
            for (int i = -1; i < 2; i++) {
                if(startCol + i <= 8 && startCol + i >=1) {
                    checker = new ChessPosition(1, startCol + i);
                    pieceChecked = board.getPiece(checker);
                    if (i == 0) {
                        if (pieceChecked == null) {
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.ROOK);
                            moves.add(newMove);
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.QUEEN);
                            moves.add(newMove);
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.BISHOP);
                            moves.add(newMove);
                            newMove = new ChessMove(position, checker, ChessPiece.PieceType.KNIGHT);
                            moves.add(newMove);
                        }
                    } else if (pieceChecked != null && pieceChecked.getTeamColor() != color) {
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.ROOK);
                        moves.add(newMove);
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.QUEEN);
                        moves.add(newMove);
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.BISHOP);
                        moves.add(newMove);
                        newMove = new ChessMove(position, checker, ChessPiece.PieceType.KNIGHT);
                        moves.add(newMove);
                    }
                }
            }
        }
        else if(color == ChessGame.TeamColor.WHITE) {
            if (startRow == 2) {
                checker2 = new ChessPosition(startRow + 2, startCol);
                pieceChecked2 = board.getPiece(checker2);
                checker = new ChessPosition(startRow + 1, startCol);
                pieceChecked = board.getPiece(checker);
                if (pieceChecked2 == null && pieceChecked == null) {
                    newMove = new ChessMove(position, checker2, null);
                    moves.add(newMove);
                }
            }
            for (int i = -1; i < 2; i++) {
                if(startCol + i <= 8 && startCol + i >=1) {
                    checker = new ChessPosition(startRow + 1, startCol + i);
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
        }

        else if(color == ChessGame.TeamColor.BLACK) {
            if (startRow == 7) {
                checker2 = new ChessPosition(startRow - 2, startCol);
                pieceChecked2 = board.getPiece(checker2);
                checker = new ChessPosition(startRow - 1, startCol);
                pieceChecked = board.getPiece(checker);
                if (pieceChecked2 == null && pieceChecked == null) {
                    newMove = new ChessMove(position, checker2, null);
                    moves.add(newMove);
                }
            }
            for (int i = -1; i < 2; i++) {
                if(startCol + i <= 8 && startCol + i >= 1) {
                    checker = new ChessPosition(startRow - 1, startCol + i);
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
        }
        return moves;
    }
}
