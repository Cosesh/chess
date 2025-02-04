package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor colorTurn;
    private ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {

        this.colorTurn = TeamColor.WHITE;
        gameBoard.resetBoard();


    }

    public ChessGame(ChessBoard board, ChessGame.TeamColor colorTurn){
        this.colorTurn = colorTurn;
        this.gameBoard = board;
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return colorTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        colorTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        ChessPiece pieceChecked = gameBoard.getPiece(startPosition);
        moves = pieceChecked.pieceMoves(gameBoard, startPosition);
        ChessBoard boardChecker;
        ChessGame gameChecker;

        for(ChessMove move : moves){
            boardChecker = getBoard();
            boardChecker.addPiece(move.getEndPosition(), pieceChecked);
            boardChecker.removePiece(startPosition);
            gameChecker = new ChessGame(boardChecker, getTeamTurn() );
            if(gameChecker.isInCheck(gameChecker.getTeamTurn())){
                validMoves.add(move);
            }

        }
        return validMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = gameBoard.getPiece(move.getStartPosition());
        if (pieceToMove.pieceMoves(gameBoard,move.getStartPosition()).contains(move)){
            gameBoard.addPiece(move.getEndPosition(),gameBoard.getPiece(move.getStartPosition()));
            gameBoard.removePiece(move.getStartPosition());
        } else{
            throw new InvalidMoveException("Invalid move");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSpot = new ChessPosition(0,0);
        ChessPosition spotToCheck;
        ChessPiece pieceToCheck;
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        for(int i = 1; i < 8; i++){
            for(int j = 1; j < 8; j++){
                spotToCheck = new ChessPosition(i,j);
                if(gameBoard.getPiece(spotToCheck) != null && gameBoard.getPiece(spotToCheck).getPieceType() == ChessPiece.PieceType.KING && gameBoard.getPiece(spotToCheck).getTeamColor() == teamColor){
                    kingSpot = spotToCheck;
                }
            }
        }

        for(int i = 1; i < 8; i++){
            for(int j = 1; j < 8; j++){
                spotToCheck = new ChessPosition(i,j);
                pieceToCheck = gameBoard.getPiece(spotToCheck);
                if(pieceToCheck != null && pieceToCheck.getTeamColor() != teamColor){
                    moves = pieceToCheck.pieceMoves(gameBoard, spotToCheck);
                    for(ChessMove move : moves) {
                        if(move.getEndPosition().getRow() == kingSpot.getRow() && move.getEndPosition().getColumn() == kingSpot.getColumn()){
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }


}
