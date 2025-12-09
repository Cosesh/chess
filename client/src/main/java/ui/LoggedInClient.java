package ui;

import static ui.EscapeSequences.*;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import model.*;
import ui.websocket.GameClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class LoggedInClient {


    private State state = State.LOGGED_IN;
    private final ServerFacade server;
    private final AuthData myauth;
    private ArrayList<GameData> theGameList;

    public LoggedInClient(String serverUrl, AuthData auth)  {
        server = new ServerFacade(serverUrl);
        myauth = auth;
    }

    public void run() {
        System.out.println( " You're logged in big boy. type help for options");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
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
                case "logout" -> logout();
                case "create" -> create(params);
                case "join" -> joinGame(params);
                case "list" -> listGames();
                case "observe" -> observeGame(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String create(String... params) throws ResponseException {
        if (params.length !=1){
            throw new ResponseException(ResponseException.Code.ClientError,
                    "create requires 1 parameter " +
                            "\n create <gameName>");
        }
        GameName name = new GameName(params[0]);
        server.create(name, myauth);
        theGameList = server.listGames(myauth).games();
        return "That mamma jamma was low key created my guy";

    }

    public String listGames() throws ResponseException {

        theGameList = server.listGames(myauth).games();
        String games = "";
        if(theGameList.isEmpty()){
            System.out.println("There are no games");
        }
        for(int i = 1; i < theGameList.size() + 1; i++ ) {
            var spot = theGameList.get(i-1);
            games += "" + i + "| name: " + spot.gameName() + "| white username: " + spot.whiteUsername() +
                    "| black username: " + spot.blackUsername() + "\n";
        }
        return games;
    }

    public String joinGame(String... params) throws Exception {

        if (params.length !=2){
            throw new ResponseException(ResponseException.Code.ClientError,
                    "join requires 2 parameters " +
                            "\n join <desried color> <game iD>");
        }

        String color = params[0].toUpperCase();
        String iDString = params[1];
        if(!iDString.matches("\\d+")){
            System.out.println("game id must be a positive integer");
            return "";
        }
        var chosenID = Integer.parseInt(iDString) - 1;
        if(chosenID >= theGameList.size() || chosenID < 0) {
            System.out.println("that game id does not exist");
            return "";
        }

        var theGame = theGameList.get(chosenID);
        var iD = theGame.gameID();
        JoinGamer joiner = new JoinGamer(color, iD);
        server.joinGame(joiner, myauth);
        var gamed = new GameClient(server.getURL(), myauth, iD, theGame);
        gamed.run();

        return "";
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length !=1){
            throw new ResponseException(ResponseException.Code.ClientError,
                    "observe requires 1 parameter " +
                            "\nobserve <game iD>");
        }
        String iDString = params[0];
        if(!iDString.matches("\\d+")){
            System.out.println("game id must be a positive integer");
            return "";
        }
        var chosenID = Integer.parseInt(iDString) - 1;
        if(chosenID >= theGameList.size() || chosenID < 0) {
            System.out.println("that game id does not exist");
            return "";
        }
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var toPrint = boardString(board);
        printBoardWhite(toPrint);
        return "";
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


    public String help() {
        return """
                - help
                - logout
                - create <game name>
                - list
                - join <desired color> <game id>
                - observe <game id>
                """;
    }
    public String logout() {
        state = State.LOGGED_OUT;
        return "logout";
    }



    private void printPrompt() {
        System.out.print("\n" + state  + " >>> " );
    }
}
