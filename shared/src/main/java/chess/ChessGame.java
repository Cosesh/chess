package chess;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;



public class ChessGame {

    private ChessGame.TeamColor colorTurn;
    private ChessBoard gameBoard = new ChessBoard();
    private boolean isFinished;

    public ChessGame() {

        this.colorTurn = TeamColor.WHITE;
        gameBoard.resetBoard();
        this.isFinished = false;


    }

    public ChessGame(ChessBoard board, ChessGame.TeamColor colorTurn){
        this.colorTurn = colorTurn;
        this.gameBoard = board;
        this.isFinished = false;
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
        Collection<ChessMove> moves;
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece pieceChecked = gameBoard.getPiece(startPosition);
        moves = pieceChecked.pieceMoves(gameBoard, startPosition);
        ChessBoard boardChecker;
        ChessGame gameChecker;

        for(ChessMove move : moves){
            boardChecker = new ChessBoard(getBoard());
            boardChecker.addPiece(move.getEndPosition(), pieceChecked);
            boardChecker.removePiece(startPosition);
            gameChecker = new ChessGame(boardChecker, getTeamTurn() );
            if(!gameChecker.isInCheck(pieceChecked.getTeamColor())){
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
        if(pieceToMove == null){
            throw new InvalidMoveException("No piece there");
        }

        ChessGame.TeamColor pieceColor = pieceToMove.getTeamColor();

        if (pieceColor!= getTeamTurn()) {
            throw new InvalidMoveException("Not your turn");
        }

        if (validMoves(move.getStartPosition()).contains(move)){
            if (move.getPromotionPiece() != null){
                pieceToMove = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
            }

            gameBoard.addPiece(move.getEndPosition(),pieceToMove);
            gameBoard.removePiece(move.getStartPosition());

            if (pieceColor == TeamColor.BLACK){
                setTeamTurn(TeamColor.WHITE);
            } else{
                setTeamTurn(TeamColor.BLACK);
            }


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
        Collection<ChessMove> moves;
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                spotToCheck = new ChessPosition(i,j);
                if(gameBoard.getPiece(spotToCheck) != null &&
                        gameBoard.getPiece(spotToCheck).getPieceType() == ChessPiece.PieceType.KING &&
                        gameBoard.getPiece(spotToCheck).getTeamColor() == teamColor){
                    kingSpot = spotToCheck;
                    break;
                }
            }
        }

        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                spotToCheck = new ChessPosition(i,j);
                pieceToCheck = gameBoard.getPiece(spotToCheck);
                if(pieceToCheck == null || pieceToCheck.getTeamColor() == teamColor){continue;}
                moves = pieceToCheck.pieceMoves(gameBoard, spotToCheck);
                for(ChessMove move : moves) {
                    if(move.getEndPosition().getRow() == kingSpot.getRow() && move.getEndPosition().getColumn() == kingSpot.getColumn()){
                        return true;
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
        ChessPosition spotToCheck;
        ChessPiece pieceToCheck;
        boolean checkmate = true;
        if(isInCheck(teamColor)){
            for(int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8 ; j++) {
                    spotToCheck = new ChessPosition(i,j);
                    pieceToCheck = gameBoard.getPiece(spotToCheck);
                    if(pieceToCheck != null && pieceToCheck.getTeamColor() == teamColor && !validMoves(spotToCheck).isEmpty()){
                        checkmate = false;
                    }
                }

            }
        } else{checkmate = false;}

        return checkmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition spotToCheck;
        ChessPiece pieceToCheck;
        boolean stale = true;
        if(!isInCheck(teamColor)) {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    spotToCheck = new ChessPosition(i, j);
                    pieceToCheck = gameBoard.getPiece(spotToCheck);
                    if (pieceToCheck != null && pieceToCheck.getTeamColor() == teamColor && !validMoves(spotToCheck).isEmpty()) {
                        stale = false;
                    }
                }
            }
        }else{stale = false;}

        return stale;
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

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return colorTurn == chessGame.colorTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colorTurn, gameBoard);
    }


    public static ChessGame fromString (String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
