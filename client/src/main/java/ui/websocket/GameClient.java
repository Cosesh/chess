package ui.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameSqlDataAccess;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.JoinGamer;
import ui.ResponseException;
import ui.ServerFacade;
import ui.State;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_BG_COLOR;


public class GameClient implements NotificationHandler{
    private State state = State.IN_GAME;
    private final ServerFacade server;
    private final AuthData myauth;
    private final int iD;
    private GameData theGame;
    private final WebSocketFacade ws;
    private GameSqlDataAccess gDAO;

    public GameClient(String serverUrl, AuthData auth, int iD, GameData theGame) throws Exception {
        server = new ServerFacade(serverUrl);
        myauth = auth;
        gDAO = new GameSqlDataAccess();
        this.iD = iD;
        this.theGame = theGame;
        ws = new WebSocketFacade(serverUrl,this);
    }

    public void run() throws ResponseException {
        ws.connect(myauth.authToken(), iD );
        System.out.println( "You're in the game \n");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("you left the game")) {
            result = getResult(scanner, result);
        }
        System.out.println();
    }

    private String getResult(Scanner scanner, String result) {
        printPrompt();
        String line = scanner.nextLine();
        try {
            result = eval(line);
            System.out.print(result);
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(msg);
        }
        return result;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "move" -> makeMove(params);
                case "resign"-> {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Are you sure you want to resign? Type yes to confirm");
                    if (scanner.nextLine().equalsIgnoreCase("yes")){
                        yield resign();
                    } else{
                        yield "ok cool keep playing boss";
                    }

                }
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String highlight(String[] params) {
        String text = params[0];
        ChessPosition position = textToPosition(text);
        ChessGame game = theGame.game();
        var valid = game.validMoves(position);
        ChessBoard board = theGame.game().getBoard();
        var username = myauth.username();
        var toPrint = boardString(board);
        for(ChessMove move: valid) {
            var row = 8 - move.getEndPosition().getRow();
            var col = move.getEndPosition().getColumn() - 1;
            highlightBoardString(toPrint, row, col);

        }
        highlightBoardString(toPrint, 8 - position.getRow(), position.getColumn() -1);
        if(username.equals(theGame.blackUsername())) {
            String[][] reverse = new String[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    reverse[i][j] = toPrint[7-i][7-j];
                }
            } printBoardBlack(reverse);
        } else {
            printBoardWhite(toPrint);

        }


        return "";
    }

    private String[][] highlightBoardString(String[][] toPrint, int row, int col) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(i == row && j == col) {
                    toPrint[i][j] = SET_BG_COLOR_YELLOW + toPrint[i][j].replaceAll("\\u001B\\[[;\\d]*m", "") + RESET_BG_COLOR;
                }
            }

        }
        return toPrint;
    }

    private String resign() throws ResponseException {
        System.out.println();
        ws.resign(myauth.authToken(), iD);
        return "you resigned";
    }

    private String makeMove(String[] params) throws InvalidMoveException, DataAccessException, ResponseException {
        theGame = gDAO.getGame(iD);
        String startText = params[0];
        String endText = params[1];
        ChessMove move = textToMove(startText, endText);
        ws.makeMove(myauth.authToken(),iD,move);

        return "";
    }

    private ChessMove textToMove(String startText, String endText) {
        int startRow = startText.charAt(1) - '0';
        int startCol =  startText.charAt(0) - 'a' + 1;


        int endRow = endText.charAt(1) - '0';
        int endCol =  endText.charAt(0) - 'a' + 1;
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        return move;
    }

    private ChessPosition textToPosition(String text) {
        int startRow = text.charAt(1) - '0';
        int startCol =  text.charAt(0) - 'a' + 1;
        ChessPosition position = new ChessPosition(startRow, startCol);
        return position;
    }

    private String redraw() {
        System.out.println("\n");

        try{
            theGame = gDAO.getGame(iD);
            ChessBoard board = theGame.game().getBoard();
            var username = myauth.username();
            var toPrint = boardString(board);
            if(username.equals(theGame.blackUsername())) {
                String[][] reverse = new String[8][8];
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        reverse[i][j] = toPrint[7-i][7-j];
                    }
                } printBoardBlack(reverse);
            } else {
                printBoardWhite(toPrint);

            }
            return "";

        } catch (DataAccessException e) {
            throw new RuntimeException();
        }

    }


    private String[][] boardString(ChessBoard board) {
        String[][] boardString = new String[8][8];
        boolean isWhite = true;
        for (int i = 1; i <= 8 ; i++) {
            for (int j = 8; j >= 1; j--) {
                ChessPosition pos = new ChessPosition(i,j);
                var piece = board.getPiece(pos);
                int display = 8-i;
                if(piece == null){
                    if(isWhite){
                        boardString[display][j-1] = SET_BG_COLOR_WHITE + "   " + RESET_BG_COLOR;
                    } else {
                        boardString[display][j-1] = SET_BG_COLOR_BLACK + "   " + RESET_BG_COLOR;
                    }

                }else {
                    var type = piece.getPieceType();
                    var color = piece.getTeamColor();
                    var letter = "";
                    if(type.name().equals("KNIGHT")) {
                        letter = "N";
                    } else{
                        letter = type.name().substring(0,1);
                    }
                    setColors(isWhite, color, boardString, display + 1, j, letter);
                }
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
        return boardString;
    }

    private static void setColors(boolean isWhite, ChessGame.TeamColor color, String[][] boardString, int i, int j, String letter) {
        if(isWhite){
            if(color.equals(ChessGame.TeamColor.WHITE)){
                boardString[i - 1][j -1] = SET_BG_COLOR_WHITE + " " +  SET_TEXT_COLOR_RED + letter + " " + RESET_BG_COLOR;
            } else {
                boardString[i -1][j -1] = SET_BG_COLOR_WHITE + " " + SET_TEXT_COLOR_BLUE + letter + " "  + RESET_BG_COLOR;
            }
        } else {
            if(color.equals(ChessGame.TeamColor.WHITE)){
                boardString[i -1][j -1] = SET_BG_COLOR_BLACK  + " " + SET_TEXT_COLOR_RED + letter + " " + RESET_BG_COLOR;
            } else {
                boardString[i -1][j -1] = SET_BG_COLOR_BLACK + " " + SET_TEXT_COLOR_BLUE + letter + " " +  RESET_BG_COLOR;
            }
        }
    }

    /* "    h  g  f  e  d  c  b  a    "    "    a  b  c  d  e  f  g  h    "   */

    private void printBoardWhite(String[][] toPrint) {
        System.out.println( SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +"    a  b  c  d  e  f  g  h    " + RESET_TEXT_COLOR);
        for (int i = 0; i < toPrint.length; i++) {
            System.out.print(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +" " + (8-i) + " " + RESET_TEXT_COLOR);
            for (int j = 0; j <toPrint[i].length ; j++) {
                System.out.print(toPrint[i][j]);
            }
            System.out.print(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +" " + (8-i) + " " + RESET_TEXT_COLOR);
            System.out.println();
        }
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +"    a  b  c  d  e  f  g  h    " + RESET_TEXT_COLOR);
    }
    private void printBoardBlack(String[][] toPrint) {
        System.out.println( SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +"    h  g  f  e  d  c  b  a    " + RESET_TEXT_COLOR);
        for (int i = 0; i < toPrint.length; i++) {
            System.out.print(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +" " + (i+1) + " " + RESET_TEXT_COLOR);
            for (int j = 0; j <toPrint[i].length ; j++) {
                System.out.print(toPrint[i][j]);
            }
            System.out.print(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +" " + (i+1) + " " + RESET_TEXT_COLOR);
            System.out.println();
        }
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE +"    h  g  f  e  d  c  b  a    " + RESET_TEXT_COLOR);
    }


    @Override
    public void notify(NotificationMessage message) {
            System.out.println(message.getMessage());

    }

    @Override
    public void load(ServerMessage message) {
        redraw();

    }
    @Override
    public void error(ErrorMessage message) {
        System.out.println(message.getErrorMessage());

    }


    public String help() {
        return """
                - help
                - redraw
                - leave
                - move <starting position> <ending position>
                - resign
                - highlight <position>
                """;
    }
    public String leave() throws ResponseException {
        ws.leave(myauth.authToken(),iD);
        state = State.LOGGED_IN;
        return "you left the game";
    }



    private void printPrompt() {
        System.out.print("\n" + state  + " >>> " );
    }
}
